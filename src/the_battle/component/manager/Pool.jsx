import React, {Component, Fragment} from 'react';
import {Unit} from "../Unit";
import {FormattedMessage} from 'react-intl';
import AddNew from './addNew';
import {Droppable} from "react-beautiful-dnd";
import styled from "styled-components";

const Reserve = styled.div`
    display: flex;
`;

export default class Pool extends Component {
    constructor(props) {
        super(props);
        this.state = {
            pool: this.props.pool,
            reserved: this.props.reserved
        };
    }

    componentDidUpdate(prevProps) {
        if (this.props.pool !== prevProps.pool) {
            this.setState({pool: this.props.pool});
        }
        if (this.props.reserved !== prevProps.reserved) {
            this.setState({reserved: this.props.reserved});
        }
    }

    render() {
        let {pool, reserved} = this.state;
        return (
            <Fragment>
                <h2><FormattedMessage id={"app.manage.pool.header"}/></h2>
                <Droppable droppableId={"reserve"} direction={"horizontal"}>
                    {(provided, snapshot) => (
                        <Reserve ref={provided.innerRef}
                                 style={snapshot.isDraggingOver ? {backgroundColor: "skyblue"} : {backgroundColor: "azure"}}
                                 {...provided.droppableProps}>
                            {
                                reserved.map((unit, index) => {
                                    return (
                                        <Unit key={unit} characteristics={pool.get(unit)}
                                              descrFunc={this.props.descrFunc} index={index}/>
                                    )
                                })
                            }
                            {provided.placeholder}
                            <AddNew addNewHero={this.props.addNewHero}/>
                        </Reserve>
                    )}
                </Droppable>
            </Fragment>
        )
    }
}

