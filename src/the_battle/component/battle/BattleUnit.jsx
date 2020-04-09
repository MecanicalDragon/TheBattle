import React, {useState, useEffect} from 'react';
import {Progress} from 'reactstrap';
import styled from "styled-components";
import {UNIT_TYPES} from "./unit/unitTypes"

import Img from 'react-image'
import img_skull from '@/img/skull.png';
import {
    UNIT_FOES_TURN, UNIT_FOES_TARGET,
    UNIT_NOT_FOES_TARGET,
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
    border-style: ${props => props.brd === UNIT_NOT_FOES_TARGET ? "none" : "dashed"}
    border-color: ${props => props.brd}
    border-width: 1px;
    background-color: ${props => props.bgc}
`;

/**
 * @return {null}
 */
export function BattleUnit(props) {

    const {
        characteristics, descrFunc, foe, calculateTargets, pos, clearTargets, selectTargets, yourTurn, rt
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
                return UNIT_FOES_TURN
            } else {
                return UNIT_NOT_FOES_TARGET
            }
        } else {
            return UNIT_NOT_FOES_TARGET
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
    }, [characteristics.hp]);

    /**
     * If unit's turn, sets it's and it's targets' borders or backgrounds
     */
    //TODO: does not work if two turns in a row 
    useEffect(() => {
        if (yourTurn && foe) {
            setBorders(UNIT_FOES_TURN);
            calculateTargets(pos, foe, unitProps.markTargets)
        } else if (yourTurn && !foe) {
            setBgc(UNIT_BG_PICKED);
            calculateTargets(pos, foe, unitProps.markTargets)
        } else {
            setBorders(UNIT_NOT_FOES_TARGET);
            setBgc(UNIT_BG_DEFAULT);
        }
    }, [yourTurn]);

    useEffect(() => {
        if (yourTurn) {
            if (foe) {
                setBorders(UNIT_FOES_TURN);
            } else {
                setBgc(UNIT_BG_PICKED);
            }
            calculateTargets(pos, foe, unitProps.markTargets)
        }
    }, [rt]);

    /**
     * If unit can be a target of attack, colors bg or borders
     */
    useEffect(() => {
        if (characteristics.hp !== 0) {
            if (characteristics.marked) {
                if (foe) {
                    setBgc(UNIT_BG_MARKED)
                } else {
                    setBorders(UNIT_FOES_TARGET)
                }
            } else {
                if (foe) {
                    setBgc(UNIT_BG_DEFAULT)
                } else {
                    setBorders(UNIT_NOT_FOES_TARGET)
                }
            }
        } else {
            setBgc(UNIT_BG_DEFAULT);
            setBorders(UNIT_NOT_FOES_TARGET)
        }
    }, [characteristics.marked]);

    const onMouseOver = () => {
        if (!yourTurn) {
            if (foe && characteristics.marked) {
                setBgc(UNIT_BG_ATTACK)
            } else {
                setBgc(UNIT_BG_OVER);
            }
        } else if (yourTurn && foe) {
            setBgc(UNIT_BG_OVER);
        }
        descrFunc(characteristics);
        if (characteristics.hp !== 0)
            calculateTargets(pos, foe, unitProps.markTargets)
    };

    const onMouseLeave = () => {
        if (!yourTurn) {
            clearTargets(foe);
            setBgc(characteristics.marked && foe ? UNIT_BG_MARKED : UNIT_BG_DEFAULT);
        } else if (foe) {
            setBgc(UNIT_BG_DEFAULT);
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