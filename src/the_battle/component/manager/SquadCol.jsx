import React from 'react';
import {Droppable} from "react-beautiful-dnd";
import styled from "styled-components";
import {Unit} from "@/component/Unit";

const LongLine = styled.div`
    display: flex;
    flex-direction: column;
    width: 175px;
    height: 90px;
    background-color: white;
    border-style: solid;
    border-width: 1px;
    border-radius: 7px;
    text-align: center;
    border-color: darkgray;
    margin: 2px;
    position: relative;
`;

const ShortLine = styled.div`
    display: flex;
    flex-direction: column;
    width: 175px;
    height: 90px;
    background-color: white;
    border-style: solid;
    border-width: 1px;
    border-radius: 7px;
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
                    <ShortLine ref={provided.innerRef}
                               style={snapshot.isDraggingOver ?
                                   {backgroundColor: "var(--droppable-ready-color)"} : {backgroundColor: "azure"}}
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
                <LongLine ref={provided.innerRef}
                           style={snapshot.isDraggingOver ?
                               {backgroundColor: "var(--droppable-ready-color)"} : {backgroundColor: "azure"}}
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