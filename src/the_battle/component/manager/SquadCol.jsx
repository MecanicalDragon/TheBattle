import React from 'react';
import {Droppable} from "react-beautiful-dnd";
import styled from "styled-components";
import {Unit} from "@/component/manager/Unit";

const LongLine = styled.div`
    background-color: ${props => (props.drag ? 'cyan' : 'azure')};
    display: flex;
    flex-direction: column;
    width: 177px;
    height: 90px;
    border-style: solid;
    border-width: 1px;
    border-radius: 10px;
    text-align: center;
    border-color: darkgray;
    margin: 2px;
    position: relative;
`;

const ShortLine = styled.div`
    background-color: ${props => (props.drag ? 'cyan' : 'azure')};
    display: flex;
    flex-direction: column;
    width: 177px;
    height: 90px;
    border-style: solid;
    border-width: 1px;
    border-radius: 10px;
    text-align: center;
    border-color: darkgray;
    margin: 2px;
    position: relative;
`;

const SquadCol = (props) => {
    if (props.short)
        return (
            <Droppable droppableId={props.col.id}>
                {(provided, snapshot) => (
                    <ShortLine ref={provided.innerRef} drag={snapshot.isDraggingOver}
                               {...provided.droppableProps}>
                        {
                            props.col.heroes.map((unit, index) => {
                                return (
                                    <Unit key={unit} characteristics={props.pool.get(unit)}
                                          descrFunc={props.descrFunc} index={index}/>
                                )
                            })
                        }
                        {provided.placeholder}
                    </ShortLine>
                )}
            </Droppable>
        );
    else return (
        <Droppable droppableId={props.col.id}>
            {(provided, snapshot) => (
                <LongLine ref={provided.innerRef} drag={snapshot.isDraggingOver}
                          {...provided.droppableProps}>
                    {
                        props.col.heroes.map((unit, index) => {
                            return (
                                <Unit key={unit} characteristics={props.pool.get(unit)}
                                      descrFunc={props.descrFunc} index={index}/>
                            )
                        })
                    }
                    {provided.placeholder}
                </LongLine>
            )}
        </Droppable>
    )
};

export default SquadCol