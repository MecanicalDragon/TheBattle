const appApi = DEPLOYED_URL;
const sendCred = SEND_CREDENTIALS;

export async function performAction(actor, action, data) {
    let url = new URL(appApi + 'action/performAction');
    url.search = new URLSearchParams({pName: pName});
    let simpleAction = {
        actor: actor.toString().toUpperCase(),
        action: action,
        additionalData: data || {}
    };
    return fetch(url, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        credentials: sendCred,
        body: JSON.stringify(simpleAction)
    }).then(function (response) {
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        return resp;
    });
}