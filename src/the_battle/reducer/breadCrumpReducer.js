import * as actionType from "@/constants/actionTypes";

// Redux.step3: define reducer
export default (state = {navPosition: 'Home'}, action = {}) => {
    if (action.type === actionType.SET_NAV_POSITION) {
        const newState = Object.assign({}, state);
        newState.navPosition = action.navPosition;
        return newState;
    }
    return state;
};