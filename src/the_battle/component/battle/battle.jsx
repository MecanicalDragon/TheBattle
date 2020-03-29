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
import * as routes from "@/router/routes";
import {FormattedMessage} from "react-intl";

const appApi = DEPLOYED_URL;

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
            lastMove: 0,
            actionMan: {
                id: -1,
                pos: "",
                player: false
            },
            battleLogs: "",
            battleWon: false,
            badReq: false
        };
    }

    componentDidMount() {
        BattleService.getBattle().then(resp => {
            if (resp) {
                let {playerName} = this.state;
                let mySquad = resp.foe1.playerName === playerName ? resp.foe1 : resp.foe2;
                let foesSquad = resp.foe1.playerName === playerName ? resp.foe2 : resp.foe1;
                let foesName = foesSquad.playerName;
                let actionMan = this.defineActionMan(resp.actionMan, mySquad, foesSquad);

                // let time = resp.lastMove.split(":");
                // let lastMove = new Date();
                // lastMove.setHours(time[0]);
                // lastMove.setMinutes(time[1]);
                // lastMove.setSeconds(time[2]);
                // time = lastMove.getTime();
                // let now = new Date().getTime();
                // if (time + 20000 < now) {
                //     console.log("ping")
                // } else {
                //     setTimeout(function () {
                //         console.log("delayed ping")
                //     }, time + 20000 - now)
                // }

                this.setState({
                    // lastMove: time,
                    foesSquad: foesSquad,
                    mySquad: mySquad,
                    foesName: foesName,
                    actionMan: actionMan
                });
            } else {
                this.setState({battleWon: true, badReq: true})
            }
        });
    }

    render() {
        let {mySquad, foesSquad, playerName, foesName, actionMan, battleWon} = this.state;
        return (
            <Container>
                <Jumbotron style={{paddingLeft: 10, paddingRight: 10}}>
                    <Row>
                        <Col>
                            {mySquad ?
                                <BattleSquad foe={false} squad={mySquad} clearTargets={this.clearTargets}
                                             calculateTargets={this.calculateTargets} actionMan={actionMan}
                                             simpleAction={this.simpleAction} playerName={playerName}
                                             won={battleWon}/>
                                : null}
                        </Col>
                        <Col>
                            {foesSquad ?
                                <BattleSquad foe={true} squad={foesSquad} clearTargets={this.clearTargets}
                                             calculateTargets={this.calculateTargets} actionMan={actionMan}
                                             selectTargets={this.selectTargets} playerName={foesName}
                                             won={battleWon}/>
                                : null}
                        </Col>
                    </Row>
                    {this.state.badReq ? null :
                        <Row style={{justifyContent: "center", marginTop: 15}}>
                            <Console battleLogs={this.state.battleLogs}/>
                        </Row>
                    }
                    {battleWon ?
                        <Row>
                            <Col style={{textAlign: "center", cursor: "pointer", color: "var(--magenta-color)"}}
                                 onClick={() => this.props.history.push(routes.manage())}>
                                {
                                    this.state.badReq ? <FormattedMessage id={'app.battle.over'}/> : null
                                }
                                <h1>EXIT</h1>
                            </Col>
                        </Row>
                        : null
                    }
                </Jumbotron>
                <SockJsClient url={appApi + 'battleStomp'} topics={['/battle/' + this.state.playerName]}
                              onMessage={(msg) => this.actionPerformed(msg)}
                              ref={(client) => {
                                  this.clientRef = client
                              }}/>
            </Container>
        )
    }

    /**
     * Receiving info by websocket about opponent's turn results
     * @param msg - ws message
     */
    actionPerformed = (msg) => {
        let actionMan = this.defineActionMan(msg.nextUnit);
        if (msg.action === ATTACK) {
            this.setState({
                mySquad: msg.additionalData.DAMAGED_SQUAD,
                battleLogs: msg.comments,
                actionMan: actionMan,
                battleWon: msg.finished
            });
        } else {
            this.setState({
                actionMan: actionMan,
                battleLogs: msg.comments,
                battleWon: msg.finished
            });
        }
    };

    /**
     * Just defining the unit, who's turn
     * @param nextUnit - from response
     * @param mySquad - player's squad
     * @param foesSquad opponent's squad
     * @returns {{pos: string, id: *, player: *}} - this.state.actionMan
     */
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

    /**
     * Perform simple action like WAIT or BLOCK
     * @param action - action itself
     */
    simpleAction = (action) => {
        if (action === ATTACK) {
            alert("This button is just for fancy view here, but others work, we assure)")
        } else {
            let {actionMan} = this.state;
            performAction(actionMan.pos, action).then(resp => {
                if (resp) {
                    let newActionMan = this.defineActionMan(resp.nextUnit);
                    this.setState({
                        actionMan: newActionMan,
                        battleLogs: resp.comments,
                        battleWon: resp.finished
                    });
                }
            })
        }
    };

    /**
     * Pick attack targets and perform attack action on them
     * @param targets - targets positions
     */
    selectTargets = (targets) => {
        let {actionMan} = this.state;
        let data = {targets: targets};
        performAction(actionMan.pos, ATTACK, data).then(resp => {
            if (resp) {
                let foe = resp.additionalData.DAMAGED_SQUAD;
                let newActionMan = this.defineActionMan(resp.nextUnit);
                this.setState({
                    foesSquad: foe,
                    actionMan: newActionMan,
                    battleLogs: resp.comments,
                    battleWon: resp.finished
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
            if (target[pos].hp > 0)
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
     * @param foe - define player's or opponent's squad
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