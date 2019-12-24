import {SET_AUTH} from "@/constants/actionTypes";

export default (state = {auth: null}, action = {}) => {
    if (action.type === SET_AUTH) {
        const newState = Object.assign({}, state);
        newState.auth = action.auth;
        return newState;
    }
    return state;
};