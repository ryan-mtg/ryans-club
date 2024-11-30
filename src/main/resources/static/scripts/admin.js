function showUpdateJson(updateId) {
    showElementById(`update-${updateId}-json`);
}

async function createInvite() {
    let response = await makePost('/api/admin/create_invite', {});

    if (response.ok) {
        const invite = await response.json();

        addInviteRow(invite);
    }
}

function addInviteRow(invite) {
    let table = document.getElementById('invite-table');

    let inviteRow = table.getElementsByTagName('tBody')[0].insertRow();
    addTextCell(inviteRow, invite.id);
    addTextCell(inviteRow, invite.state);
    addTextCell(inviteRow, invite.token);
    addTextCell(inviteRow, invite.creator.displayName);
    addTextCell(inviteRow, toCozyReadableDate(new Date(invite.creationTime)));
    addTextCell(inviteRow, invite.user?.displayName ?? '');
    addTextCell(inviteRow, invite.useTime ? toCozyReadableDate(new Date(invite.useTime)) : '');
}