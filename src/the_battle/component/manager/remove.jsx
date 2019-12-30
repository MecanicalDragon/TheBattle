import React, {useState} from 'react';
import {FormattedMessage} from "react-intl";
import {Droppable} from "react-beautiful-dnd";
import styled from "styled-components";

const Bin = styled.div`
opacity: ${props => (props.show ? '1.0' : '0.0')}
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    width: 200px;
    height: 100px;
    background-color: ${props => (props.drag ? 'orangered' : 'red')}
    border-style: solid;
    border-width: 1px;
    border-radius: 7px;;
    justify-content: center;
    align-items: center;
    border-color: red;
    color: white;
`;


const Remove = (props) => {

    return (
        <Droppable droppableId={"remove"}>
            {(provided, snapshot) => (
                <Bin ref={provided.innerRef} {...provided.droppableProps} drag={snapshot.isDraggingOver}
                     show={props.show}>
                    <FormattedMessage id={"app.manage.delete.unit"}/>
                    {provided.placeholder}
                </Bin>
            )}
        </Droppable>
    );
};

export default Remove