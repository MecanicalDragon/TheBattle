import React, {useState} from 'react';
import styled from "styled-components";
import * as fighter from "./unit/fighter"
import * as ranger from "./unit/ranger"
import * as sage from "./unit/sage"

import Img from 'react-image'
import img_fgt from '@/img/fighter.png';
import img_rng from '@/img/ranger.png';
import img_sag from '@/img/sage.png';

const UnitPlace = styled.div`
    width: 100px;
    height: 100px;
    border-radius: 15px;
    background-color: ${props => props.mrk ? "var(--marked-unit)" : props.over ? "var(--over-unit)" : "var(--jumbotron-bg)"}
`;

/**
 * @return {null}
 */
export function BattleUnit(props) {

    const {characteristics, descrFunc, foe, calculateTargets, pos, clearTargets} = props;
    const [over, setOver] = useState(false);

    const unitProps = getUnitProps(characteristics.type.type);

    const onMouseOver = () => {
        descrFunc(characteristics);
        setOver(true);
        calculateTargets(pos, foe, unitProps.mark)
    };

    const onMouseLeave = () => {
        clearTargets(foe);
        setOver(false);
    };

    return characteristics ?
        <UnitPlace over={over} mrk={characteristics.marked}
                   onMouseOver={() => onMouseOver()}
                   onMouseLeave={() => onMouseLeave()}>
            <Img style={{maxHeight: 100, maxWidth: 80, transform: foe ? "scaleX(-1)" : "scaleX(1)"}}
                 src={unitProps.image}/>
        </UnitPlace>
        : null
}

function getUnitProps(type) {
    switch (type) {
        case "FIGHTER":
            return {image: img_fgt, attack: fighter.attack, mark: fighter.markTargets};
        case "SAGE":
            return {image: img_sag, attack: sage.attack, mark: sage.markTargets};
        case "RANGER":
            return {image: img_rng, attack: ranger.attack, mark: ranger.markTargets};
        default:
            return null
    }
}