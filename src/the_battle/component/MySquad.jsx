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
import * as BattleService from '../service/BattleService'
import {FormattedMessage} from 'react-intl';

export default class MySquad extends Component {
    constructor(props) {
        super(props);
        this.state = {
            descr: ""
        }
    }

     setDescription = (text) => {
        let string = JSON.stringify(text).split(",").join("\n").replace("{", "")
            .replace("}", "").split("\"").join(" ").replace("name", "class");
        this.setState({descr: string});
    };

    render() {
        let {squad} = this.props;
        let {descr} = this.state;
        return (
            <Row>
                <Col xs={"auto"}>
                <textarea id={"mySquadStats"} value={descr} style={{width: "150px", height: "200px", resize: "none"}}
                          readOnly={true}/>
                </Col>
                <Col xs={"auto"}>
                    <Unit characteristics={squad ? squad.pos1 : null} setDescr={this.setDescription}
                          row={squad ? squad.type === "FORCED_FRONT" ? 0 : 1 : null}/>
                    <Unit characteristics={squad ? squad.pos2 : null} setDescr={this.setDescription}
                          row={squad ? squad.type === "FORCED_FRONT" ? 1 : 0 : null}/>
                    <Unit characteristics={squad ? squad.pos3 : null} setDescr={this.setDescription}
                          row={squad ? squad.type === "FORCED_FRONT" ? 0 : 1 : null}/>
                    <Unit characteristics={squad ? squad.pos4 : null} setDescr={this.setDescription}
                          row={squad ? squad.type === "FORCED_FRONT" ? 1 : 0 : null}/>
                    <Unit characteristics={squad ? squad.pos5 : null} setDescr={this.setDescription}
                          row={squad ? squad.type === "FORCED_FRONT" ? 0 : 1 : null}/>
                </Col>
            </Row>
        )
    }
}

