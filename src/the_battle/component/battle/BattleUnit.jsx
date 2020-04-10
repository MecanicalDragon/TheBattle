import React, {useState, useEffect} from 'react';
import {Progress} from 'reactstrap';
import styled from "styled-components";
import {UNIT_TYPES} from "./unit/unitTypes"

import Img from 'react-image'
import img_skull from '@/img/skull.png';
import {
    UNIT_BORDERS_PICKED, UNIT_BORDERS_MARKED,
    UNIT_BORDERS_DEFAULT,
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
    border-style: ${props => props.brd === UNIT_BORDERS_DEFAULT ? "none" : "dashed"}
    border-color: ${props => props.brd}
    border-width: 1px;
    background-color: ${props => props.bgc}
`;

/**
 * @return {null}
 */
export function BattleUnit(props) {

    const {
        characteristics, descrFunc, foe, calculateTargets, pos, clearTargets, selectTargets, yourTurn, remarkTrigger
    } = props;

    const initDefaultBg = () => {
        if (foe) {
            return UNIT_BG_DEFAULT
        } else {
            if (yourTurn) {
                return UNIT_BG_PICKED
            } else {
                return UNIT_BG_DEFAULT
            }
        }
    };

    const initDefaultBorders = () => {
        if (foe) {
            if (yourTurn) {
                return UNIT_BORDERS_PICKED
            } else {
                return UNIT_BORDERS_DEFAULT
            }
        } else {
            return UNIT_BORDERS_DEFAULT
        }
    };

    const [bgc, setBgc] = useState(initDefaultBg());
    const [borders, setBorders] = useState(initDefaultBorders());
    const [currentHP, setCurrentHP] = useState(characteristics.hp);

    const unitProps = getUnitProps(characteristics.type.type);

    /**
     * Updating unit hp
     */
    useEffect(() => {
        setCurrentHP(characteristics.hp * 100 / characteristics.type.health);
        if (characteristics.hp < 1) {
            setBgc(UNIT_BG_DEFAULT);
            setBorders(UNIT_BORDERS_DEFAULT)
        }
    }, [characteristics.hp]);

    /**
     * If unit's turn, sets it's and it's targets' borders or backgrounds
     */
    useEffect(() => {
        if (yourTurn) {
            if (foe) {
                setBorders(UNIT_BORDERS_PICKED);
            } else {
                setBgc(UNIT_BG_PICKED);
            }
            calculateTargets(pos, foe, unitProps.markTargets, true)
        } else {
            if (foe) {
                setBorders(UNIT_BORDERS_DEFAULT);
            } else {
                setBgc(UNIT_BG_DEFAULT);
            }
        }
    }, [yourTurn]);

    /**
     * Remark targets when cursor leaves squad area or if this is unit's second move in a row
     */
    useEffect(() => {
        if (yourTurn) {
            if (foe) {
                setBorders(UNIT_BORDERS_PICKED);
            } else {
                setBgc(UNIT_BG_PICKED);
            }
            calculateTargets(pos, foe, unitProps.markTargets)
        }
    }, [remarkTrigger]);

    /**
     * If unit can be a target of player's attack, colors bg
     */
    useEffect(() => {
        if (foe && characteristics.hp > 0) {
            if (characteristics.marktByPlayer) {
                setBgc(UNIT_BG_MARKED)
            } else {
                setBgc(UNIT_BG_DEFAULT)
            }
        }
    }, [characteristics.marktByPlayer]);

    /**
     * If unit can be a target of foe's attack, colors borders
     */
    useEffect(() => {
        if (!foe && characteristics.hp > 0) {
            if (characteristics.marktByFoe) {
                setBorders(UNIT_BORDERS_MARKED)
            } else {
                setBorders(UNIT_BORDERS_DEFAULT)
            }
        }
    }, [characteristics.marktByFoe]);

    const onMouseOver = () => {
        if (!(yourTurn && !foe)) {
            if (characteristics.marktByPlayer) {
                setBgc(UNIT_BG_ATTACK)
            } else {
                setBgc(UNIT_BG_OVER);
            }
        }
        descrFunc(characteristics);
        if (characteristics.hp > 0)
            calculateTargets(pos, foe, unitProps.markTargets)
    };

    const onMouseLeave = () => {
        if (!yourTurn) {
            if (characteristics.hp > 0) clearTargets(foe);
            setBgc(characteristics.marktByPlayer ? UNIT_BG_MARKED : UNIT_BG_DEFAULT);
        } else if (foe) {
            setBgc(UNIT_BG_DEFAULT);
        }
    };

    const validateAttack = () => {
        if (characteristics.marktByPlayer)
            selectTargets([pos]);
    };

    return characteristics ?
        <UnitPlace bgc={bgc} brd={borders}
                   onMouseOver={() => onMouseOver()}
                   onMouseLeave={() => onMouseLeave()}
                   onClick={foe ? () => validateAttack() : null}>
            {/*<div style={{height: 90, width: 80}}/>*/}
            <Img style={{maxHeight: 100, maxWidth: 80, marginLeft: 10, transform: foe ? "scaleX(-1)" : "scaleX(1)"}}
                 src={characteristics.hp === 0 ? img_skull : unitProps.image} draggable={false}/>
            <Progress animated color={"success"} value={currentHP}
                      style={{height: 5, marginRight: 4, marginLeft: 4, backgroundColor: "red"}}/>
        </UnitPlace>
        : null
}

function getUnitProps(type) {
    return UNIT_TYPES[type]()
}