import React from 'react';
import Img from 'react-image'
import img_fgt from '@/img/fighter.png';
import {FORCED_BACK, FORCED_FRONT, SHORT_LINE, CLOSE_TARGETS} from "@/constants/ingameConstants";

// export function Fighter(props) {
//
//     const {foe} = props;
//
//     const targets = () => {
//         console.log("close enemies")
//     };
//
//     return (
//         <Img style={{maxHeight: 100, maxWidth: 80, transform: foe ? "scaleX(-1)" : "scaleX(1)"}}
//              src={img_fgt}/>
//     )
// }

export function attack() {
    console.log("close enemies")
}

//TODO: concat does not work
//TODO: object[][][] - merdge functions
export function markTargets(position, attacker, target) {
    console.log("______");
    console.log(position);
    console.log(attacker);

    let posN = +position.substring(3);
    let targets = [];

    function addIfTargetIsForcedFront(upperPosition, lowerPosition) {
        if (target.pos1.hp === 0 && target.pos5.hp === 0) {
            targets.push("pos4", "pos5")
        } else {
            if (target.pos1.hp === 0 && (position === upperPosition)) {
                targets.push("pos2")
            }
            if (target.pos5.hp === 0 && (position === lowerPosition)) {
                targets.push("pos4")
            }
        }
    }

    function addIfTargetIsForcedBack(upperPosition, lowerPosition) {
        if (target.pos2.hp === 0 && target.pos4.hp === 0) {
            targets.push("pos1", "pos2", "pos3")
        } else {
            if (target.pos2.hp === 0 && (position !== lowerPosition)) {
                targets.push("pos1")
            }
            if (target.pos4.hp === 0 && (position !== upperPosition)) {
                targets.push("pos5")
            }
        }
    }

    function addToFF(position, upperPos, lowerPos) {
        // Attacking unit is in the front line
        if (target.type === FORCED_FRONT) { //  target has FORCED_FRONT
            targets.concat(CLOSE_TARGETS.ff[position]);
            addIfTargetIsForcedFront(upperPos, lowerPos);
            // if (target.pos3.hp === 0) {
            //     if (target.pos1.hp === 0 && target.pos5.hp === 0) {
            //         targets.push("pos4", "pos5")
            //     } else {
            //         if (target.pos1.hp === 0 && (position === "pos1")) {
            //             targets.push("pos2")
            //         }
            //         if (target.pos5.hp === 0 && (position === "pos5")) {
            //             targets.push("pos4")
            //         }
            //     }
            // }
        } else {    //  FORCED_BACK here
            targets.concat(CLOSE_TARGETS.fb[position]);
            addIfTargetIsForcedBack(upperPos, lowerPos);
            // if (target.pos2.hp === 0 && target.pos4.hp === 0) {
            //     targets.push("pos1", "pos2", "pos3")
            // } else {
            //     if (target.pos2.hp === 0 && (position !== "pos5")) {
            //         targets.push("pos1")
            //     }
            //     if (target.pos4.hp === 0 && (position !== "pos1")) {
            //         targets.push("pos5")
            //     }
            // }
        }
    }

    function addToFB(position, upperPos, lowerPos) {
        // Attacking unit is in the front line
        if (target.type === FORCED_FRONT) {
            targets.concat(CLOSE_TARGETS.bf[position]);
            addIfTargetIsForcedFront(upperPos, lowerPos);
            // if (target.pos3.hp === 0) {
            //     if (target.pos1.hp === 0 && target.pos5.hp === 0) {
            //         targets.push("pos4", "pos5")
            //     } else {
            //         if (target.pos1.hp === 0 && (position === "pos2")) {
            //             targets.push("pos2")
            //         }
            //         if (target.pos5.hp === 0 && (position === "pos4")) {
            //             targets.push("pos4")
            //         }
            //     }
            // }
        } else {    //  FORCED_BACK here
            targets.concat(CLOSE_TARGETS.bb[position]);
            addIfTargetIsForcedBack(upperPos, lowerPos);
            // if (target.pos2.hp === 0 && target.pos4.hp === 0) {
            //     targets.push("pos1", "pos2", "pos3")
            // } else {
            //     if (target.pos2.hp === 0 && (position !== "pos4")) {
            //         targets.push("pos1")
            //     }
            //     if (target.pos4.hp === 0 && (position !== "pos2")) {
            //         targets.push("pos5")
            //     }
            // }
        }
    }

    if (attacker.type === FORCED_FRONT) {

        // Attacking unit is in the back line
        if (SHORT_LINE[0] === position || SHORT_LINE[1] === position) {
            let posInc = "pos" + (posN + 1);
            let posDec = "pos" + (posN - 1);
            if (attacker[posInc].hp === 0 && attacker[posDec].hp === 0) {
                addToFF(posInc, "pos1", "pos5");
                addToFF(posDec, "pos1", "pos5");
            }
        }
        addToFF(position, "pos1", "pos5")
        // // Attacking unit is in the front line
        // if (target.type === FORCED_FRONT) { //  target has FORCED_FRONT
        //     targets = CLOSE_TARGETS.ff[position];
        //     addIfTargetIsForcedFront("pos1", "pos5");
        //     // if (target.pos3.hp === 0) {
        //     //     if (target.pos1.hp === 0 && target.pos5.hp === 0) {
        //     //         targets.push("pos4", "pos5")
        //     //     } else {
        //     //         if (target.pos1.hp === 0 && (position === "pos1")) {
        //     //             targets.push("pos2")
        //     //         }
        //     //         if (target.pos5.hp === 0 && (position === "pos5")) {
        //     //             targets.push("pos4")
        //     //         }
        //     //     }
        //     // }
        //     return targets
        // } else {    //  FORCED_BACK here
        //     targets = CLOSE_TARGETS.fb[position];
        //     addIfTargetIsForcedBack("pos1", "pos5");
        //     // if (target.pos2.hp === 0 && target.pos4.hp === 0) {
        //     //     targets.push("pos1", "pos2", "pos3")
        //     // } else {
        //     //     if (target.pos2.hp === 0 && (position !== "pos5")) {
        //     //         targets.push("pos1")
        //     //     }
        //     //     if (target.pos4.hp === 0 && (position !== "pos1")) {
        //     //         targets.push("pos5")
        //     //     }
        //     // }
        //     return targets
        // }

    } else {    // FORCED_BACK here

        // Attacking unit is in the back line
        if (SHORT_LINE[0] !== position && SHORT_LINE[1] !== position) {
            if (position === "pos3") {
                let posInc = "pos" + (posN + 1);
                let posDec = "pos" + (posN - 1);
                if (attacker[posInc].hp === 0 && attacker[posDec].hp === 0) {
                    addToFB(posInc, "pos2", "pos4");
                    addToFB(posDec, "pos2", "pos4");
                }
            } else if (position === "pos1" && attacker.pos2.hp === 0) {
                addToFB("pos2", "pos2", "pos4")
            } else if(position === "pos5" && attacker.pos4.hp === 0) {
                addToFB("pos4", "pos2", "pos4")
            }
        }

        addToFB(position, "pos2", "pos4")
        // // Attacking unit is in the front line
        // if (target.type === FORCED_FRONT) {
        //     targets = CLOSE_TARGETS.bf[position];
        //     addIfTargetIsForcedFront("pos2", "pos4");
        //     // if (target.pos3.hp === 0) {
        //     //     if (target.pos1.hp === 0 && target.pos5.hp === 0) {
        //     //         targets.push("pos4", "pos5")
        //     //     } else {
        //     //         if (target.pos1.hp === 0 && (position === "pos2")) {
        //     //             targets.push("pos2")
        //     //         }
        //     //         if (target.pos5.hp === 0 && (position === "pos4")) {
        //     //             targets.push("pos4")
        //     //         }
        //     //     }
        //     // }
        //     return targets;
        // } else {    //  FORCED_BACK here
        //     targets = CLOSE_TARGETS.bb[position];
        //     addIfTargetIsForcedBack("pos2", "pos4");
        //     // if (target.pos2.hp === 0 && target.pos4.hp === 0) {
        //     //     targets.push("pos1", "pos2", "pos3")
        //     // } else {
        //     //     if (target.pos2.hp === 0 && (position !== "pos4")) {
        //     //         targets.push("pos1")
        //     //     }
        //     //     if (target.pos4.hp === 0 && (position !== "pos2")) {
        //     //         targets.push("pos5")
        //     //     }
        //     // }
        //     return targets
        // }

    }
    return targets;
}
