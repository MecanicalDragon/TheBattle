import React from "react";
import defaultHandling from "@/service/ErrorService";
import {NotificationManager} from "react-notifications";
import {FormattedMessage} from "react-intl";

const appApi = DEPLOYED_URL;
const sendCred = SEND_CREDENTIALS;

/**
 * Send ping request to the server if turn time exceeds limit
 */
export async function ping() {
    let url = new URL(appApi + 'action/pingTurn');
    fetch(url, {method: 'POST', credentials: sendCred}).then(function (response) {
        console.log("Time is up! Ping request has been sent!")
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

const msgCoolDown = {
    player: true,
    foe: true
};

/**
 * Send message to the opponent
 * @param msgNumber - number of message
 */
export async function sendMessage(msgNumber) {
    if (msgCoolDown.player) {
        msgCoolDown.player = false
        let url = new URL(appApi + 'action/sendMessage');
        fetch(url, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            credentials: sendCred,
            body: JSON.stringify(msgNumber)
        }).then(function (response) {
            if (response.status === 200) {
                NotificationManager.success(<FormattedMessage id={"app.battle.message.you.say"}/>,
                    <FormattedMessage id={"app.battle.message.default." + msgNumber}/>, 3000)
            } else if (response.status === 400) {
                NotificationManager.warning(<FormattedMessage id={"app.battle.message.battle.over.msg"}/>,
                    <FormattedMessage id={"app.battle.message.battle.over.header"}/>, 5000)
            } else defaultHandling(response, true);
        })
        setTimeout(function () {
            msgCoolDown.player = true;
        }, 3000)
    } else {
        NotificationManager.error("", <FormattedMessage id={"app.battle.message.too.early"}/>, 3000)
    }
}

/**
 * Show opponent's message
 * @param msg - messageAction
 */
export function showNotification(msg) {
    if (msgCoolDown.foe) {
        msgCoolDown.foe = false;
        NotificationManager.warning(<FormattedMessage id={"app.battle.message.foe.says"}/>,
            <FormattedMessage id={"app.battle.message.default." + msg.messageNumber}/>, 3000)
        setTimeout(function () {
            msgCoolDown.foe = true;
        }, 3000)
    }
}