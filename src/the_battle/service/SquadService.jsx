const appApi = DEPLOYED_URL;

export async function getSquad() {
    return fetch(appApi + 'squad/getNewSquad').then(function (response) {
        return response.json();
    }).then(resp => {
        if (resp.status !== undefined) {
            console.log(resp);
            return null;
        } else {
            return resp;
        }
    });
}

//TODO: TODO_SECURITY: requestParam 'name' should be removed in release
export async function getPool(pName) {
    return fetch(appApi + 'squad/getPool?pName=' + pName).then(function (response) {
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        return resp;
    });
}

//TODO: TODO_SECURITY: requestParam 'name' should be removed in release
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
        console.log("add_new response:");
        console.log(response);
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        return resp;
    });
}