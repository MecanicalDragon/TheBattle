const appApi = DEPLOYED_URL;
const sendCred = SEND_CREDENTIALS;

export async function getPool() {
    return fetch(appApi + 'squad/getPool', {
        credentials: sendCred
    }).then(function (response) {
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        let pool = new Map();
        resp.forEach(function (entry) {
            pool.set(entry.id, entry);
        });
        return pool;
    });
}

export async function retireHero(unit) {
    let url = new URL(appApi + 'squad/retireHero');
    url.search = new URLSearchParams({unit: unit});
    return fetch(url, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        credentials: sendCred
    }).then(function (response) {
        return response.status === 200 ? response.text() : null;
    }).then(resp => {
        return resp;
    });
}

export async function addNewHero(name, type) {
    let url = new URL(appApi + 'squad/addNew');
    url.search = new URLSearchParams({name: name, type: type.toUpperCase()});
    return fetch(url, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        credentials: sendCred
    }).then(function (response) {
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        return resp;
    });
}