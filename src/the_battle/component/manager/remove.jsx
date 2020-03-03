import React from 'react';
import {FormattedMessage} from "react-intl";
import {Droppable} from "react-beautiful-dnd";
import styled from "styled-components";

const Bin = styled.div`
    opacity: ${props => (props.show ? '1.0' : '0.0')}
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    width: 201px;
    height: 100px;
    background-color: ${props => (props.drag ? 'orangered' : 'red')}
    border-style: solid;
    border-width: 4px;
    border-radius: 10px;
    border-color: darkred;
    justify-content: center;
    align-items: center;
    color: white;
`;


const Remove = (props) => {

    return (
        <Droppable droppableId={"remove"}>
            {(provided, snapshot) => (
                <Bin ref={provided.innerRef} {...provided.droppableProps} drag={snapshot.isDraggingOver}
                     show={props.show}>
                    {snapshot.isDraggingOver ? "" : <FormattedMessage id={"app.manage.delete.unit"}/>}
                    {provided.placeholder}
                </Bin>
            )}
        </Droppable>
    );
};

export default Remove