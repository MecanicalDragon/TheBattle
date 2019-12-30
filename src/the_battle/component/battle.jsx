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
import * as SquadService from '@/service/SquadService'
import {FormattedMessage} from 'react-intl';

class BattleComp extends Component {

    constructor(props) {
        super(props);
        this.state = {};
        this.props.dispatch(setNavPosition(Battle));
        SquadService.getSquad().then(resp => this.setState({squad: resp}));
    }

    render() {
        let {squad} = this.state;
        return (
            <Container>
                <Jumbotron>
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