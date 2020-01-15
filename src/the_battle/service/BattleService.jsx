const appApi = DEPLOYED_URL;

//TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
export async function getBattle(pName) {
    let url = new URL(appApi + 'battle/getDislocations');
    url.search = new URLSearchParams({pName: pName});
    return fetch(url).then(function (response) {
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        return resp;
    });
}

//TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
export async function battleBid(pName, squad) {
    let url = new URL(appApi + 'battle/registerBattleBid');
    url.search = new URLSearchParams({pName: pName});
    return fetch(url, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(squad)
    }).then(function (response) {
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        return resp;
    });
}

//TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
export async function cancelBid(pName) {
    let url = new URL(appApi + 'battle/cancelBid');
    url.search = new URLSearchParams({pName: pName});
    return fetch(url, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    }).then(function (response) {
        return response.status === 200 ? response.text() : null;
    }).then(resp => {
        return resp;
    });
}

//TODO: delete after tests
export async function test(pName) {
    let url = new URL(appApi + 'battle/test');
    url.search = new URLSearchParams({pName: pName});
    return fetch(url, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    }).then(function (response) {
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        return resp;
    });
}
