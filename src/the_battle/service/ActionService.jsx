import React from "react";
import defaultHandling from "@/service/ErrorService";

const appApi = DEPLOYED_URL;
const sendCred = SEND_CREDENTIALS;

/**
 * Send ping request to the server if turn time exceeds limit
 */
export async function ping() {
    let url = new URL(appApi + 'action/pingTurn');
    fetch(url, {method: 'POST', credentials: sendCred}).then(function (response) {
        console.log("Time is up! Ping request has sent!")
    })
}

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
        } else if (response.status === 230) {
            return null;
        } else throw response;
    }).catch(e => {
        defaultHandling(e, true);
        return null;
    });
}