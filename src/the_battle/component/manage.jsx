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
import {Manage} from "@/constants/paths";
import MySquad from "@/component/MySquad";
import * as SquadService from '@/service/SquadService'
import {getPlayerName} from '@/service/PlayerService'
import {FormattedMessage} from 'react-intl';

class ManageComp extends Component {

    constructor(props) {
        super(props);
        this.state = {
            playerName: getPlayerName(),
            squad: []
        };
        this.props.dispatch(setNavPosition(Manage));
    }

    componentDidMount() {
        SquadService.getPool().then(resp => this.setState({squad: resp}));
        console.log(this.state.squad);
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

export default connect()(ManageComp);