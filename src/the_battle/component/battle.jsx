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
import * as BattleService from '@/service/BattleService'
import {getPlayerName} from "@/service/PlayerService";

class BattleComp extends Component {

    constructor(props) {
        super(props);
        this.props.dispatch(setNavPosition(Battle));
        let bud = BattleService.getBattleUuid();
        let playerName = getPlayerName();
        this.state = {
            bud: bud,
            playerName: playerName,
            mySquad: undefined,
            foesSquad: undefined,
            foesName: null
        };
    }

    componentDidMount() {
        let {playerName} = this.state;
        let {bud} = this.state;
        BattleService.getBattle(playerName, bud).then(foes => {
            try {
                if (foes.uuid === bud) {
                    let mySquad = null;
                    let foesSquad = null;
                    switch (playerName) {
                        case foes.foe1.player.name:
                            mySquad = foes.foe1;
                            foesSquad = foes.foe2;
                            break;
                        case foes.foe2.player.name:
                            mySquad = foes.foe2;
                            foesSquad = foes.foe1;
                            break;
                        default:
                            throw "No such player name in loaded battle!"

                    }
                    let foesName = foesSquad.player.name;
                    this.setState({foesSquad: foesSquad, mySquad: mySquad, foesName: foesName})
                }
            } catch (e) {
                console.log(e)
            }
            console.log(this.state)
        });
    }

    render() {
        let {mySquad} = this.state;
        let {foesSquad} = this.state;
        let {playerName} = this.state;
        let {foesName} = this.state;
        return (
            <Container>
                <Jumbotron>
                    <Row>
                        <Col>
                            {playerName}
                        </Col>
                        <Col>
                            {foesName}
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            {mySquad ? mySquad.pos1.name : null}<br/>
                            {mySquad ? mySquad.pos2.name : null}<br/>
                            {mySquad ? mySquad.pos3.name : null}<br/>
                            {mySquad ? mySquad.pos4.name : null}<br/>
                            {mySquad ? mySquad.pos5.name : null}<br/>
                        </Col>
                        <Col>
                            {foesSquad ? foesSquad.pos1.name : null}<br/>
                            {foesSquad ? foesSquad.pos2.name : null}<br/>
                            {foesSquad ? foesSquad.pos3.name : null}<br/>
                            {foesSquad ? foesSquad.pos4.name : null}<br/>
                            {foesSquad ? foesSquad.pos5.name : null}<br/>
                        </Col>
                    </Row>
                </Jumbotron>
            </Container>
        )
    }
}

export default connect()(BattleComp);