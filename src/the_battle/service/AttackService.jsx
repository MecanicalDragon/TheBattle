const appApi = DEPLOYED_URL;

//TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
export async function performAttack(pName, attacker, targets) {
    let url = new URL(appApi + 'attack/performAttack');
    url.search = new URLSearchParams({pName: pName});
    return fetch(url, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({attacker: attacker.toString().toUpperCase(), targets: targets})
    }).then(function (response) {
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        return resp;
    });
}