import React, {Component, Fragment} from 'react';
import {
    Row,
} from 'reactstrap'
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
    top: 45px;
`;


export default class MySquad extends Component {
    constructor(props) {
        super(props);
        this.state = {
            type: this.props.type,
            pool: this.props.pool,
            short1: this.props.short1,
            short2: this.props.short2,
            long1: this.props.long1,
            long2: this.props.long2,
            long3: this.props.long3,
        }
    }

    componentDidUpdate(prevProps) {
        if (this.props.type !== prevProps.type) {
            this.setState({type: this.props.type});
        }
        if (this.props.pool !== prevProps.pool) {
            this.setState({pool: this.props.pool});
        }
        if (this.props.short1 !== prevProps.short1) {
            this.setState({short1: this.props.short1});
        }
        if (this.props.short2 !== prevProps.short2) {
            this.setState({short2: this.props.short2});
        }
        if (this.props.long1 !== prevProps.long1) {
            this.setState({long1: this.props.long1});
        }
        if (this.props.long2 !== prevProps.long2) {
            this.setState({long2: this.props.long2});
        }
        if (this.props.long3 !== prevProps.long3) {
            this.setState({long3: this.props.long3});
        }
    }

    render() {
        let smallRow = this.getSmallRow();
        let longRow = this.getLongRow();
        return (
            <Row style={{marginLeft: 10, marginRight: 10}}>
                <div>
                    {this.state.type === 1 ? smallRow : longRow}
                </div>
                <div>
                    {this.state.type === 2 ? smallRow : longRow}
                </div>
            </Row>
        )
    }

    getSmallRow() {
        let {pool, short1, short2, long1, long2, long3} = this.state;
        return (
            <Fragment>
                <Droppable droppableId={"short1"}>
                    {(provided, snapshot) => (
                        <ShortLine ref={provided.innerRef}
                                   style={snapshot.isDraggingOver ?
                                       {backgroundColor: "var(--droppable-ready-color)"} : {backgroundColor: "azure"}}
                                   {...provided.droppableProps}>
                            {
                                short1.map((unit, index) => {
                                    return (
                                        <Unit key={unit} characteristics={pool.get(unit)}
                                              descrFunc={this.props.descrFunc} index={index}/>
                                    )
                                })
                            }
                            {provided.placeholder}
                        </ShortLine>
                    )}
                </Droppable>
                <Droppable droppableId={"short2"}>
                    {(provided, snapshot) => (
                        <ShortLine ref={provided.innerRef}
                                   style={snapshot.isDraggingOver ?
                                       {backgroundColor: "var(--droppable-ready-color)"} : {backgroundColor: "azure"}}
                                   {...provided.droppableProps}>
                            {
                                short2.map((unit, index) => {
                                    return (
                                        <Unit key={unit} characteristics={pool.get(unit)}
                                              descrFunc={this.props.descrFunc} index={index}/>
                                    )
                                })
                            }
                            {provided.placeholder}
                        </ShortLine>
                    )}
                </Droppable>
            </Fragment>
        )
    }

    getLongRow() {
        let {pool, short1, short2, long1, long2, long3} = this.state;
        return (
            <Fragment>
                <Droppable droppableId={"long1"}>
                    {(provided, snapshot) => (
                        <LongLine ref={provided.innerRef}
                                  style={snapshot.isDraggingOver ?
                                      {backgroundColor: "var(--droppable-ready-color)"} : {backgroundColor: "azure"}}
                                  {...provided.droppableProps}>
                            {
                                long1.map((unit, index) => {
                                    return (
                                        <Unit key={unit} characteristics={pool.get(unit)}
                                              descrFunc={this.props.descrFunc} index={index}/>
                                    )
                                })
                            }
                            {provided.placeholder}
                        </LongLine>
                    )}
                </Droppable>
                <Droppable droppableId={"long2"}>
                    {(provided, snapshot) => (
                        <LongLine ref={provided.innerRef}
                                  style={snapshot.isDraggingOver ?
                                      {backgroundColor: "var(--droppable-ready-color)"} : {backgroundColor: "azure"}}
                                  {...provided.droppableProps}>
                            {
                                long2.map((unit, index) => {
                                    return (
                                        <Unit key={unit} characteristics={pool.get(unit)}
                                              descrFunc={this.props.descrFunc} index={index}/>
                                    )
                                })
                            }
                            {provided.placeholder}
                        </LongLine>
                    )}
                </Droppable>
                <Droppable droppableId={"long3"}>
                    {(provided, snapshot) => (
                        <LongLine ref={provided.innerRef}
                                  style={snapshot.isDraggingOver ?
                                      {backgroundColor: "var(--droppable-ready-color)"} : {backgroundColor: "azure"}}
                                  {...provided.droppableProps}>
                            {
                                long3.map((unit, index) => {
                                    return (
                                        <Unit key={unit} characteristics={pool.get(unit)}
                                              descrFunc={this.props.descrFunc} index={index}/>
                                    )
                                })
                            }
                            {provided.placeholder}
                        </LongLine>
                    )}
                </Droppable>
            </Fragment>
        )
    }
}

