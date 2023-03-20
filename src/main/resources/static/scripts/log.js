async function initializeLogs() {
    const tagElement = document.getElementById('log-tag');

    if (tagElement != null) {
        const tag = getValue('log-tag');
        initializeLog(tag);
    }
}

initializeLogs();

async function initializeLog(tag) {
    const response = makeGet('/api/log', {tag});
    response.then((response) => response.json().then(updateLog));

    document.querySelectorAll('.share-log-button')
        .forEach(button => button.onclick = copyLogLink);
}

async function copyLogLink() {
    const tag = getValue('log-tag');
    let url = new URL(window.location.href);
    url.search = '';
    url.pathname = 'log/' + tag;

    copyToClipboard(url.toString(), 'Link copied');
}

function updateLog(log) {
    new LogUpdater(log).update();
}

class LogUpdater {
    #log;
    #shipManager;
    #isSolo;

    constructor(log) {
        this.#log = log;
        this.#isSolo = this.#log.type == 'SOLO_ARMADA';
        this.#shipManager = new ShipManager(this.#isSolo);
    }

    update() {
        this.#updateSummary();
        this.#addShips();
        this.#addGraphs();
        this.#addEvents();

        let logSection = document.getElementById('log');
        showElement(logSection);
        logSection.style.display = 'flex';
    }

    #updateSummary() {
        let summary = document.getElementById('log-summary');
        summary.classList.add(this.#log.outcome ? 'victory' : 'defeat');
        summary.classList.remove(this.#log.outcome ? 'defeat' : 'victory');

        setText('summary-title', this.#getLogTitle());
        setValue('log-tag', this.#log.tag);
        setText('outcome', this.#log.outcome ? 'Victory' : 'Defeat');
        setText('location', this.#log.location);
        setText('rounds', this.#log.rounds);
        setText('time', toCozyReadableDate(new Date(this.#log.time)));
    }

    #addShips() {
        let container = document.getElementById('ships-container');

        let shipFactory = new ShipSectionFactory();

        for (let i = 0; i < this.#log.ships.length; i++) {
            const ship = this.#log.ships[i];
            this.#shipManager.addShip(ship);

            const shipElement = shipFactory.createSection(ship);
            container.appendChild(shipElement);
        }
    }

    #addGraphs() {
        let graphFactory = new GraphFactory();
        graphFactory.createGraphs(this.#log.rounds, this.#log.ships)
    }

    #addEvents() {
        let shipNamer = this.#shipManager.createShipNamer();
        new EventUpdater(shipNamer).update(this.#log.rounds, this.#log.events);
    }

    #getLogTitle() {
        switch (this.#log.type) {
            case 'HOSTILE':
                return 'Hostile Battle';
            case 'PVP':
                return 'PvP Battle';
            case 'ARMADA':
                return 'Armada Attack';
            case 'SOLO_ARMADA':
                return 'Solo Armada Attack';
            case 'STARBASE_ASSAULT':
                return 'Starbase Assault';
        }
        return 'Battle Log';
    }
}

const SHIP_COLORS = [
    {background: 'rgba(256, 156, 156, .8)', border: 'red'},
    {background: 'rgba(156, 156, 256, .8)', border: 'blue'},
    {background: 'rgba(156, 256, 156, .6)', border: 'green'},
    {background: 'white', border: 'yellow'},
    {background: 'mauve', border: 'purple'},
    {background: 'desert', border: 'orange'},
    {background: 'pink', border: 'pink'},
    {background: 'grey', border: 'black'},
];

class ShipManager {
    #ships = [];
    #isSolo;

    constructor(isSolo) {
        this.#isSolo = isSolo;
    }

    addShip(ship) {
        this.#computeHandles(ship);

        const index = this.#ships.length;
        this.#ships.push(ship);
        ship.index = index;
        ship.color = SHIP_COLORS[index];
    }

    createShipNamer() {
        return {
            nameShip: function (shipIdentifier) {
                return shipIdentifier.shipName;
            }
        };
    }

    #computeHandles(ship) {
        if (this.#isSolo) {
            ship.handle = this.#getShipName(ship);
            ship.subHandle = this.#getPlayerName(ship);
        } else {
            ship.handle = this.#getPlayerName(ship);
            ship.subHandle = this.#getShipName(ship);
        }
    }

    #getPlayerName(ship) {
        if (ship.shipIdentifier) {
            return ship.shipIdentifier.playerName;
        }
        return 'Hostile?';
    }

    #getShipName(ship) {
        if (ship.shipIdentifier) {
            return ship.shipIdentifier.shipName;
        }
        if (ship.shipClass) {
            return ship.shipClass.name
        }
        return 'Hostile?';
    }
}

const ROUNDS_PER_PAGE = 5;

class EventController {
    #eventUpdater;
    #pages;
    #currentPage;

    constructor(eventUpdater, rounds, events) {
        this.#eventUpdater = eventUpdater;
        this.#pages = [];
        this.#currentPage = 0;

        const pageCount = Math.floor((rounds + 4) / ROUNDS_PER_PAGE);
        for (let i = 0; i < pageCount; i++) {
            this.#pages.push([]);
        }

        events.forEach(event => {
            const page = Math.floor((event.round - 1) / ROUNDS_PER_PAGE);
            this.#pages[page].push(event);
        });
    }

    get pageCount() {
        return this.#pages.length;
    }

    get currentPage() {
        return this.#currentPage;
    }

    get currentEvents() {
        return this.#pages[this.#currentPage];
    }

    incrementPage() {
        if (this.#currentPage + 1 < this.#pages.length) {
            this.#currentPage++;
            this.#eventUpdater.repage(this);
        }
    }

    decrementPage() {
        if (0 < this.#currentPage) {
            this.#currentPage--;
            this.#eventUpdater.repage(this);
        }
    }

    setPage(page) {
        if (page != this.#currentPage) {
            this.#currentPage = page;
            this.#eventUpdater.repage(this);
        }
    }
}

class EventUpdater {
    #shipNamer;
    #tableBody;

    constructor(shipNamer) {
        this.#shipNamer = shipNamer;
    }

    update(rounds, events) {
        this.repage(new EventController(this, rounds, events));
    }

    repage(eventController) {
        this.#setEvents(eventController.currentEvents);
        this.#createPagination(eventController);
    }

    #setEvents(events) {
        let eventsTable = document.getElementById('events-table');
        eventsTable.createTBody();

        this.#tableBody = document.createElement('tBody');
        for (let i = 0; i < events.length; i++) {
            this.#createEventRow(events[i]);
        }

        eventsTable.replaceChild(this.#tableBody, eventsTable.tBodies[0]);
    }

    #createPagination(eventController) {
        let eventsSection = document.getElementsByClassName('events-section').item(0);
        let paginations = eventsSection.getElementsByClassName('pagination');

        for (let i = 0; i < paginations.length; i++) {
            let pagination = paginations[i];
            this.#removePages(pagination);

            let previousPageLink = pagination.getElementsByClassName('previous-page').item(0);
            previousPageLink.onclick = function () {
                eventController.decrementPage();
            }

            let nextPageLink = pagination.getElementsByClassName('next-page').item(0);
            nextPageLink.onclick = function () {
                eventController.incrementPage();
            }

            for (let page = 0; page < eventController.pageCount; page++) {
                let pageLink = document.createElement('span');
                pageLink.innerText = page + 1;
                pageLink.classList.add('page');
                if (page == eventController.currentPage) {
                    pageLink.classList.add('current');
                }
                pageLink.onclick = function () {
                    eventController.setPage(page);
                }
                pagination.insertBefore(pageLink, nextPageLink);
            }
        }
    }

    #removePages(pagination) {
        let removals = [];
        pagination.childNodes.forEach(child => {
            if (child.classList.contains('page')) {
                removals.push(child);
            }
        });
        removals.forEach(removal => removal.remove());
    }

    #createEventRow(event) {
        let row = this.#tableBody.insertRow();

        addTextCell(row, event.round);
        switch (event.type) {
            case 'Attack Event':
                return this.#createAttackEventRow(row, event);
            case 'Charging Weapon Event':
                return this.#createChargingWeaponEventRow(row, event);
            case 'Officer Proc Event':
                return this.#createOfficerProcEventRow(row, event);
            case 'Shield Depleted Event':
                return this.#createShieldDepletedEventRow(row, event);
            case 'Ship Destroyed Event':
                return this.#createShipDestroyedEventRow(row, event);
            default:
                console.log('unknown event type: ' + event.type);
        }
    }

    #createAttackEventRow(row, event) {
        addTextCell(row, this.#nameShip(event.attacker));
        addTextCell(row, 'Attacks');
        addTextCell(row, this.#nameShip(event.defender));
        addTextCell(row, scoplifyNumber(event.damage.total) + ' damage');
    }

    #createChargingWeaponEventRow(row, event) {
        addTextCell(row, this.#nameShip(event.ship));
        addTextCell(row, 'Charging Weapon');
        addTextCell(row, event.charge + '%');
    }

    #createOfficerProcEventRow(row, event) {
        addTextCell(row, this.#nameShip(event.ship));
        addTextCell(row, 'Officer Ability');
        addTextCell(row, event.abilityOwnerName);
        addTextCell(row, event.abilityName);
    }

    #createShieldDepletedEventRow(row, event) {
        addTextCell(row, this.#nameShip(event.ship));
        let cell = addTextCell(row, 'Shield depleted');
        cell.classList.add('defeat');
    }

    #createShipDestroyedEventRow(row, event) {
        addTextCell(row, this.#nameShip(event.ship));
        let cell = addTextCell(row, 'Ship destroyed');
        cell.classList.add('defeat');
    }

    #nameShip(shipIdentifier) {
        return this.#shipNamer.nameShip(shipIdentifier);
    }
}


const MITIGATED = 'mitigated';
const SHIELD = 'shield';
const HULL = 'hull';
const CRITICAL = 'critical';
const REGULAR = 'regular';

class GraphFactory {
    #composers;

    constructor() {
        this.#composers = [
            new SimpleGraphComposer('damage-dealt', 'bar', round => round.dealt.total.total),
            new StackedGraphComposer('mitigation-damage-dealt', 'bar', [
                new StackDescriptor(HULL, 1, round => round.dealt.total.hull),
                new StackDescriptor(SHIELD, 2, round => round.dealt.total.shield),
                new StackDescriptor(MITIGATED, 3, round => round.dealt.total.mitigated)
            ]),
            new StackedGraphComposer('critical-damage-dealt', 'bar', [
                new StackDescriptor(REGULAR, 1, round => round.dealt.critical.total),
                new StackDescriptor(CRITICAL, 2, round => round.dealt.regular.total),
            ]),
            new SimpleGraphComposer('damage-received', 'bar', round => round.received.total.total),
            new StackedGraphComposer('mitigation-damage-received', 'bar', [
                new StackDescriptor(HULL, 1, round => round.received.total.hull),
                new StackDescriptor(SHIELD, 2, round => round.received.total.shield),
                new StackDescriptor(MITIGATED, 3, round => round.received.total.mitigated)
            ]),
            new StackedGraphComposer('critical-damage-received', 'bar', [
                new StackDescriptor(REGULAR, 1, round => round.received.critical.total),
                new StackDescriptor(CRITICAL, 2, round => round.received.regular.total),
            ]),
            new ConditionalGraphComposer('mitigation', 'line', round => round.received.total.shots > 0,
                    round => round.received.total.mitigation),
            new ConditionalGraphComposer('piercing', 'line', round => round.dealt.total.shots > 0,
                    round => round.dealt.total.piercing),
            new SimpleGraphComposer('shots', 'bar', round => round.dealt.total.total),
        ];
    }

    createGraphs(rounds, ships) {
        let labels = this.#createLabels(rounds);

        let dataSets = {};
        this.#composers.forEach(composer => dataSets[composer.label] = []);

        ships.forEach(ship => {
            const roundsData = ship.damageReport.rounds;

            let dataSet = {};
            this.#composers.forEach(composer => dataSet[composer.label] = composer.createDataSet());

            for (let round = 1; round <= rounds; round++) {
                this.#composers.forEach(composer => {
                    composer.addRoundData(dataSet[composer.label], round < roundsData.length ? roundsData[round] : null);
                });
            }

            this.#composers.forEach(composer => {
                dataSets[composer.label].push(...composer.completeDataSet(dataSet[composer.label], ship.handle, ship.color));
            });
        });

        this.#composers.forEach(composer => makeChart(composer.label + '-canvas', composer.type, labels, dataSets[composer.label], composer.overrideLegend));
    }

    #createLabels(rounds) {
        let labels = [];
        for (let round = 1; round <= rounds; round++) {
            labels.push(`Round ${round}`);
        }
        return labels;
    }
}

class GraphComposer {
    #label;
    #type;

    constructor(label, type) {
        this.#label = label;
        this.#type = type;
    }

    get overrideLegend() {
        return false;
    }

    get label() {
        return this.#label;
    }

    get type() {
        return this.#type;
    }

    createDataSet() {
        return [];
    }

    completeDataSet(dataSet, name, color) {
        return [makeDataSet(name, dataSet, color)];
    }
}

class SimpleGraphComposer extends GraphComposer {
    #roundCallback;

    constructor(label, type, roundCallback) {
        super(label, type);
        this.#roundCallback = roundCallback;
    }

    addRoundData(dataSet, roundData) {
        let value = roundData ? this.#roundCallback(roundData) : 0;
        dataSet.push(value);
    }
}

class ConditionalGraphComposer extends GraphComposer {
    #conditionCallback;
    #roundCallback;

    constructor(label, type, conditionCallback, roundCallback) {
        super(label, type);
        this.#conditionCallback = conditionCallback;
        this.#roundCallback = roundCallback;
    }

    addRoundData(dataSet, roundData) {
        let value = roundData && this.#conditionCallback(roundData) ? this.#roundCallback(roundData) : null;
        dataSet.push(value);
    }
}

class StackDescriptor {
    #label;
    #style;
    #roundCallback;

    constructor(label, style, roundCallback) {
        this.#label = label;
        this.#style = style;
        this.#roundCallback = roundCallback;
    }

    get label() {
        return this.#label;
    }

    get style() {
        return this.#style;
    }

    getRoundValue(roundData) {
        return this.#roundCallback(roundData);
    }
}

class StackedGraphComposer extends GraphComposer {
    #stackDescriptors;

    constructor(label, type, stackDescriptors) {
        super(label, type);
        this.#stackDescriptors = stackDescriptors;
    }

    get overrideLegend() {
        return true;
    }

    createDataSet() {
        let dataSet = {};
        this.#stackDescriptors.forEach(descriptor => dataSet[descriptor.label] = []);
        return dataSet;
    }

    addRoundData(dataSet, roundData) {
        this.#stackDescriptors.forEach(descriptor => {
            let value = roundData ? descriptor.getRoundValue(roundData) : 0;
            dataSet[descriptor.label].push(value);
        });
    }

    completeDataSet(dataSet, name, color) {
        return this.#stackDescriptors.map(descriptor => {
            return makeStackedDataSet(name, descriptor.label, dataSet[descriptor.label], color, descriptor.style);
        });
    }
}

class ShipSectionFactory {
    constructor() {}

    createSection(ship) {
        let shipElement = document.createElement('section');
        shipElement.classList.add('ship-section');
        shipElement.classList.add(this.#getShipRarityStyle(ship));

        shipElement.appendChild(createElement('h3', ship.handle));
        shipElement.appendChild(createElement('div', ship.subHandle));

        if (ship.shipClass) {
            let shipImage = document.createElement('img');
            shipImage.classList.add('ship-icon');
            shipImage.src = ship.shipClass.imageUrl;

            shipElement.appendChild(shipImage);
        }

        shipElement.appendChild(this.#getShipSurvivalElement(ship));
        shipElement.appendChild(this.#getShieldDroppedElement(ship));
        shipElement.appendChild(this.#getAverageMitigationElement(ship));
        shipElement.appendChild(this.#getAveragePiercingElement(ship));
        shipElement.appendChild(this.#getShipOfficerSummary(ship));
        shipElement.appendChild(this.#getShipDamageSummary(ship));
        return shipElement;
    }

    #getShipSurvivalElement(ship) {
        if (ship.survived) {
            return createElement('div', 'Survived', ['victory']);
        }

        let survivalElement = createElement('div');
        survivalElement.appendChild(document.createTextNode('Defeated in '));
        survivalElement.appendChild(createElement('span', `${ship.roundsSurvived}`, ['defeat']));
        survivalElement.appendChild(document.createTextNode(' rounds'));

        return survivalElement;
    }

    #getShieldDroppedElement(ship) {
        if (ship.shieldDropped) {
            let shieldElement = createElement('div');
            shieldElement.appendChild(document.createTextNode('Shield dropped in round '));
            shieldElement.appendChild(createElement('span', `${ship.roundShieldDropped}`, ['defeat']));

            return shieldElement;
        }

        return createElement('div', 'Shields held', ['victory']);
    }

    #getAverageMitigationElement(ship) {
        const mitigation = formatPercent(ship.damageReport.received.total.mitigation);
        return createElement('div', `Average Mitigation: ${mitigation}`);
    }

    #getAveragePiercingElement(ship) {
        const piercing = formatPercent(ship.damageReport.dealt.total.piercing);
        return createElement('div', `Average Piercing: ${piercing}`);
    }

    #getShipOfficerSummary(ship) {
        const counts = ship.officerReport.counts;
        if (counts.length == 0) {
            return document.createTextNode('');
        }

        let table = document.createElement('table');
        table.classList.add('data-table');
        let headerRow = table.createTHead().insertRow();
        addTextCell(headerRow, 'Officer');
        addTextCell(headerRow, 'Ability');
        addTextCell(headerRow, 'Procs');

        let tableBody = table.createTBody();
        for (let i = 0; i < counts.length; i++) {
            const officerCount = counts[i];
            let row = tableBody.insertRow();
            let nameCell = row.insertCell();

            let officerImage = document.createElement('img');
            officerImage.src = officerCount.officer.imageUrl;
            officerImage.alt = officerImage.title = officerCount.officer.name;
            officerImage.classList.add('officer-icon');
            nameCell.appendChild(officerImage);
            nameCell.classList.add('center-cell');

            addTextCell(row, officerCount.ability);
            addTextCell(row, officerCount.count);
        }

        return table;
    }

    #getShipDamageSummary(ship) {
        let damageReport = ship.damageReport;

        let table = document.createElement('table');
        table.classList.add('data-table');
        let headerRow = table.createTHead().insertRow();
        addTextCell(headerRow, 'Damage');
        addTextCell(headerRow, 'Dealt');
        addTextCell(headerRow, 'Received');

        let tableBody = table.createTBody();
        let totalRow = tableBody.insertRow();
        addTextCell(totalRow, 'Total');
        addTextCell(totalRow, scoplifyNumber(damageReport.dealt.total.total));
        addTextCell(totalRow, scoplifyNumber(damageReport.received.total.total));

        let mitigatedRow = tableBody.insertRow();
        addTextCell(mitigatedRow, 'Mitigated');
        addTextCell(mitigatedRow, scoplifyNumber(damageReport.dealt.total.mitigated));
        addTextCell(mitigatedRow, scoplifyNumber(damageReport.received.total.mitigated));

        let shieldRow = tableBody.insertRow();
        addTextCell(shieldRow, 'Shield');
        addTextCell(shieldRow, scoplifyNumber(damageReport.dealt.total.shield));
        addTextCell(shieldRow, scoplifyNumber(damageReport.received.total.shield));

        let hullRow = tableBody.insertRow();
        addTextCell(hullRow, 'Hull');
        addTextCell(hullRow, scoplifyNumber(damageReport.dealt.total.hull));
        addTextCell(hullRow, scoplifyNumber(damageReport.received.total.hull));

        return table;
    }

    #getShipRarityStyle(ship) {
        if (ship.shipClass) {
            return getRarityStyle(ship.shipClass.rarity);
        }
        return 'rarity-common';
    }
}