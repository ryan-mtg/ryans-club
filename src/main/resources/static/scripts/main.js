function hideElementById(elementId) {
    hideElement(document.getElementById(elementId));
}

function hideElement(element) {
    element.hidden = true;
}

function showElementById(elementId) {
    showElement(document.getElementById(elementId));
}

function showElement(element) {
    element.hidden = false;
}

async function makePost(endpoint, parameters) {
    const settings = getPostSettings(parameters);

    const response = await fetch(endpoint, settings);

    handleError(response);
    return response;
}

async function makeFormDataPost(endpoint, formData) {
    const settings = getFormDataPostSettings(formData);

    const response = await fetch(endpoint, settings);

    handleError(response);
    return response;
}

async function handleError(response) {
    if (!response.ok) {
        const json = await response.json();
        if (json.hasOwnProperty('message')) {
            showErrorMessage(json.message);
        }
    }
}

function addSecurityField(settings) {
    const security = document.getElementById('security-token').dataset;
    settings.headers[security.header] = security.token;
    settings.body = JSON.stringify(parameters);
    return settings;
}

function getPostSettings(parameters) {
    let settings = getRequestSettings('POST');
    addSecurityField(settings);
}

function getFormDataPostSettings(formData) {
    return {
        method: 'POST',
        body: formData,
        headers: {
            Accept: 'application/json'
        },
    };
}

function getRequestSettings(method) {
    return {
        method,
        headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json'
        },
    };
}

function showInfoMessage(message) {
    setText('info-message', message);
    showElementById('info-banner');
}

function showErrorMessage(message) {
    setText('error-message', message);
    const errorBanner = showElementById('error-banner');
    if (isSafari()) {
        errorBanner.scrollIntoView();
        document.body.style.transform = 'scale(1)';
    }
}

function dismissErrorMessage() {
    hideElementById('error-banner');
}

function dismissWarningMessage() {
    hideElementById('warning-banner');
}

function dismissInfoMessage() {
    hideElementById('info-banner');
}

function flashInfoMessage(message, waitInSeconds) {
    showInfoMessage(message);
    setTimeout(dismissInfoMessage, waitInSeconds * 1000);
}

function isSafari() {
    return /^((?!chrome|android).)*safari/i.test(navigator.userAgent);
}

function getValue(elementId) {
    return document.getElementById(elementId).value;
}

function getInt(elementId) {
    return parseInt(getValue(elementId));
}

function setText(elementId, text) {
    document.getElementById(elementId).innerText = text;
}

function addTextCell(row, text) {
    let textCell = row.insertCell();
    textCell.innerText = text;
}

function createElement(tag, text, classes) {
    let element = document.createElement(tag);
    element.innerText = text;

    if (classes) {
        element.classList.add(classes);
    }

    return element;
}

function getOrdinalSuffix(ordinal) {
    if (11 <= ordinal && ordinal <= 13) {
        return 'th';
    }
    switch (ordinal%10) {
        case 1:
            return 'st';
        case 2:
            return 'nd';
        case 3:
            return 'rd';
        default:
            return 'th';
    }
}

function pluralize(amount, thing) {
    if (amount == 1) {
        return `1 ${thing}`;
    }
    return `${amount} ${thing}s`;
}

const DATE_FORMATTER = new Intl.DateTimeFormat('en-US', {month: 'short', day: 'numeric'});
const TIME_FORMATTER = new Intl.DateTimeFormat('en-US', {hour: 'numeric', minute: '2-digit'});

const MS_IN_SECOND = 1000;
const MS_IN_MINUTE = 60 * MS_IN_SECOND;
const MS_IN_HOUR = 60 * MS_IN_MINUTE;
const MS_IN_DAY = 24 * MS_IN_HOUR;

const S_IN_MINUTE = 60;
const S_IN_HOUR = 60 * S_IN_MINUTE;
const S_IN_DAY = 24 * S_IN_HOUR;


function createLocalDate(timestamp) {
    return new Date(timestamp);
}

function getDay(date) {
    let day = new Date(date);
    day.setHours(0, 0, 0, 0);
    return day;
}

function getShortReadableTimestamp(time) {
    return DATE_FORMATTER.format(time) + getOrdinalSuffix(time.getDate()) + ' at ' + TIME_FORMATTER.format(time);
}

function toCozyReadableTimestamp(timestamp) {
    return toCozyReadableDate(new Date(timestamp * 1000));
}

function toCozyReadableDate(time) {
    const now = createLocalDate(Date.now());
    const ms_since_time = now.getTime() - time.getTime();
    if (ms_since_time >= 0) {
        if (ms_since_time > MS_IN_DAY) {
            const today = getDay(now);
            const yesterday = new Date(today);
            yesterday.setDate(today.getDate() - 1);
            const day = getDay(time);

            if (day.getTime() == yesterday.getTime()) {
                return 'yesterday at ' + TIME_FORMATTER.format(time);
            }

            return getShortReadableTimestamp(time);
        } else if (ms_since_time > MS_IN_HOUR) {
            const hours = Math.round(ms_since_time / MS_IN_HOUR);
            return pluralize(hours, 'hour') + ' ago';
        } else if (ms_since_time > MS_IN_MINUTE) {
            const minutes = Math.round(ms_since_time / MS_IN_MINUTE);
            return pluralize(minutes, 'minute') + ' ago';
        } else {
            return 'just now';
        }
    }

    const ms_to_time = - ms_since_time;

    if (ms_to_time > MS_IN_DAY) {
        const today = getDay(now);
        const tomorrow = new Date(today);
        tomorrow.setDate(today.getDate() + 1);
        const day = getDay(time);

        if (day.getTime() == tomorrow.getTime()) {
            return 'tomorrow at ' + TIME_FORMATTER.format(time);
        }

        return getShortReadableTimestamp(time);
    } else if (ms_to_time > MS_IN_HOUR) {
        const hours = Math.round(ms_to_time / MS_IN_HOUR);
        return 'in ' + pluralize(hours, 'hour');
    } else if (ms_to_time > MS_IN_MINUTE) {
        const minutes = Math.round(ms_to_time / MS_IN_MINUTE);
        return 'in ' + pluralize(minutes, 'minute');
    } else {
        const seconds = Math.round(ms_to_time / MS_IN_SECOND);
        return 'in ' + pluralize(seconds, 'second');
    }

    return getShortReadableTimestamp(date);
}

function copyToClipboard(value, message) {
    const WAIT_TIME = 3;
    if (navigator.clipboard && window.isSecureContext) {
        navigator.clipboard.writeText(value).then(function() {
            flashInfoMessage(message, WAIT_TIME);
        }, function() {
            showErrorMessage('Unable to write to clipboard.');
        });
    } else {
        let valueInput = document.createElement('textarea');
        valueInput.value = value;
        valueInput.style.position = 'fixed';
        valueInput.style.left = '-999999px';
        valueInput.style.top = '-999999px';
        document.body.appendChild(valueInput);
        valueInput.focus();
        valueInput.select();
        if (document.execCommand('copy')) {
            flashInfoMessage(message, WAIT_TIME);
        } else {
            showErrorMessage('Unable to write to clipboard.');
        }
        valueInput.remove();
    }
}

// ----------------------
// Form related functions
// ----------------------

function showForm(formName, focusId, hideOnEscapeFunction) {
    const modal = document.getElementById(formName + '-modal');
    previousFocusElement = document.activeElement;

    modal.style.display = 'block';
    if (focusId) {
        document.getElementById(formName + '-' + focusId).focus();
    }

    document.addEventListener('keydown', hideOnEscapeFunction);
}

function hideForm(formName, hideOnEscapeFunction) {
    document.getElementById(formName + '-modal').style.display = 'none';
    document.removeEventListener('keydown', hideOnEscapeFunction);
    previousFocusElement.focus();
}

// ----------------------
// Upload Functions
// ----------------------

const FILE_SIZE_LIMIT = 1 * 1024 * 1024;

async function uploadLog() {
    const fileInput = document.getElementById('log-file-button');

    if (fileInput.files.length != 1) {
        return;
    }

    if (fileInput.files[0].size > FILE_SIZE_LIMIT) {
        showErrorMessage('File too large. 1MB limit!');
        return;
    }

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    const response = await makeFormDataPost('api/public/submit_log', formData);
    if (response.ok) {
        const log = await response.json();
        updateLog(log);
    }
}

function updateLog(log) {
    updateLogSummary(log);
    addShips(log.ships, log.type == 'SOLO_ARMADA');
    makeGraphs(log);

    let logSection = document.getElementById('log');
    showElement(logSection);
    logSection.style.display = 'flex';
}

function updateLogSummary(log) {
    let summary = document.getElementById('log-summary');
    summary.classList.add(log.outcome ? 'victory' : 'defeat');
    summary.classList.remove(log.outcome ? 'defeat' : 'victory');

    document.getElementById('summary-title').innerText = getLogTitle(log);
    setText('outcome', log.outcome ? 'Victory' : 'Defeat');
    setText('location', log.location);
    setText('rounds', log.rounds);
    setText('time', toCozyReadableDate(new Date(log.time)));
    setText('lines', log.lines);
}

function getLogTitle(log) {
    switch (log.type) {
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

function removeAllChildren(element) {
    element.textContent = '';
}

function addShips(ships, isSolo) {
    let container = document.getElementById('ships-container');
    removeAllChildren(container);
    for (let i = 0; i < ships.length; i++) {
        const ship = ships[i];
        let shipElement = document.createElement('section');
        shipElement.classList.add('ship-section');
        shipElement.classList.add(getShipRarityStyle(ship));

        shipElement.appendChild(createElement('h3', isSolo ? getShipName(ship) : getPlayerName(ship)));

        if (ship.shipClass) {
            let shipImage = document.createElement('img');
            shipImage.classList.add('ship-icon');
            shipImage.src = ship.shipClass.imageUrl;

            shipElement.appendChild(shipImage);
        }

        shipElement.appendChild(createElement('div', isSolo ? getPlayerName(ship) : getShipName(ship)));
        shipElement.appendChild(getShipSurvivalElement(ship));
        shipElement.appendChild(getShieldDroppedElement(ship));
        shipElement.appendChild(getShipDamageSummary(ship))

        container.appendChild(shipElement);
    }
}

function makeGraphs(log) {
    let damageDealtData = [];
    let criticalDamageDealtData = [];
    let damageReceivedData = [];
    let criticalDamageReceivedData = [];
    let mitigationData = [];
    let piercingData = [];
    let shotsData = [];
    let labels = [];

    for (let round = 1; round <= log.rounds; round++) {
        labels.push(`Round ${round}`);
    }

    log.ships.forEach(ship => {
        const roundsData = ship.damageReport.rounds;

        let damageDealtPerRound = {mitigated: [], shield: [], hull: []};
        let criticalDamageDealtPerRound = {critical: [], regular: []};
        let damageReceivedPerRound = {mitigated: [], shield: [], hull: []};
        let criticalDamageReceivedPerRound = {critical: [], regular: []};
        let mitigationPerRound = [];
        let piercingPerRound = [];
        let shotsPerRound = [];
        for (let round = 1; round <= log.rounds; round++) {
            let mitigated = round < roundsData.length ? roundsData[round].dealt.total.mitigated : 0;
            damageDealtPerRound.mitigated.push(mitigated);

            let shield = round < roundsData.length ? roundsData[round].dealt.total.shield : 0;
            damageDealtPerRound.shield.push(shield);

            let hull = round < roundsData.length ? roundsData[round].dealt.total.hull : 0;
            damageDealtPerRound.hull.push(hull);

            let critical = round < roundsData.length ? roundsData[round].dealt.critical.total : 0;
            criticalDamageDealtPerRound.critical.push(critical);

            let regular = round < roundsData.length ? roundsData[round].dealt.regular.total : 0;
            criticalDamageDealtPerRound.regular.push(regular);

            mitigated = round < roundsData.length ? roundsData[round].received.total.mitigated : 0;
            damageReceivedPerRound.mitigated.push(mitigated);

            shield = round < roundsData.length ? roundsData[round].received.total.shield : 0;
            damageReceivedPerRound.shield.push(shield);

            hull = round < roundsData.length ? roundsData[round].received.total.hull : 0;
            damageReceivedPerRound.hull.push(hull);

            critical = round < roundsData.length ? roundsData[round].received.critical.total : 0;
            criticalDamageReceivedPerRound.critical.push(critical);

            regular = round < roundsData.length ? roundsData[round].received.regular.total : 0;
            criticalDamageReceivedPerRound.regular.push(regular);

            if (round < roundsData.length && roundsData[round].received.total.shots > 0) {
                let mitigation = roundsData[round].received.total.mitigation;
                mitigationPerRound.push(mitigation);
            } else {
                mitigationPerRound.push(null);
            }

            if (round < roundsData.length && roundsData[round].dealt.total.shots > 0) {
                let piercing = roundsData[round].dealt.total.piercing;
                piercingPerRound.push(piercing);
            } else {
                piercingPerRound.push(null);
            }

            let shots = round < roundsData.length ? roundsData[round].dealt.total.shots : 0;
            shotsPerRound.push(shots);
        }

        const name = getShipName(ship);

        damageDealtData.push(makeStackedDataSet(name + ' - mitigation', damageDealtPerRound.mitigated, name));
        damageDealtData.push(makeStackedDataSet(name + ' - shield', damageDealtPerRound.shield, name));
        damageDealtData.push(makeStackedDataSet(name + ' - hull', damageDealtPerRound.hull, name));
        criticalDamageDealtData.push(makeStackedDataSet(name + ' - critical', criticalDamageDealtPerRound.critical, name));
        criticalDamageDealtData.push(makeStackedDataSet(name + ' - regular', criticalDamageDealtPerRound.regular, name));

        damageReceivedData.push(makeStackedDataSet(name + ' - mitigation', damageReceivedPerRound.mitigated, name));
        damageReceivedData.push(makeStackedDataSet(name + ' - shield', damageReceivedPerRound.shield, name));
        damageReceivedData.push(makeStackedDataSet(name + ' - hull', damageDealtPerRound.hull, name));
        criticalDamageReceivedData.push(makeStackedDataSet(name + ' - critical', criticalDamageReceivedPerRound.critical, name));
        criticalDamageReceivedData.push(makeStackedDataSet(name + ' - regular', criticalDamageReceivedPerRound.regular, name));
        mitigationData.push(makeDataSet(name, mitigationPerRound));
        piercingData.push(makeDataSet(name, piercingPerRound));
        shotsData.push(makeDataSet(name, shotsPerRound));
    });

    makeChart('damage-dealt-canvas', 'bar', labels, damageDealtData);
    makeChart('critical-damage-dealt-canvas', 'bar', labels, criticalDamageDealtData);
    makeChart('damage-received-canvas', 'bar', labels, damageReceivedData);
    makeChart('critical-damage-received-canvas', 'bar', labels, criticalDamageReceivedData);
    makeChart('mitigation-canvas', 'line', labels, mitigationData);
    makeChart('piercing-canvas', 'line', labels, piercingData);
    makeChart('shots-canvas', 'bar', labels, shotsData);
}

function makeDataSet(label, data) {
    return {
        label,
        data,
        borderWidth: 1
    };
}

function makeStackedDataSet(label, data, stack) {
    return {
        label,
        data,
        borderWidth: 1,
        stack
    };
}

function makeChart(canvasId, type, labels, datasets) {
    const canvas = document.getElementById(canvasId);
    new Chart(canvas, {
        type: type,
        data: {
            labels: labels,
            datasets: datasets
        },
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

function getPlayerName(ship) {
    if (ship.shipIdentifier) {
        return ship.shipIdentifier.playerName;
    }
    return 'Hostile?';
}

function getShipName(ship) {
    if (ship.shipIdentifier) {
        return ship.shipIdentifier.shipName;
    }
    if (ship.shipClass) {
        return ship.shipClass.name
    }
    return 'Hostile?';
}

function getShipSurvivalElement(ship) {
    if (ship.survived) {
        return createElement('div', 'Survived', ['victory']);
    }

    return createElement('div', `Defeated in ${ship.roundsSurvived} rounds`, ['defeat']);
}

function getShieldDroppedElement(ship) {
    if (ship.shieldDropped) {
        return createElement('div', `Shield dropped in round ${ship.roundShieldDropped}`, ['defeat']);
    }

    return createElement('div', 'Shields held', ['victory']);
}

function scoplifyNumber(number) {
    if (number > 1_000_000) {
        return `${Math.floor(number / 1_000_000)}M`;
    }

    if (number > 1_000) {
        return `${Math.floor(number / 1_000)}K`;
    }

    return number;
}

function getShipDamageSummary(ship) {
    let damageReport = ship.damageReport;

    let table = document.createElement('table');
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

function getRarityStyle(rarity) {
    const lowerRarity = rarity.toLowerCase();
    return `rarity-${lowerRarity}`;
}

function getShipRarityStyle(ship) {
    if (ship.shipClass) {
        return getRarityStyle(ship.shipClass.rarity);
    }
    return 'rarity-common';
}