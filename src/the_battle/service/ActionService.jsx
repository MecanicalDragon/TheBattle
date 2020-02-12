import {NotificationManager} from "react-notifications";
import React from "react";

const appApi = DEPLOYED_URL;
const sendCred = SEND_CREDENTIALS;

/**
 * Sending request to perform an action
 * @param actor - acting unit position (pos5)
 * @param action - action itself (ATTACK, BLOCK, WAIT)
 * @param data - additional data (targets positions array)
 * @returns new state data
 */
export async function performAction(actor, action, data) {
    let url = new URL(appApi + 'action/performAction');
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
        if (response.status === 200) {
            return response.json().then(r => {
                return r
            })
        } else throw response;
    }).catch(e => {
        return e.text().then(msg => {
            NotificationManager.error(action, msg, 3000);
            return null;
        })
    });
}