import React from 'react';
import Img from 'react-image'
import img_fgt from '@/img/fighter.png';
import {FORCED_BACK, FORCED_FRONT, SHORT_LINE, CLOSE_TARGETS} from "@/constants/ingameConstants";

export function markTargets(position, attacker, target) {

    let posN = +position.substring(3);
    let targets = [];

    function addBacklineIfTargetIsForcedFront(position) {
        if (target.pos3.hp === 0) {
            if (target.pos1.hp === 0 && position !== "pos5" && position !== "pos4") {
                targets.push("pos2")
            }
            if (target.pos5.hp === 0 && position !== "pos1" && position !== "pos2") {
                targets.push("pos4")
            }
        }
    }

    function addBacklineIfTargetIsForcedBack(position) {
        if (target.pos2.hp === 0 && target.pos4.hp === 0) {
            targets.push("pos1", "pos3", "pos5")
        } else {
            if (target.pos2.hp === 0 && position !== "pos4" && position !== "pos5") {
                targets.push("pos1")
            }
            if (target.pos4.hp === 0 && position !== "pos1" && position !== "pos2") {
                targets.push("pos5")
            }
        }
    }

    // Add targets to attacker from first line in FF-type squad
    function addToFF(position) {
        // Target squad is FF-type
        if (target.type === FORCED_FRONT) { //  target has FORCED_FRONT
            targets = targets.concat(CLOSE_TARGETS.ff[position]);
            if (target.pos3.hp === 0) {
                targets.push("pos5", "pos1")
            }
            addBacklineIfTargetIsForcedFront(position);
        } else {    //  Target squad is FORCED_BACK
            targets = targets.concat(CLOSE_TARGETS.fb[position]);
            if (attacker.pos3.hp === 0) {
                targets.push("pos2", "pos4")
            }
            addBacklineIfTargetIsForcedBack(position);
        }
    }

    // Add targets to attacker from first line in FB-type line
    function addToFB(position) {
        // Target squad is FF-type
        if (target.type === FORCED_FRONT) {
            targets = targets.concat(CLOSE_TARGETS.bf[position]);
            if (target.pos3.hp === 0) {
                targets.push("pos5", "pos1")
            }
            addBacklineIfTargetIsForcedFront(position);
        } else {    //  Target squad is //  FORCED_BACK
            targets = targets.concat(CLOSE_TARGETS.bb[position]);
            addBacklineIfTargetIsForcedBack(position);
        }
    }

    if (attacker.type === FORCED_FRONT) {

        // Attacking unit is in the back line
        if (SHORT_LINE[0] === position || SHORT_LINE[1] === position) {
            let posInc = "pos" + (posN + 1);
            let posDec = "pos" + (posN - 1);
            if (attacker[posInc].hp === 0 && attacker[posDec].hp === 0) {
                addToFF("pos3");
            }
        } else {
            addToFF(position);
        }


    } else {    // FORCED_BACK here

        // Attacking unit is in the back line
        if (SHORT_LINE[0] !== position && SHORT_LINE[1] !== position) {
            if (target.pos3.hp === 0 && target.pos1.hp === 0 && target.pos5.hp === 0) {
                targets.push("pos2", "pos4")
            } else {
                if (position === "pos3") {
                    let posInc = "pos" + (posN + 1);
                    let posDec = "pos" + (posN - 1);
                    if (attacker[posInc].hp === 0 && attacker[posDec].hp === 0) {
                        addToFB(posInc);
                        addToFB(posDec);
                    }
                } else {
                    if (position === "pos1" && attacker.pos2.hp === 0) {
                        addToFB("pos2")
                    } else if (position === "pos5" && attacker.pos4.hp === 0) {
                        addToFB("pos4")
                    }
                }
            }
        } else {
            addToFB(position)
        }
    }
    return targets;
}
