import React, {Component} from 'react';
import {connect} from 'react-redux';
import {
    Container,
    Jumbotron,
    Button,
    Row,
    Col
} from 'reactstrap'
import {setNavPosition} from "@/constants/actions";
import {Battle} from "@/constants/paths";
import MySquad from "@/component/MySquad";
import * as BattleService from '../service/BattleService'
import {FormattedMessage} from 'react-intl';

class BattleComp extends Component {

    constructor(props) {
        super(props);
        this.state = {};
        this.props.dispatch(setNavPosition(Battle));
        BattleService.getSquad().then(resp => this.setState({squad: resp}));
    }

    render() {
        let {squad} = this.state;
        return (
            <Container>
                <Jumbotron>
                        <MySquad squad={squad}/>
                    <Row>
                        <Col>
                            <Button style={{marginTop: "20px"}} onClick={() => {
                                console.log(this.state.squad)
                            }}>Print</Button>
                        </Col>
                    </Row>
                </Jumbotron>
            </Container>
        )
    }
}

export default connect()(BattleComp);