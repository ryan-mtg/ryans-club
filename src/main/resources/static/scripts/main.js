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
    addShips(log.ships);

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

function addShips(ships) {
    let container = document.getElementById('ships-container');
    removeAllChildren(container);
    for (let i = 0; i < ships.length; i++) {
        const ship = ships[i];
        let shipElement = document.createElement('section');
        shipElement.classList.add('ship-section');
        shipElement.classList.add(getShipRarityStyle(ship));

        shipElement.appendChild(createElement('h3', getPlayerName(ship)));

        if (ship.shipClass) {
            let shipImage = document.createElement('img');
            shipImage.classList.add('ship-icon');
            shipImage.src = ship.shipClass.imageUrl;

            shipElement.appendChild(shipImage);
        }

        shipElement.appendChild(createElement('div', getShipName(ship)));
        shipElement.appendChild(getShipSurvivalElement(ship));

        container.appendChild(shipElement);
    }
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