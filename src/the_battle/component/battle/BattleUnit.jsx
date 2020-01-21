import React, {useState, useEffect} from 'react';
import {Progress} from 'reactstrap';
import styled from "styled-components";
import * as fighter from "./unit/fighter"
import * as ranger from "./unit/ranger"
import * as sage from "./unit/sage"

import Img from 'react-image'
import img_fgt from '@/img/fighter.png';
import img_rng from '@/img/ranger.png';
import img_sag from '@/img/sage.png';
import {
    FOES_TURN, MY_TURN,
    NO_TURN,
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
    border-style: ${props => props.brd === NO_TURN ? "none" : "dashed"}
    border-color: ${props => props.brd}
    border-width: 1px;
    background-color: ${props => props.bgc}
`;

/**
 * @return {null}
 */
export function BattleUnit(props) {

    const {
        characteristics, descrFunc, foe, calculateTargets, pos, clearTargets, pickActor, selectTargets,
        yourTurn
    } = props;
    const [bgc, setBgc] = useState(UNIT_BG_DEFAULT);
    const [borders, setBorders] = useState(NO_TURN);
    const [currentHP, setCurrentHP] = useState(characteristics.hp);

    const unitProps = getUnitProps(characteristics.type.type);

    useEffect(() => {
        setCurrentHP(characteristics.hp * 100 / characteristics.type.health);
    }, [characteristics.hp]);

    /**
     * If unit's turn, colors borders
     */
    useEffect(() => {
        if (yourTurn && foe) {
            setBorders(FOES_TURN)
        } else if (yourTurn && !foe) {
            setBorders(MY_TURN)
        } else {
            setBorders(NO_TURN)
        }
    }, [yourTurn]);

    /**
     * If unit can be a target of attack, colors bg
     */
    useEffect(() => {
        if (characteristics.marked) {
            setBgc(UNIT_BG_MARKED)
        } else {
            setBgc(UNIT_BG_DEFAULT)
        }
    }, [characteristics.marked]);

    /**
     * If unit's picked, colors bg
     */
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
        <UnitPlace bgc={bgc} brd={borders}
                   onMouseOver={() => onMouseOver()}
                   onMouseLeave={() => onMouseLeave()}
                   onClick={foe ? () => validateAttack() : () => pickActor(pos)}>
            {/*<div  style={{height: 90, width: 80}}/>*/}
            <Img style={{maxHeight: 100, maxWidth: 80, transform: foe ? "scaleX(-1)" : "scaleX(1)"}}
                 src={unitProps.image}/>
            <Progress animated color={"success"} value={currentHP}
                      style={{height: 5, marginRight: 3, marginLeft: 3, backgroundColor: "red"}}/>
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