import React, {useState} from 'react';
import Img from 'react-image'
import img_rng from '@/img/ranger.png';

// export function Ranger(props) {
//
//     const {foe} = props;
//
//     const targets = () =>{
//         console.log("distant enemies")
//     };
//
//     return (
//         <Img style={{maxHeight: 100, maxWidth: 80, transform: foe ? "scaleX(-1)" : "scaleX(1)"}}
//              src={img_rng}/>
//     )
// }

export function attack() {
    console.log("distant enemies")
}

export function markTargets(position, attacker, target) {
    return ["pos1", "pos3"]
}