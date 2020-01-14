const appApi = DEPLOYED_URL;
import {createStore} from 'redux';
import {reducers} from '@/reducer/reducers'
import React from "react";
import {setBattleUuid} from "@/constants/actions";

// // Redux
// export const loadState = () => {
//     try {
//         const serializedState = sessionStorage.getItem('bud');
//         console.log("load bud state");
//         console.log(serializedState);
//         if (serializedState === null) {
//             return {bud: null};
//         }
//         return JSON.parse(serializedState);
//     } catch (err) {
//         return undefined;
//     }
// };
//
// export const saveState = (state) => {
//     try {
//         const serializedState = JSON.stringify(state);
//         console.log("save bud state");
//         console.log(state);
//         sessionStorage.setItem('bud', serializedState);
//     } catch {
//         // ignore write errors
//     }
// };
//
// const store = createStore(
//     reducers,
//     loadState()
// );
//
// store.subscribe(() => {
//     saveState({
//         bud: store.getState().bud
//     });
// });
// /// Redux

export function registerBattle(uuid) {
    store.dispatch(setBattleUuid(uuid))
}

export function clearBattle() {
    store.dispatch(setBattleUuid(null))
}

export function getBattleUuid() {
    let bud = store.getState().bud;
    return bud ? bud.bud : null
}

//TODO: TODO_SECURITY: requestParam 'name' should be removed in release
export async function getBattle(pName, bud) {
    let url = new URL(appApi + 'battle/getDislocations');
    url.search = new URLSearchParams({pName: pName, bud: bud});
    return fetch(url).then(function (response) {
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        return resp;
    });
}

//TODO: TODO_SECURITY: requestParam 'name' should be removed in release
export async function battleBid(pName, squad) {
    let url = new URL(appApi + 'battle/startBattleBid');
    url.search = new URLSearchParams({pName: pName});
    return fetch(url, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(squad)
    }).then(function (response) {
        return response.status === 200 ? response.json() : null;
    }).then(resp => {
        return resp;
    });
}

//TODO: TODO_SECURITY: requestParam 'name' should be removed in release
export async function cancelBid(pName) {
    let url = new URL(appApi + 'battle/cancelBid');
    url.search = new URLSearchParams({pName: pName});
    return fetch(url, {
        method: 'POST',
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

//TODO: delete after tests
export async function test(pName) {
    let url = new URL(appApi + 'battle/test');
    url.search = new URLSearchParams({pName: pName});
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
