import React, {useState} from "react";
import styled from "styled-components";
import DescriptionArea from "@/component/common/DescriptionArea";
import {BattleUnit} from "@/component/battle/BattleUnit";
import ControlPanel from "@/component/battle/ControlPanel";

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

    let {foe, squad, calculateTargets, clearTargets, pickActor, selectTargets, actionManId, simpleAction} = props;

    const [description, setDescription] = useState("");

    const getLine = (...units) => {
        let positions = units.length < 3 ? ["pos2", "pos4"] : ["pos1", "pos3", "pos5"];
        return (
            <Column style={units.length < 3 ? {position: "relative", top: 50} : {}}>{
                units.map((unit, index) => {
                        return (
                            <BattleUnit key={index} characteristics={unit} descrFunc={setDescription} foe={foe}
                                        calculateTargets={calculateTargets} pos={positions[index]}
                                        clearTargets={clearTargets} pickActor={foe ? null : pickActor}
                                        selectTargets={foe ? selectTargets : null} yourTurn={actionManId === unit.id}/>
                        )
                    }
                )
            }
            </Column>
        )
    };

    return (
        <Field foe={foe}>
            <ControlPanel simpleAction={simpleAction} foe={foe}/>
            <DescriptionArea description={description}/>
            {squad ?
                <Squad straight={(squad.type === "FORCED_BACK" && !foe) || (squad.type !== "FORCED_BACK" && foe)}>
                    {getLine(squad.pos1, squad.pos3, squad.pos5)}
                    {getLine(squad.pos2, squad.pos4)}
                </Squad>
                : null}
        </Field>
    )
};

export default BattleSquad