import React, {Fragment, useState, useEffect} from "react";
import styled from "styled-components";
import DescriptionArea from "@/component/common/DescriptionArea";
import {BattleUnit} from "@/component/battle/BattleUnit";
import ControlPanel from "@/component/battle/ControlPanel";
import Header from "@/component/battle/Header";

const Field = styled.div`
    display: flex;
    flex-direction: ${props => (props.foe ? 'row-reverse' : 'row')}
`;

const Squad = styled.div`
    display: flex;
    flex-direction: ${props => (props.straight ? 'row' : 'row-reverse')}
`;

const Column = styled.div`
    display: flex;
    flex-direction: column;
`;

const BattleSquad = (props) => {

    let {foe, squad, calculateTargets, clearTargets, selectTargets, actionMan, simpleAction, playerName, won, ava, twoTurns} = props;

    const [description, setDescription] = useState("");
    const [remarkTrigger, increaseTrigger] = useState(0);

    /**
     * If unit makes second turn in a row, triggers target marking again.
     */
    useEffect(() => {
        increaseTrigger(remarkTrigger + 1)
    }, [twoTurns]);

    const getLine = (...units) => {
        let positions = units.length < 3 ? ["pos2", "pos4"] : ["pos1", "pos3", "pos5"];
        return (
            <Column style={units.length < 3 ? {position: "relative", top: 50, height: 200} : {}}
                    onMouseLeave={() => increaseTrigger(remarkTrigger + 1)}>{
                units.map((unit, index) => {
                        return (
                            <BattleUnit key={index} characteristics={unit} descrFunc={setDescription} foe={foe}
                                        calculateTargets={calculateTargets} pos={positions[index]}
                                        clearTargets={clearTargets} selectTargets={foe ? selectTargets : null}
                                        yourTurn={actionMan.id === unit.id} remarkTrigger={remarkTrigger}/>
                        )
                    }
                )
            }
            </Column>
        )
    };

    return (
        <Fragment>
            <Header foe={foe} playerName={playerName} yourTurn={actionMan.player} won={won}/>
            <Field foe={foe}>
                <ControlPanel simpleAction={simpleAction} foe={foe} yourTurn={actionMan.player} won={won} ava={ava}/>
                <DescriptionArea description={description}/>
                {squad ?
                    <Squad className={"unselectable"} style={foe ? {marginRight: 5} : {marginLeft: 5}}
                           straight={(squad.type === "FORCED_BACK" && !foe) || (squad.type !== "FORCED_BACK" && foe)}>
                        {getLine(squad.pos1, squad.pos3, squad.pos5)}
                        {getLine(squad.pos2, squad.pos4)}
                    </Squad>
                    : null}
            </Field>
        </Fragment>
    )
};

export default BattleSquad