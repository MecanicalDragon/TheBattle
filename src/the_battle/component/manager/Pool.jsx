import React, {Component, Fragment} from 'react';
import {Unit} from "./Unit";
import {FormattedMessage} from 'react-intl';
import AddNew from './addNew';
import {Droppable} from "react-beautiful-dnd";
import styled from "styled-components";
import PerfectScrollbar from 'react-perfect-scrollbar'
import 'react-perfect-scrollbar/dist/css/styles.css';

const Reserve = styled.div`
    background-color: ${props => (props.drag ? 'cyan' : 'azure')}
    display: flex;
    flex-direction: column;
    min-height: 498px;
    width: 175px;
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
                <Droppable droppableId={"reserve"}>
                    {/*direction={"horizontal"}*/}
                    {(provided, snapshot) => (
                        <Fragment>
                            <PerfectScrollbar style={{
                                height: 500,
                                borderStyle: "solid",
                                borderColor: "green",
                                borderWidth: 1,
                                borderRadius: 7,
                                backgroundColor: "azure"
                            }}>
                                <Reserve ref={provided.innerRef} drag={snapshot.isDraggingOver}
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
                                </Reserve>
                            </PerfectScrollbar>
                            <AddNew addNewHero={this.props.addNewHero}/>
                        </Fragment>
                    )}
                </Droppable>
            </Fragment>
        )
    }
}

