const appApi = DEPLOYED_URL;
const sendCred = SEND_CREDENTIALS;

export async function getBattle() {
    let url = new URL(appApi + 'battle/getDislocations');
    return fetch(url, {credentials: sendCred}).then(function (response) {
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        return resp;
    });
}

export async function battleBid(squad) {
    let url = new URL(appApi + 'battle/registerBattleBid');
    return fetch(url, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }, credentials: sendCred,
        body: JSON.stringify(squad)
    }).then(function (response) {
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        return resp;
    });
}

export async function cancelBid() {
    let url = new URL(appApi + 'battle/cancelBid');
    return fetch(url, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }, credentials: sendCred
    }).then(function (response) {
        return response.status === 200 ? response.text() : null;
    }).then(resp => {
        return resp;
    });
}
