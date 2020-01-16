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

export function markTargets(position, attacker, target) {
    console.log("______");
    console.log(position);
    console.log(attacker);

    let posN = +position.substring(3);
    let targets = [];

    if (attacker.type === FORCED_FRONT) {
        if (SHORT_LINE[0] === position || SHORT_LINE[1] === position) {
            //TODO: front absence checks
            return []
        }
        if (target.type === FORCED_FRONT) {
            //TODO: front absence checks
            return CLOSE_TARGETS.ff[position]
        } else {    //  FORCED_BACK here
            //TODO: front absence checks
            return CLOSE_TARGETS.fb[position]
        }

    } else {    // FORCED_BACK here
        if (SHORT_LINE[0] !== position && SHORT_LINE[1] !== position) {
            //TODO: front absence checks
            return []
        }
        if (target.type === FORCED_FRONT) {
            //TODO: front absence checks
            return CLOSE_TARGETS.bf[position]
        } else {    //  FORCED_BACK here
            //TODO: front absence checks
            return CLOSE_TARGETS.bb[position]
        }

    }
}
