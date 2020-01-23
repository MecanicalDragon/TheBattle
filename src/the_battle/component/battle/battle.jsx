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
import {performAction} from "@/service/ActionService";
import {ATTACK} from "@/constants/ingameConstants";
import SockJsClient from 'react-stomp';
import Console from "@/component/battle/Console";

class BattleComp extends Component {

    constructor(props) {
        super(props);
        this.props.dispatch(setNavPosition(Battle));
        this.state = {
            playerName: getPlayerName(),
            myDescr: "",
            foesDescr: "",
            mySquad: undefined,
            foesSquad: undefined,
            foesName: null,
            actionMan: {
                id: -1,
                pos: "",
                player: false
            },
            battleLogs: ""
        };
    }

    componentDidMount() {
        let {playerName} = this.state;
        BattleService.getBattle(playerName).then(resp => {
            if (resp) {
                let mySquad = resp.foe1.playerName === playerName ? resp.foe1 : resp.foe2;
                let foesSquad = resp.foe1.playerName === playerName ? resp.foe2 : resp.foe1;
                let foesName = foesSquad.playerName;
                let actionMan = this.defineActionMan(resp.actionMan, mySquad, foesSquad);
                this.setState({
                    foesSquad: foesSquad,
                    mySquad: mySquad,
                    foesName: foesName,
                    actionMan: actionMan
                });
            }
        });
    }

    render() {
        let {mySquad} = this.state;
        let {foesSquad} = this.state;
        let {playerName} = this.state;
        let {foesName} = this.state;
        let {actionMan} = this.state;
        return (
            <Container>
                <Jumbotron style={{paddingLeft: 10, paddingRight: 10}}>
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
                                             calculateTargets={this.calculateTargets} actionManId={actionMan.id}
                                             simpleAction={this.simpleAction}/>
                                : null}
                        </Col>
                        <Col>
                            {foesSquad ?
                                <BattleSquad foe={true} squad={foesSquad} clearTargets={this.clearTargets}
                                             calculateTargets={this.calculateTargets}
                                             selectTargets={this.selectTargets} actionManId={actionMan.id}/>
                                : null}
                        </Col>
                    </Row>
                    <Row style={{justifyContent: "center", marginTop: 15}}>
                        <Console battleLogs={this.state.battleLogs}/>
                    </Row>
                </Jumbotron>
                <SockJsClient url='http://localhost:9191/battleStomp' topics={['/battle/' + this.state.playerName]}
                              onMessage={(msg) => this.actionPerformed(msg)}
                              ref={(client) => {
                                  this.clientRef = client
                              }}/>
            </Container>
        )
    }

    actionPerformed = (msg) => {
        let actionMan = this.defineActionMan(msg.nextUnit);
        if (msg.action === ATTACK) {
            this.setState({
                actionMan: actionMan,
                battleLogs: msg.comments,
                mySquad: msg.additionalData.DAMAGED_SQUAD
            });
        } else {
            this.setState({
                actionMan: actionMan,
                battleLogs: msg.comments
            });
        }
    };

    defineActionMan = (nextUnit, mySquad, foesSquad) => {

        let getPosition = function (sq, pos) {
            return sq[pos].id === actionMan.id;
        };

        let ms = mySquad || this.state.mySquad;
        let fs = foesSquad || this.state.foesSquad;
        let actionMan = {
            id: nextUnit.id,
            pos: "",
            player: undefined
        };
        let arr = ["pos1", "pos2", "pos3", "pos4", "pos5"];
        for (let i = 0; i < arr.length; i++) {
            if (getPosition(ms, arr[i])) {
                actionMan.pos = arr[i];
                actionMan.player = true;
            }
        }
        if (actionMan.player === undefined) {
            for (let i = 0; i < arr.length; i++) {
                if (getPosition(fs, arr[i])) {
                    actionMan.pos = arr[i];
                    actionMan.player = false;
                }
            }
        }
        return actionMan;
    };

    simpleAction = (action) => {
        let {actionMan, playerName} = this.state;
        performAction(playerName, actionMan.pos, action).then(resp => {
            let newActionMan = this.defineActionMan(resp.nextUnit);
            this.setState({
                actionMan: newActionMan,
                mySquad: {
                    ...this.state.mySquad, [actionMan.pos]: {
                        ...this.state.mySquad[actionMan.pos], picked: false
                    }
                },
                battleLogs: resp.comments
            });
        })
    };

    selectTargets = (targets) => {
        let {playerName, actionMan} = this.state;
        let data = {targets: targets};
        performAction(playerName, actionMan.pos, ATTACK, data).then(resp => {
            if (resp) {
                let foe = resp.additionalData.DAMAGED_SQUAD;
                let newActionMan = this.defineActionMan(resp.nextUnit);
                this.setState({
                    foesSquad: foe,
                    actionMan: newActionMan,
                    mySquad: {
                        ...this.state.mySquad, [actionMan.pos]: {
                            ...this.state.mySquad[actionMan.pos], picked: false
                        }
                    },
                    battleLogs: resp.comments
                });
            }
        })
    };

    /**
     * Invoked on mouse over unit
     * @param pos - position in a squad
     * @param foe - boolean of player's/foe's squad
     * @param mark - mark function of unit type
     */
    calculateTargets = (pos, foe, mark) => {
        let attacker = foe === true ? this.state.foesSquad : this.state.mySquad;
        let target = foe === true ? this.state.mySquad : this.state.foesSquad;

        target.pos1.marked = false;
        target.pos2.marked = false;
        target.pos3.marked = false;
        target.pos4.marked = false;
        target.pos5.marked = false;

        let targets = mark(pos, attacker, target);
        targets.forEach(function (pos) {
            target[pos].marked = true;
        });

        if (!foe) {
            this.setState({foesSquad: target});
        } else {
            this.setState({mySquad: target});
        }
    };

    /**
     * Clears target marks from units.
     * Invoked on mouseLeave
     * @param foe
     */
    clearTargets = (foe) => {
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
    };
}

export default connect()(BattleComp);