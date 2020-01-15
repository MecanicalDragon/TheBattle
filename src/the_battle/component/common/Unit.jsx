import React from 'react';
import {Draggable} from "react-beautiful-dnd";
import styled from "styled-components";

const UnitPlace = styled.div`
    width: ${props => (props.narrow ? '140px' : '165px')}
    height: 80px;
`;

/**
 * @return {null}
 */
export function Unit(props) {
    const {characteristics, descrFunc, inBattle} = props;
    return characteristics ? inBattle ?
        <UnitPlace className={"unitLogo" + getBorderColor(characteristics.type.type)}
                   onMouseOver={() => descrFunc(characteristics)} narrow={inBattle}>
            {characteristics.name}<br/>
            {characteristics.type.type}<br/>
            level {characteristics.level}
        </UnitPlace>
        : (
            <Draggable draggableId={props.characteristics.id} index={props.index}>
                {(provided, snapshot) => (
                    <UnitPlace className={"unitLogo" + getBorderColor(characteristics.type.type)}
                               onMouseOver={() => descrFunc(characteristics)}
                               {...provided.draggableProps}
                               {...provided.dragHandleProps}
                               ref={provided.innerRef}
                               isDragging={snapshot.isDragging}>
                        {characteristics.name}<br/>
                        {characteristics.type.type}<br/>
                        level {characteristics.level}
                    </UnitPlace>
                )}
            </Draggable>
        )
        : null
}

function getBorderColor(type) {
    switch (type) {
        case "FIGHTER":
            return " warriorLogo";
        case "SAGE":
            return " mageLogo";
        case "RANGER":
            return " archerLogo";
        default:
            return null
    }
}