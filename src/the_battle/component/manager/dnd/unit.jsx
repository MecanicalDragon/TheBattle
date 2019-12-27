import React, {Component} from 'react';
import {Draggable} from "react-beautiful-dnd";
import styled from "styled-components";

const Container = styled.div`
    width: 175px;
    height: 90px;
    background-color: ${props => (props.isDragging ? 'lightgreen' : 'lightgray')}
    border-style: solid;
    border-width: 1px;
    border-radius: 7px;
    text-align: center;
    border-color: darkgray
    margin: 5px;
`;

export default class DndUnit extends Component {

    render() {
        return (
            <Draggable draggableId={this.props.hero.id} index={this.props.index}>
                {(provided, snapshot) => (
                    <Container
                        {...provided.draggableProps}
                        {...provided.dragHandleProps}
                        ref={provided.innerRef}
                        isDragging={snapshot.isDragging}>
                        <span>{this.props.hero.name}</span>
                        <br/>
                        <span>{this.props.hero.type}</span>
                    </Container>
                )}
            </Draggable>
        )
    }
}
