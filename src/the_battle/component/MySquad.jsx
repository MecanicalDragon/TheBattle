import React, {Component, Fragment} from 'react';
import {connect} from 'react-redux';
import {
    Container,
    Jumbotron,
    Button,
    Row,
    Col
} from 'reactstrap'
import * as routes from '@/router/routes'
import {setNavPosition} from "@/constants/actions";
import {Unit} from "./Unit";
import * as SquadService from '../service/SquadService'
import {FormattedMessage} from 'react-intl';

export default class MySquad extends Component {
    constructor(props) {
        super(props);
        this.state = {
            type: this.props.type
        }
    }

    componentDidUpdate(prevProps) {
        if (this.props.type !== prevProps.type) {
            this.setState({type: this.props.type});
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
        return (
            <Fragment>
                <div className={"unitPlace smallRow"}>empty</div>
                <div className={"unitPlace smallRow"}>empty</div>
            </Fragment>
        )
    }

    getLongRow() {
        return (
            <Fragment>
                <div className={"unitPlace"}>empty</div>
                <div className={"unitPlace"}>empty</div>
                <div className={"unitPlace"}>empty</div>
            </Fragment>
        )
    }
}

