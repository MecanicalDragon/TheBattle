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

export default class Pool extends Component {
    constructor(props) {
        super(props);
        this.state = {
            pool: []
        };
    }

    componentDidMount() {
        SquadService.getPool().then(resp => {
            console.log(resp)
            this.setState({pool: resp})
        });
    }

    render() {
        let {pool} = this.state;
        return (
            <Fragment>
                <h2><FormattedMessage id={"app.manage.pool.header"}/></h2>
                <Row style={{marginBottom: 15}}>
                    {
                        pool.map((unit, index) => {
                            return (
                                <Unit key={index} characteristics={unit} descrFunc={this.props.descrFunc}/>
                            )
                        })
                    }
                </Row>
            </Fragment>
        )
    }
}

