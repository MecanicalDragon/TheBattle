import React from 'react';
import Img from 'react-image'
import img_sag from '@/img/sage.png';

// export function Sage(props) {
//
//     const {foe} = props;
//
//     const targets = () =>{
//         console.log("everyone")
//     };
//
//     return (
//         <Img style={{maxHeight: 100, maxWidth: 80, transform: foe ? "scaleX(-1)" : "scaleX(1)"}}
//              src={img_sag}/>
//     )
// }

export function attack() {
    console.log("everyone")
}

export function markTargets(position, attacker, target) {
    return ["pos1", "pos2", "pos3", "pos4", "pos5"];
}