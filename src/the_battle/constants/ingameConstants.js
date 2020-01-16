export const FORCED_FRONT = "FORCED_FRONT";
export const FORCED_BACK = "FORCED_BACK";
export const LONG_LINE = ["pos1", "pos2", "pos3"];
export const SHORT_LINE = ["pos2", "pos4"];

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