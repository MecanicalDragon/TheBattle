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
import BattleSquad from "@/component/battle/BattleSquad";
import {performAttack} from "@/service/AttackService";

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
            foesName: null,
            attacker: undefined,
            targets: [],
            lockTargets: false
        };
    }

    componentDidMount() {
        let {playerName} = this.state;
        BattleService.getBattle(playerName).then(foes => {
            if (foes) {
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
                            <h3>{playerName}</h3>
                        </Col>
                        <Col/>
                        <Col xs={"auto"}>
                            <h3>{foesName}</h3>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            {mySquad ?
                                <BattleSquad foe={false} squad={mySquad} clearTargets={this.clearTargets}
                                             calculateTargets={this.calculateTargets} pickAttacker={this.pickAttacker}/>
                                : null}
                        </Col>
                        <Col>
                            {foesSquad ?
                                <BattleSquad foe={true} squad={foesSquad} clearTargets={this.clearTargets}
                                             calculateTargets={this.calculateTargets}
                                             selectTargets={this.selectTargets}/>
                                : null}
                        </Col>
                    </Row>
                </Jumbotron>
            </Container>
        )
    }

    pickAttacker = (attacker) => {
        if (attacker === this.state.attacker) {
            this.setState({
                attacker: undefined, lockTargets: false, mySquad: {
                    ...this.state.mySquad, [attacker]: {
                        ...this.state.mySquad[attacker], picked: false
                    }
                }
            });
        } else if (this.state.attacker === undefined) {
            this.setState({
                attacker: attacker, lockTargets: true, mySquad: {
                    ...this.state.mySquad, [attacker]: {
                        ...this.state.mySquad[attacker], picked: true

                    }
                }
            });
        }
    };

    selectTargets = (targets) => {
        this.setState({targets: targets});
        let {playerName, attacker} = this.state;
        performAttack(playerName, attacker, targets).then(r => {
            if (r) {
                console.log("perform attack!");
                console.log(r);

            }
        })
    };

    calculateTargets = (pos, foe, mark) => {
        if (!this.state.lockTargets) {
            let attacker = foe === true ? this.state.foesSquad : this.state.mySquad;
            let target = foe === true ? this.state.mySquad : this.state.foesSquad;
            let targets = mark(pos, attacker, target);
            targets.forEach(function (pos) {
                target[pos].marked = true;
            });
            if (!foe) {
                this.setState({foesSquad: target});
            } else {
                this.setState({mySquad: target});
            }
        }
    };

    clearTargets = (foe) => {
        if (!this.state.lockTargets) {
            let clearMark = function (squad) {
                squad.pos1.marked = false;
                squad.pos2.marked = false;
                squad.pos3.marked = false;
                squad.pos4.marked = false;
                squad.pos5.marked = false;
            };
            if (foe) {
                let squad = this.state.mySquad;
                clearMark(squad);
                this.setState({mySquad: squad});
            } else {
                let squad = this.state.foesSquad;
                clearMark(squad);
                this.setState({foesSquad: squad});
            }
        }
    };
}

export default connect()(BattleComp);