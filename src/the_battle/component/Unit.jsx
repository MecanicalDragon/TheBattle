import React, {Component} from 'react';
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
import {Battle} from "@/constants/paths";
import * as SquadService from '../service/SquadService'
import {FormattedMessage} from 'react-intl';

function Unit(props) {
    const {characteristics, row, setDescr} = props;
    return (
        <Row>
            <Col style={row === 0 ? {marginLeft: "50px"} : {}} className={"unitLogo"}
                 onMouseOver={() => setDescr(characteristics)}>
                {characteristics ? characteristics.name : "pending..."}
            </Col>
        </Row>
    )
}

export {Unit}