import React, {useState} from "react";
import styled from "styled-components";
import DescriptionArea from "@/component/common/DescriptionArea";
import {Unit} from "@/component/common/Unit";

const Field = styled.div`
    display: flex;
    flex-direction: ${props => (props.foe ? 'row-reverse' : 'row')}
`;

const Squad = styled.div`
    display: flex;
    flex-direction: ${props => (props.front ? 'row-reverse' : 'row')}
`;

const Column = styled.div`
    display: flex;
    flex-direction: column;
`;

const BattleSquad = (props) => {

    let {foe, squad} = props;

    const [description, setDescription] = useState("");

    const getLine = (...units) => {
        return (
            <Column style={units.length < 3 ? {position: "relative", top: 45} : {}}>{
                units.map((unit, index) => {
                        return (
                            <Unit key={index} characteristics={unit} descrFunc={setDescription} inBattle={true}/>
                        )
                    }
                )
            }
            </Column>
        )
    };

    return (
        <Field foe={foe}>
            <DescriptionArea description={description}/>
            {squad ?
                <Squad front={squad.type === "FORCED_FRONT" && !foe}>
                    {getLine(squad.pos1, squad.pos3, squad.pos5)}
                    {getLine(squad.pos2, squad.pos4)}
                </Squad>
                : null}
        </Field>
    )
};

export default BattleSquad