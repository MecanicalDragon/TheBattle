import {NotificationManager} from "react-notifications";
import defaultHandling from "@/service/ErrorService";
import {FormattedMessage} from "react-intl";
import React from "react";

const appApi = DEPLOYED_URL;
const sendCred = SEND_CREDENTIALS;

/**
 * Request of free heroes pool
 * @returns free heroes pool or null
 */
export async function getPool() {
    return fetch(appApi + 'squad/getPoolAndData', {
        credentials: sendCred
    }).then(function (response) {
        if (response.status === 200)
            return response.json();
        else throw response;
    }).then(resp => {
        let pool = new Map();
        resp.pool.forEach(function (entry) {
            pool.set(entry.id, entry);
        });
        return {pool: pool, playerData: resp.player};
    }).catch(e => {
        defaultHandling(e);
        return null;
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
        if (response.status === 200)
            return true;
        else throw response;
    }).catch(e => {
        defaultHandling(e, true);
        return null;
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
        if (response.status === 200)
            return response.json().then(r => {
                return r
            });
        else throw response;
    }).catch(e => {
        if (e.status === 412) {
            NotificationManager.warning(name, <FormattedMessage id={"app.manage.unit.name"}/>, 5000);
        } else defaultHandling(e);
        return null;
    });
}