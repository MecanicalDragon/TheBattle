const appApi = DEPLOYED_URL;

//TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
export async function getPool(pName) {
    return fetch(appApi + 'squad/getPool?pName=' + pName).then(function (response) {
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        let pool = new Map();
        resp.forEach(function (entry) {
            pool.set(entry.id, entry);
        });
        console.log("pool^");
        console.log(pool);
        return pool;
    });
}

//TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
export async function retireHero(pName, unit) {
    let url = new URL(appApi + 'squad/retireHero');
    url.search = new URLSearchParams({pName: pName, unit: unit});
    return fetch(url, {
        method: 'DELETE',
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

//TODO: TODO_SECURITY: requestParam 'pName' should be removed in release
export async function addNewHero(pName, name, type) {
    let url = new URL(appApi + 'squad/addNew');
    url.search = new URLSearchParams({pName: pName, name: name, type: type.toUpperCase()});
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