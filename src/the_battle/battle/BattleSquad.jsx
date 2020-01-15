import React, {useState} from "react";
import {Button} from 'reactstrap'
import styled from "styled-components";
import DescriptionArea from "@/component/common/DescriptionArea";
import {BattleUnit} from "@/battle/BattleUnit";

const Field = styled.div`
    display: flex;
    flex-direction: ${props => (props.foe ? 'row-reverse' : 'row')}
`;

const ControlPane = styled.div`
    width: 100px
    display: flex;
    flex-direction: column;
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

    let {foe, squad, calculateTargets} = props;

    const [description, setDescription] = useState("");

    const getLine = (...units) => {
        let positions = units.length < 3 ? ["pos2", "pos4"] : ["pos1", "pos3", "pos5"];
        return (
            <Column style={units.length < 3 ? {position: "relative", top: 50} : {}}>{
                units.map((unit, index) => {
                        return (
                            <BattleUnit key={index} characteristics={unit} descrFunc={setDescription} foe={foe}
                                        calculateTargets={calculateTargets} pos={positions[index]}/>
                        )
                    }
                )
            }
            </Column>
        )
    };

    return (
        <Field foe={foe}>
            <ControlPane>
                {/*    <Button style={{margin: 5}}>Attack!</Button>*/}
                {/*    <Button style={{margin: 5}}>Spell</Button>*/}
                {/*    <Button style={{margin: 5}}>Wait</Button>*/}
                {/*    <Button style={{margin: 5}}>Block</Button>*/}
            </ControlPane>
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