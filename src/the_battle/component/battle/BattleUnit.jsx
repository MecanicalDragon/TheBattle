import React, {useState, useEffect} from 'react';
import styled from "styled-components";
import * as fighter from "./unit/fighter"
import * as ranger from "./unit/ranger"
import * as sage from "./unit/sage"

import Img from 'react-image'
import img_fgt from '@/img/fighter.png';
import img_rng from '@/img/ranger.png';
import img_sag from '@/img/sage.png';
import {
    UNIT_BG_ATTACK,
    UNIT_BG_DEFAULT,
    UNIT_BG_MARKED,
    UNIT_BG_OVER,
    UNIT_BG_PICKED
} from "@/constants/ingameConstants";

const UnitPlace = styled.div`
    width: 100px;
    height: 100px;
    border-radius: 15px;
    background-color: ${props => props.bgc}
`;

/**
 * @return {null}
 */
export function BattleUnit(props) {

    const {characteristics, descrFunc, foe, calculateTargets, pos, clearTargets, pickAttacker, selectTargets} = props;
    const [bgc, setBgc] = useState(UNIT_BG_DEFAULT);

    const unitProps = getUnitProps(characteristics.type.type);

    useEffect(() => {
        if (characteristics.marked) {
            setBgc(UNIT_BG_MARKED)
        } else {
            setBgc(UNIT_BG_DEFAULT)
        }
    }, [characteristics.marked]);

    useEffect(() => {
        if (characteristics.picked) {
            setBgc(UNIT_BG_PICKED)
        } else if (characteristics.picked === undefined) {
            setBgc(UNIT_BG_DEFAULT)
        } else {
            setBgc(UNIT_BG_OVER)
        }
    }, [characteristics.picked]);

    const onMouseOver = () => {
        if (!characteristics.picked) {
            descrFunc(characteristics);
            if (characteristics.marked) {
                setBgc(UNIT_BG_ATTACK)
            } else {
                setBgc(UNIT_BG_OVER);
                calculateTargets(pos, foe, unitProps.mark)
            }
        }
    };

    const onMouseLeave = () => {
        if (!characteristics.picked) {
            clearTargets(foe);
            setBgc(characteristics.marked ? UNIT_BG_MARKED : UNIT_BG_DEFAULT);
        }
    };
    const validateAttack = () => {
        if (characteristics.marked)
            selectTargets([pos]);
    };

    return characteristics ?
        <UnitPlace bgc={bgc}
                   onMouseOver={() => onMouseOver()}
                   onMouseLeave={() => onMouseLeave()}
                   onClick={foe ? () => validateAttack() : () => pickAttacker(pos)}>
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