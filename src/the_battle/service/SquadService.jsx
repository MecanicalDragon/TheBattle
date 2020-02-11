import {NotificationManager} from "react-notifications";

const appApi = DEPLOYED_URL;
const sendCred = SEND_CREDENTIALS;

/**
 * Request of free heroes pool
 * @returns free heroes pool or null
 */
export async function getPool() {
    return fetch(appApi + 'squad/getPool', {
        credentials: sendCred
    }).then(function (response) {
        return response.status === 200 ? response.json() : throw response;
    }).then(resp => {
        let pool = new Map();
        resp.forEach(function (entry) {
            pool.set(entry.id, entry);
        });
        return pool;
    }).catch(e => {
        return e.text().then(msg => {
            NotificationManager.error("ERROR", msg, 3000);
            return null;
        })
    });
}

/**
 * Request of hero removal
 * @param unit - unit id
 * @returns status string or null
 */
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
        return response.status === 200 ? response.text() : throw response;
    }).then(resp => {
        return resp;
    }).catch(e => {
        return e.text().then(msg => {
            NotificationManager.error("ERROR", msg, 3000);
            return null;
        })
    });
}

/**
 * Adds new hero to player's pool
 * @param name - new hero name
 * @param type - new hero type
 * @returns newly created hero or null
 */
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