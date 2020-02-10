const appApi = DEPLOYED_URL;
const sendCred = SEND_CREDENTIALS;
import {createStore} from 'redux';
import {reducers} from '@/reducer/reducers'
import {setAuth} from "@/constants/actions";
import {NotificationManager} from 'react-notifications';
import {FormattedMessage} from "react-intl";
import React from "react";

// Redux
export const loadState = () => {
    try {
        const serializedState = sessionStorage.getItem('state');
        // console.log("load auth state");
        // console.log(serializedState);
        if (serializedState === null) {
            return {auth: null};
        }
        return JSON.parse(serializedState);
    } catch (err) {
        return undefined;
    }
};

export const saveState = (state) => {
    try {
        const serializedState = JSON.stringify(state);
        // console.log("save auth state");
        // console.log(state);
        sessionStorage.setItem('state', serializedState);
    } catch {
        // ignore write errors
    }
};

const store = createStore(
    reducers,
    loadState()
);

store.subscribe(() => {
    saveState({
        auth: store.getState().auth
    });
});
/// Redux

//TODO: maybe you wand to bound this request with server?
export function isPlayerLoggedIn() {
    let auth = store.getState().auth;
    // console.log("is user logged in?");
    // console.log(auth);
    return auth !== null && auth.auth !== null;
}

export function getPlayerName() {
    return store.getState().auth.auth;
}

/**
 * Just logout
 *
 * @returns true if successful
 */
export async function logout() {
    let url = new URL(appApi + 'auth/logout');
    return fetch(url, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        credentials: sendCred
    }).then(function (response) {
        if (response.status === 200)
            return response.text();
        else return response
    }).then(resp => {
        if (resp === "LOGGED_OUT") {
            //TODO: get rid of this auth store
            store.dispatch(setAuth(null));
            return true;
        } else {
            NotificationManager.error("", <FormattedMessage id={"app.logout.failed"}/>, 5000);
            return false;
        }
    });
}

/**
 * Login function.
 *
 * @param name - player name
 * @param pw - player's password
 * @returns null in incorrect login case or Player-dto in correct one.
 */
export async function login(name, pw) {
    let url = new URL(appApi + 'auth/login');
    return fetch(url, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: name,
                pw: pw
            })
        }
    ).then(function (response) {
        if (response.status === 200)
            return response.json().then(resp => {
                store.dispatch(setAuth(resp.name));
                NotificationManager.success(name, <FormattedMessage id={"app.input.login.success"}/>, 3000);
                return resp;
            });
        else throw response
    }).catch(e => {
        switch (e.status) {
            case 400:
                NotificationManager.warning(name, <FormattedMessage id={"app.login.bad.input"}/>, 5000);
                break;
            case 428:
                NotificationManager.warning(name, <FormattedMessage id={"app.login.should.logout"}/>, 3000);
                break;
            default:
                NotificationManager.error(name, <FormattedMessage id={"app.login.unexpected.error"}/>, 5000);
        }
        return null;
    });
}

/**
 * Creates new player
 *
 * @param name - player name
 * @param pw - player's password
 * @returns null if input name is incorrect or exists, player name otherwise
 */
export async function create(name, pw) {
    if (name.length !== name.trim().length || pw.length !== pw.trim().length) {
        NotificationManager.warning(name, <FormattedMessage id={"app.input.create.trim"}/>, 3000);
    } else if (!name.match("^[A-Za-z0-9]{4,16}$") || !pw.match("^[A-Za-z0-9]{4,16}$")) {
        NotificationManager.warning(name, <FormattedMessage id={"app.input.regex"}/>, 3000);
    } else {
        let url = new URL(appApi + 'auth/createPlayer');
        url.searchParams.append("name", name);
        return fetch(url, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: name,
                pw: pw
            })
        }).then(function (response) {
            if (response.status === 200)
                return response.json().then(resp => {
                    NotificationManager.success(name, <FormattedMessage id={"app.input.create.success"}/>, 3000);
                    store.dispatch(setAuth(name));
                    return resp;
                });
            else throw response;
        }).catch(e => {
            switch (e.status) {
                case 400:
                    NotificationManager.warning(name, <FormattedMessage id={"app.create.bad.input"}/>, 5000);
                    break;
                case 409:
                    NotificationManager.warning(name, <FormattedMessage id={"app.create.user.exists"}/>, 5000);
                    break;
                case 428:
                    NotificationManager.warning(name, <FormattedMessage id={"app.login.should.logout"}/>, 3000);
                    break;
                default:
                    NotificationManager.error(name, <FormattedMessage id={"app.login.unexpected.error"}/>, 5000);
            }
            return null;
        });
    }
    return null;
}