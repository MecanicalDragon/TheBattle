export const FORCED_FRONT = "FORCED_FRONT";
export const FORCED_BACK = "FORCED_BACK";
export const LONG_LINE = ["pos1", "pos2", "pos3"];
export const SHORT_LINE = ["pos2", "pos4"];

export const UNIT_BG_DEFAULT = "var(--jumbotron-bg)";
export const UNIT_BG_OVER = "var(--over-unit)";
export const UNIT_BG_MARKED = "var(--marked-unit)";
export const UNIT_BG_PICKED = "var(--picked-unit)";
export const UNIT_BG_ATTACK = "var(--attack-unit)";

export const UNIT_FOES_TARGET = "red";
export const UNIT_FOES_TURN = "green";
export const UNIT_NOT_FOES_TARGET = "grey";

export const STATUS = ["FREE", "IN_SEARCH", "IN_BATTLE"];

export const ATTACK = "ATTACK";
export const WAIT = "WAIT";
export const BLOCK = "BLOCK";
export const CONCEDE = "CONCEDE";

export const CLOSE_TARGETS = {
    "ff": {
        "pos1": ["pos1", "pos3"],
        "pos3": ["pos1", "pos3", "pos5"],
        "pos5": ["pos3", "pos5"]
    },
    "bb": {
        "pos2": ["pos2", "pos4"],
        "pos4": ["pos2", "pos4"]
    },
    "fb": {
        "pos1": ["pos2"],
        "pos3": ["pos2", "pos4"],
        "pos5": ["pos4"]
    },
    "bf": {
        "pos2": ["pos1", "pos3"],
        "pos4": ["pos3", "pos5"]
    }
};