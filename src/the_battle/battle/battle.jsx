import React, {Component} from 'react';
import {connect} from 'react-redux';
import {
    Container,
    Jumbotron,
    Row,
    Col
} from 'reactstrap'
import {setNavPosition} from "@/constants/actions";
import {Battle} from "@/constants/paths";
import * as BattleService from '@/service/BattleService'
import {getPlayerName} from "@/service/PlayerService";
import BattleSquad from "@/component/common/BattleSquad";

class BattleComp extends Component {

    constructor(props) {
        super(props);
        this.props.dispatch(setNavPosition(Battle));
        let playerName = getPlayerName();
        this.state = {
            playerName: playerName,
            myDescr: "",
            foesDescr: "",
            mySquad: undefined,
            foesSquad: undefined,
            foesName: null
        };
    }

    componentDidMount() {
        let {playerName} = this.state;
        BattleService.getBattle(playerName).then(foes => {
            try {
                let mySquad = null;
                let foesSquad = null;
                switch (playerName) {
                    case foes.foe1.playerName:
                        mySquad = foes.foe1;
                        foesSquad = foes.foe2;
                        break;
                    case foes.foe2.playerName:
                        mySquad = foes.foe2;
                        foesSquad = foes.foe1;
                        break;
                    default:
                        throw "No such player name in loaded battle!"

                }
                let foesName = foesSquad.playerName;
                this.setState({foesSquad: foesSquad, mySquad: mySquad, foesName: foesName})
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
                        <Col xs={"auto"}>
                            <h3>
                            {playerName}
                            </h3>
                        </Col>
                        <Col/>
                        <Col xs={"auto"}>
                            <h3>
                            {foesName}
                            </h3>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            {mySquad ? <BattleSquad foe={false} squad={mySquad}/> : null}
                        </Col>
                        <Col>
                            {foesSquad ? <BattleSquad foe={true} squad={foesSquad}/> : null}
                        </Col>
                    </Row>
                </Jumbotron>
            </Container>
        )
    }
}

export default connect()(BattleComp);