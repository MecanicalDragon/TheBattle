import * as actionType from "./actionTypes";

// Redux.step2: define action
export const setLocale = (locale = '') => ({type: actionType.SET_LOCALE, locale});
export const setNavPosition = (navPosition = '') => ({type: actionType.SET_NAV_POSITION, navPosition});
export const setAuth = (auth = null) => ({type: actionType.SET_AUTH, auth});