import React from 'react';
import DndUnit from './unit'
import {Droppable} from "react-beautiful-dnd";
import styled from "styled-components";

const Container = styled.div`
    background-color: azure;
    border-color: aqua;
    border-style: solid;
    border-width: 1px;
    border-radius: 15px;
    width: 190px;
    height: 720px;
    text-align: center;
    align-content: center;
    display: flex;
    flex-direction: column
    margin-left: 10px;
    margin-right: 10px;
`;

const Pool = styled.div`
flex-grow: 1;
min-height: 100px;
`;

export default class DragPool extends React.Component {

    render() {
        return (
            <Container>
                <h3>{this.props.column.title}</h3>
                <Droppable droppableId={this.props.column.id}>
                    {(provided, snapshot) => (
                        <Pool
                            ref={provided.innerRef}
                            style={snapshot.isDraggingOver ? {backgroundColor: "skyblue"} : {backgroundColor: "azure"}}
                            {...provided.droppableProps}>
                            {
                                this.props.heroes.map((hero, index) =>
                                    <DndUnit key={hero.id} hero={hero} index={index}/>)
                            }
                            {provided.placeholder}
                        </Pool>
                    )}
                </Droppable>
            </Container>
        )
    }
}