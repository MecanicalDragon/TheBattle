import {SET_BATTLE_UUID} from "@/constants/actionTypes";

export default (state = {bud: null}, action = {}) => {
    if (action.type === SET_BATTLE_UUID) {
        const newState = Object.assign({}, state);
        newState.bud = action.bud;
        return newState;
    }
    return state;
};