const appApi = DEPLOYED_URL;

//TODO: TODO_SECURITY: requestParam 'name' should be removed in release
export async function battleBid(pName, squad) {
    let url = new URL(appApi + 'battle/startBattleBid');
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