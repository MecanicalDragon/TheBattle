import image from '@/img/fighter.png';
import {FORCED_FRONT, SHORT_LINE, CLOSE_TARGETS} from "@/constants/ingameConstants";

function markTargets(position, attacker, target) {

    let targets = [];

    function addBacklineIfTargetIsForcedFront() {
        if (target.pos3.hp === 0) {
            if (target.pos1.hp === 0) {
                targets.push("pos2")
            }
            if (target.pos5.hp === 0) {
                targets.push("pos4")
            }
        }
    }

    function addBacklineIfTargetIsForcedBack(position) {
        if (target.pos2.hp === 0 && target.pos4.hp === 0) {
            targets.push("pos1", "pos3", "pos5")
        } else {
            let fbCondition = position ? (position !== "pos5" || attacker.pos3.hp === 0) : true;
            if (target.pos2.hp === 0 && fbCondition) {
                targets.push("pos1")
            }
            if (target.pos4.hp === 0 && fbCondition) {
                targets.push("pos5")
            }
        }
    }

    // Add targets to attacker from first line in FF-type squad
    function addToFF(position) {
        // Target squad is FF-type
        if (target.type === FORCED_FRONT) { //  target has FORCED_FRONT
            targets = targets.concat(CLOSE_TARGETS.ff[position]);
            if (target.pos3.hp === 0 || attacker.pos3.hp === 0) {
                targets.push("pos5", "pos1")
            }
            addBacklineIfTargetIsForcedFront();
        } else {    //  Target squad is FORCED_BACK
            targets = targets.concat(CLOSE_TARGETS.fb[position]);
            if (attacker.pos3.hp === 0 ||
                ((position === "pos5" && target.pos4.hp === 0) || (position === "pos1" && target.pos2.hp === 0))) {
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
            let aPos = position === "pos2" ? "pos4" : "pos2";
            if (target.pos3.hp === 0 || (attacker[aPos].hp === 0)) {
                targets.push("pos5", "pos1")
            }
            addBacklineIfTargetIsForcedFront();
        } else {    //  Target squad is //  FORCED_BACK
            targets = targets.concat(CLOSE_TARGETS.bb[position]);
            addBacklineIfTargetIsForcedBack();
        }
    }

    if (attacker.type === FORCED_FRONT) {

        // Attacking unit is in the back line
        if (SHORT_LINE[0] === position || SHORT_LINE[1] === position) {
            if (attacker.pos3.hp === 0) {
                if ((position === "pos2" && attacker.pos1.hp === 0)
                    || (position === "pos4" && attacker.pos5.hp === 0)) {
                    addToFF("pos3");
                }
            }
        } else {
            addToFF(position);
        }

    } else {    // FORCED_BACK here

        // Attacking unit is in the back line
        if (SHORT_LINE[0] !== position && SHORT_LINE[1] !== position) {
            if (attacker.pos2.hp === 0 && attacker.pos4.hp === 0) {
                addToFB("pos2")
            } else {
                if (position === "pos1" && attacker.pos2.hp === 0) {
                    addToFB("pos2")
                } else if (position === "pos5" && attacker.pos4.hp === 0) {
                    addToFB("pos4")
                }
            }
        } else {
            addToFB(position)
        }
    }
    return targets.filter((value, index, self) => {
        return self.indexOf(value) === index && target[value].hp > 0;
    });
}

export default function properties() {
    return {
        image: image,
        markTargets: markTargets
    }
}
