import * as actionTypes from "@/constants/actionTypes"

export default (state = {locale: 'en'}, action = {}) => {
    if (action.type === actionTypes.SET_LOCALE) {
        const newState = Object.assign({}, state);
        newState.locale = action.locale;
        return newState;
    }
    return state;
};