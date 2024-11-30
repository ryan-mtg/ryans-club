const DATE_FORMATTER = new Intl.DateTimeFormat('en-US', {month: 'short', day: 'numeric'});
const TIME_FORMATTER = new Intl.DateTimeFormat('en-US', {hour: 'numeric', minute: '2-digit'});

const MS_IN_SECOND = 1000;
const MS_IN_MINUTE = 60 * MS_IN_SECOND;
const MS_IN_HOUR = 60 * MS_IN_MINUTE;
const MS_IN_DAY = 24 * MS_IN_HOUR;

const S_IN_MINUTE = 60;
const S_IN_HOUR = 60 * S_IN_MINUTE;
const S_IN_DAY = 24 * S_IN_HOUR;

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

async function makeGet(endpoint, parameters) {
    let url = new URL(endpoint, location.origin);
    url.search = new URLSearchParams(parameters).toString();
    const settings = getGetSettings();

    const response = await fetch(url, settings);

    handleError(response);
    return response;
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
    return settings;
}

function getPostSettings(parameters) {
    let settings = getRequestSettings('POST');
    settings.body = JSON.stringify(parameters);
    return addSecurityField(settings);
}

function getGetSettings() {
    return getRequestSettings('GET');
}


function getFormDataPostSettings(formData) {
    let settings = {
        method: 'POST',
        body: formData,
        headers: {
            Accept: 'application/json'
        },
    };
    return addSecurityField(settings);
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

function setValue(elementId, value) {
    return document.getElementById(elementId).value = value;
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
    return textCell;
}

function createElement(tag, text, classes) {
    let element = document.createElement(tag);
    if (text) {
        element.innerText = text;
    }

    if (classes) {
        classes.forEach(c => element.classList.add(c));
    }

    return element;
}

function createDiv(text, classes) {
    return createElement('div', text, classes);
}

function createSpan(text, classes) {
    return createElement('span', text, classes);
}

function createImg(src, classes) {
    let img = createElement('img', null, classes);
    img.src = src;
    return img;
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
// Tab related functions
// ----------------------

function getPanel(tab) {
    return document.getElementById(tab.getAttribute('aria-controls'));
}

function deactivateTabs(tabList, exceptionTab) {
    for (let i = 0; i < tabList.children.length; i++) {
        let tab = tabList.children[i];
        if (tab == exceptionTab) {
            continue;
        }
        tab.setAttribute('aria-selected', false);
        tab.setAttribute('tabindex', -1);
        const panel = getPanel(tab);
        if (panel != null) {
            hideElement(panel);
        }
    }
}

async function selectTab(event) {
    let tab = event.target;
    while (tab && tab.getAttribute('role') != 'tab') {
        tab = tab.parentElement;
    }

    if (!tab) {
        return;
    }

    let panel = getPanel(tab);
    showElement(panel);
    let tabList = tab.parentElement;
    if (tabList.getAttribute('role') == 'tablist') {
        deactivateTabs(tabList, tab);
    }

    tab.removeAttribute('tabindex');
    tab.setAttribute('aria-selected', true);
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

function removeAllChildren(element) {
    element.textContent = '';
}

async function uploadLog() {
    const fileInput = document.getElementById('log-file-button');

    if (fileInput.files.length != 1) {
        return;
    }

    if (fileInput.files[0].size > FILE_SIZE_LIMIT) {
        showErrorMessage('File too large. 1MB limit!');
        return;
    }

    const userId = getInt('log-file-user-id');

    const formData = new FormData();
    formData.append('file', fileInput.files[0]);

    if (userId != 0) {
        formData.append('user', userId);
    }

    const response = await makeFormDataPost('/api/submit_log', formData);
    if (response.ok) {
        const log = await response.json();
        window.location.replace('/log/' + log.tag);
    }
}

function createDiagonalPattern(colors) {
    let shape = document.createElement('canvas');
    shape.width = 10;
    shape.height = 10;
    let c = shape.getContext('2d');
    c.fillStyle = colors.background;
    c.fillRect(0, 0, 10, 10);
    c.strokeStyle = colors.border;
    c.lineWidth = 3;
    c.beginPath();
    c.moveTo(2, 0);
    c.lineTo(10, 8);
    c.stroke();
    c.beginPath();
    c.moveTo(0, 8);
    c.lineTo(2, 10);
    c.stroke();
    return c.createPattern(shape, 'repeat');
}

function createHorizontalPattern(colors) {
    let shape = document.createElement('canvas');
    shape.width = 10;
    shape.height = 10;
    let c = shape.getContext('2d');
    c.fillStyle = colors.background;
    c.fillRect(0, 0, 10, 10);
    c.strokeStyle = colors.border;
    c.lineWidth = 2;
    c.beginPath();
    c.moveTo(0, 2);
    c.lineTo(10, 2);
    c.stroke();
    c.beginPath();
    c.moveTo(0, 7);
    c.lineTo(10, 7);
    c.stroke();
    return c.createPattern(shape, 'repeat');
}

function makeDataSet(label, data, colors) {
    return {
        label,
        data,
        backgroundColor: colors.background,
        borderColor: colors.border,
        borderWidth: 1
    };
}

function styleBackground(colors, style) {
    switch (style) {
        case 1:
            return colors.background;
        case 2:
            return createDiagonalPattern(colors);
        case 3:
            return createHorizontalPattern(colors);
    }
}

function makeStackedDataSet(stack, damageType, data, shipColors, style) {
    return {
        label: stack + ' - ' + damageType,
        data,
        backgroundColor: styleBackground(shipColors, style),
        borderColor: shipColors.border,
        borderWidth: 2,
        stack,
        damageType
    };
}

function makeChart(canvasId, type, labels, datasets, overrideLegend) {
    const canvas = document.getElementById(canvasId);
    if (canvas.chart) {
        canvas.chart.destroy();
    }

    const yellow = 'rgba(255, 255, 0, .7)';
    const lightYellow = 'rgba(255, 255, 224, .3)';

    const generateLabels = Chart.defaults.plugins.legend.labels.generateLabels;
    const onClick = Chart.defaults.plugins.legend.onClick;

    const generateStackedLabels = function(chart) {
        let oldLabels = generateLabels(chart);
        let stackState = chart.stackState || {};
        let seen = {};
        let stackLabels = [];
        let damageTypeLabels = [];
        oldLabels.forEach(label => {
            let meta = chart.getDatasetMeta(label.datasetIndex);
            let stack = meta.stack;
            let damageType = meta._dataset.damageType;

            if (!seen[stack]) {
                let newLabel = {...label};
                newLabel.text = stack;
                newLabel.rcType = 'stack';
                newLabel.rcValue = stack;
                stackLabels.push(newLabel);
                seen[stack] = true;
                stackState[stack] = stackState[stack] || {visibility: true, datasetIndices: []};
            }
            stackState[stack].datasetIndices.push(label.datasetIndex);

            if (!seen[damageType]) {
                let newLabel = {...label};
                newLabel.text = damageType;
                newLabel.rcType = 'damageType';
                newLabel.rcValue = damageType;
                damageTypeLabels.push(newLabel);
                seen[damageType] = true;
                stackState[damageType] = stackState[damageType] || {visibility: true, datasetIndices: []};
            }
            stackState[damageType].datasetIndices.push(label.datasetIndex);
        });

        chart.stackState = stackState;
        return stackLabels.concat(damageTypeLabels);
    }

    const stackedOnClick = function (click, legendItem, legend) {
        const value = legendItem.rcValue;
        let stackState = legend.chart.stackState;
        if (!value) {
            return onClick(click, legendItem, legend);
        }

        stackState[value].visibility = !stackState[value].visibility;
        legendItem.hidden = !stackState[value].visibility;

        stackState[value].datasetIndices.forEach(index => {
            const dataset = chart.data.datasets[index];
            const visible = stackState[dataset.stack].visibility && stackState[dataset.damageType].visibility;
            chart.setDatasetVisibility(index, visible);
        });
        chart.update();
    }

    let chart = new Chart(canvas, {
        type: type,
        data: {
            labels: labels,
            datasets: datasets
        },
        options: {
            plugins: {
                legend: {
                    onClick: overrideLegend ? stackedOnClick : onClick,
                    labels: {
                        generateLabels: overrideLegend ? generateStackedLabels : generateLabels,
                        color: yellow,
                    }
                },
                zoom: {
                    zoom: {
                        wheel: {
                            enabled: true
                        }
                    },
                    pan: {
                        enabled: true
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        color: lightYellow,
                    },
                    ticks: {
                        color: yellow,
                    }
                },
                y: {
                    grid: {
                        color: lightYellow,
                    },
                    ticks: {
                        callback: function(value, index, ticks) {
                            return scoplifyNumber(value);
                        },
                        color: yellow,
                    },
                    beginAtZero: true,
                }
            }
        }
    });
    canvas.chart = chart;
}

function formatPercent(value, digits = 2) {
    return (value * 100).toFixed(digits) + '%';
}

function scoplifyNumber(number) {
    if (number > 1_000_000_000_000) {
        return `${Math.floor(number / 1_000_000_000_000)}T`;
    }

    if (number > 1_000_000_000) {
        return `${Math.floor(number / 1_000_000_000)}B`;
    }

    if (number > 1_000_000) {
        return `${Math.floor(number / 1_000_000)}M`;
    }

    if (number > 1_000) {
        return `${Math.floor(number / 1_000)}K`;
    }

    if (number < 0) {
        return '-' + scoplifyNumber(-number);
    }

    return number;
}

function getRarityStyle(rarity) {
    const lowerRarity = rarity.toLowerCase();
    return `rarity-${lowerRarity}`;
}

async function registerUser() {
    const server = getInt('register-user-server');
    const handle = getValue('register-user-handle');
    const inviteToken = getValue('register-user-invite-token');
    let response = await makePost('/api/register_user', {server, handle, inviteToken});

    if (response.ok) {
        window.location = '/user';
    }
}

// ----------------------
// Selector Functions
// ----------------------

function removeSelected(elementId) {
    let element = document.getElementById(elementId);
    element.classList.remove('selected');
}

// ----------------------
// Profile Page Functions
// ----------------------

async function createSyncToken() {
    let response = await makePost('/api/create_sync_token', {});

    if (response.ok) {
        const syncToken = await response.json();

        hideElementById('create-sync-token-button');
        showElementById('copy-sync-token-button');
        setValue('sync-token', syncToken);

        let modIniExample = document.getElementById('mod-ini-example');
        const newText = modIniExample.innerText.replace("[sync-token]", syncToken)
        modIniExample.innerText = newText;
    }
}

function copySyncToken() {
    const token = getValue('sync-token');
    copyToClipboard(token, 'Sync Token copied to clipboard');
}

async function updateSpocksClubSyncToken() {
    const token = getValue('spocks-club-sync-token');
    let response = await makePost('/api/update_spocks_club_sync_token', {token});

    if (response.ok) {
        alert("updated!");
    }
}

function selectItem(newItemSelector, newItemType) {
    let selector = newItemSelector.parentElement;

    removeSelected(`item-${selector.dataset.item}-selector`);
    hideElementById(`item-${selector.dataset.item}-panel`);

    newItemSelector.classList.add('selected');
    showElementById(`item-${newItemType}-panel`);
    selector.dataset.item = newItemType;
}

function selectInventory(newGroupSelector) {
    let selector = newGroupSelector.parentElement;

    removeSelected(`inventory-${selector.dataset.group}-selector`);
    hideElementById(`inventory-${selector.dataset.group}-panel`);

    newGroupSelector.classList.add('selected');
    const newGroup = newGroupSelector.dataset.groupId;
    showElementById(`inventory-${newGroup}-panel`);
    selector.dataset.group = newGroup;
}

function selectDailyGroup(newGroupSelector) {
    let selector = newGroupSelector.parentElement;

    removeSelected(`daily-${selector.dataset.group}-selector`);
    hideElementById(`daily-${selector.dataset.group}-panel`);

    newGroupSelector.classList.add('selected');
    const newGroup = newGroupSelector.dataset.groupId;
    showElementById(`daily-${newGroup}-panel`);
    selector.dataset.group = newGroup;
}

async function selectDailyChest(newChestSelector) {
    let dailyBlock = getDailyBlock(newChestSelector);
    const dailyId = getDailyId(dailyBlock);
    let selector = newChestSelector.parentElement;

    const newChest = parseInt(newChestSelector.dataset.chest);

    const parameters = {daily: dailyId, chest: newChest};
    let response = await makePost('/api/update_daily', parameters);

    if (response.ok) {
        let dailyTask = await response.json();
        removeSelected(`daily-${dailyId}-chest-${selector.dataset.chest}`);

        newChestSelector.classList.add('selected');
        selector.dataset.chest = newChest;

        updateDailyTask(dailyBlock, dailyTask);
    }
}

function updateDailyTask(dailyBlock, dailyTask) {
    const dailyId = getDailyId(dailyBlock);

    let costs = document.getElementById(`daily-${dailyId}-costs`);
    costs.innerHTML = '';
    dailyTask.required.forEach(requirement => {
        const resource = requirement.resource;

        let requirementItem = createDiv(null, ['daily-task-item']);

        let img = createImg(resource.imageUrl, ['daily-resource', resource.style]);
        requirementItem.appendChild(img);

        requirementItem.appendChild(createDiv(resource.name));

        if (requirement.ready) {
            const redeemsText = `${requirement.redeems} Redeems`;
            requirementItem.appendChild(createDiv(redeemsText));
        } else {
            let unreadyDiv = createDiv();

            unreadyDiv.appendChild(createSpan(scoplifyNumber(requirement.stillNeeded), ['task-unready']));
            unreadyDiv.appendChild(document.createTextNode(' / '));
            unreadyDiv.appendChild(createSpan(scoplifyNumber(requirement.cost)));
            requirementItem.appendChild(unreadyDiv);
        }

        costs.appendChild(requirementItem);
    });

    let rewards = document.getElementById(`daily-${dailyId}-rewards`);
    rewards.innerHTML = '';
    dailyTask.selectedRewards.forEach(reward => {
        const resource = reward.resource;

        let rewardItem = createDiv(null, ['daily-task-item']);

        let img = createImg(resource.imageUrl, ['daily-resource', resource.style]);
        rewardItem.appendChild(img);

        rewardItem.appendChild(createDiv(reward.display));

        rewards.appendChild(rewardItem);
    });
}

function getDailyBlock(element) {
    while (!element.classList.contains('daily-task-block')) {
        element = element.parentElement;
    }

    return element;
}

function getDailyId(element) {
    return getDailyBlock(element).dataset.daily;
}

// ----------------------
// Crewing drag and drop
// ----------------------

function officerDragStart(event) {
    const officerId = event.target.dataset.officerId;
    event.dataTransfer.setData('text/plain', officerId);
    event.dataTransfer.dropEffect = 'move';
}

function officerDragEnd(event) {
    if (event.dataTransfer.dropEffect != 'none') {
        event.target.draggable = false;
    }
}

function officerDragOver(event) {
    event.preventDefault();
    event.dataTransfer.dropEffect = 'move';
}

function officerDrop(event) {
    event.preventDefault();
    let officerSlot = getContainerSlot(event.target);

    const newOfficerId = event.dataTransfer.getData('text/plain');

    if ('officerId' in officerSlot.dataset) {
        const oldOfficerId = officerSlot.dataset.officerId;
        let oldOfficerBlock = document.getElementById(`officer-${oldOfficerId}-block`);
        oldOfficerBlock.draggable = true;
    }
    officerSlot.innerHTML = '';

    officerSlot.dataset.officerId = newOfficerId;
    officerSlot.appendChild(createOfficerBlock(newOfficerId));
}

function getContainerSlot(element) {
    while (element) {
        if (element.classList.contains('bridge-slot') || element.classList.contains('officer-slot')) {
            return element;
        }
        element = element.parentNode;
    }
}

function createOfficerBlock(officerId) {
    let definitionBlock = document.getElementById(`officer-${officerId}-block`);

    let image = createElement('img', null, 'large-set-icon');
    image.src = definitionBlock.dataset.imageSrc;
    image.draggable = false;

    let officerDiv = createElement('div', null, 'crew-block');
    officerDiv.appendChild(image);
    officerDiv.classList.add(definitionBlock.dataset.style);
    return officerDiv;
}
