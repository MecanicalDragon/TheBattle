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
import {performAction, ping} from "@/service/ActionService";
import {ATTACK, TIME_FOR_TURN} from "@/constants/ingameConstants";
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
            playersAvatar: null,
            foesAvatar : null,
            myDescr: "",
            foesDescr: "",
            mySquad: undefined,
            foesSquad: undefined,
            foesName: null,
            lastMoveTimestamp: 0,
            timeLeft: "",
            actionMan: {
                id: -1,
                pos: "",
                player: false
            },
            battleLogs: "",
            battleWon: false,
            badReq: false,
            twoTurnsInARowCounter: 0
        };
    }

    componentDidMount() {
        BattleService.getBattle().then(resp => {
            if (resp) {
                let {dislocations} = resp;
                let {playerName} = this.state;
                let mySquad = dislocations.foe1.playerName === playerName ? dislocations.foe1 : dislocations.foe2;
                let foesSquad = dislocations.foe1.playerName === playerName ? dislocations.foe2 : dislocations.foe1;
                let foesName = foesSquad.playerName;
                let actionMan = this.defineActionMan(dislocations.actionMan, mySquad, foesSquad);
                let lastMoveTimestamp = dislocations.lastMove;

                this.setState({
                    playersAvatar: resp.playersAvatar,
                    foesAvatar: resp.foesAvatar,
                    lastMoveTimestamp: lastMoveTimestamp,
                    foesSquad: foesSquad,
                    mySquad: mySquad,
                    foesName: foesName,
                    actionMan: actionMan
                });

                if (lastMoveTimestamp + TIME_FOR_TURN <= Date.now()) {
                    ping()
                } else {
                    this.startTimeCounter();
                }
            } else {
                this.setState({battleWon: true, badReq: true})
            }
        });
    }

    render() {
        let {mySquad, foesSquad, playerName, foesName, actionMan, battleWon, timeLeft
            , twoTurnsInARowCounter, playersAvatar, foesAvatar} = this.state;
        return (
            <Container>
                <Jumbotron style={{paddingLeft: 10, paddingRight: 10}}>
                    <Row style={{height: 10, textAlign: "center"}}>
                        <Col>
                            <h1 style={timeLeft < 6 ? {color: "red"} : {color: "black"}}>{timeLeft}</h1>
                        </Col>
                    </Row>
                    <Row>
                        <Col>
                            {mySquad ?
                                <BattleSquad foe={false} squad={mySquad} clearTargets={this.clearTargets}
                                             calculateTargets={this.calculateTargets} actionMan={actionMan}
                                             simpleAction={this.simpleAction} playerName={playerName}
                                             won={battleWon} twoTurns={twoTurnsInARowCounter} ava={playersAvatar}/>
                                : null}
                        </Col>
                        <Col>
                            {foesSquad ?
                                <BattleSquad foe={true} squad={foesSquad} clearTargets={this.clearTargets}
                                             calculateTargets={this.calculateTargets} actionMan={actionMan}
                                             selectTargets={this.performAttack} playerName={foesName}
                                             won={battleWon} twoTurns={twoTurnsInARowCounter} ava={foesAvatar}/>
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
                            <Col style={{textAlign: "center", cursor: "pointer", color: "var(--app-primary-color)"}}
                                 onClick={() => this.props.history.push(routes.manage())}>
                                {
                                    this.state.badReq ? <FormattedMessage id={'app.battle.over'}/> : null
                                }
                                <h1><FormattedMessage id={'app.battle.over.exit'}/></h1>
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
     * Function refreshes turn time counter every second and pings server for forcing turn shifting if time is up.
     */
    startTimeCounter = async function () {
        function sleep1sec() {
            return new Promise(resolve => setTimeout(resolve, 1000));
        }

        let last = this.state.lastMoveTimestamp;
        let now = (Date.now() / 1000).toFixed();
        let limit = ((last + TIME_FOR_TURN) / 1000).toFixed();
        while (now <= limit) {
            if (last === this.state.lastMoveTimestamp) {
                this.setState({timeLeft: limit - now});
                await sleep1sec();
                now++;
            } else return;
        }
        if (!this.state.battleWon) ping();
    };

    /**
     * Receiving info by websocket about opponent's turn results
     * @param msg - ws message
     */
    actionPerformed = (msg) => {
        let actionMan = this.defineActionMan(msg.nextUnit);
        this.setState({
            mySquad: msg.action === ATTACK ? msg.additionalData.DAMAGED_SQUAD : this.state.mySquad,
            actionMan: actionMan,
            lastMoveTimestamp: msg.lastMoveTimestamp,
            battleLogs: msg.comments,
            battleWon: msg.finished
        });
        this.startTimeCounter();
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
     * Perform simple action by button clicking like WAIT or BLOCK
     * @param action - action itself
     */
    simpleAction = (action) => {
        if (action === ATTACK) {
            this.performAttackAsSimpleAction()
        } else {
            this.doSimpleAction(action);
        }
    };

    /**
     * Attack with red button on the control panel. Picks possible target with the lowest hp
     * and performs an attack action or logs in console impossibility of this action.
     */
    performAttackAsSimpleAction = () => {
        let {foesSquad} = this.state;
        let target = null
        let targetHP = 999999999
        for (let i = 1; i < 6; i++) {
            let pos = "pos" + i;
            if (foesSquad[pos].marktByPlayer && foesSquad[pos].hp < targetHP) {
                target = pos
                targetHP = foesSquad[pos].hp
            }
        }
        if (target) this.performAttack([target])
        else {
            this.setState({
                battleLogs: this.state.mySquad[this.state.actionMan.pos].name
                    + " can not see any target to attack!"
            })
        }
    }

    /**
     * Perform attack action on targets
     * @param targets - targets positions
     */
    performAttack = (targets) => {
        this.doSimpleAction(ATTACK, {targets: targets});
    };

    /**
     * Aggregate action logic
     * @param action - action constant
     * @param adData - additionalData like targets for ATTACK-action
     */
    doSimpleAction = (action, adData) => {
        if (this.state.battleWon) return;
        let {actionMan, twoTurnsInARowCounter} = this.state;
        performAction(actionMan.pos, action, adData).then(resp => {
            if (resp) {
                let foe = resp.additionalData.DAMAGED_SQUAD;
                let newActionMan = this.defineActionMan(resp.nextUnit);
                if (newActionMan.id === actionMan.id)
                    twoTurnsInARowCounter++;
                this.setState({
                    foesSquad: foe || this.state.foesSquad,
                    actionMan: newActionMan,
                    lastMoveTimestamp: resp.lastMoveTimestamp,
                    battleLogs: resp.comments,
                    battleWon: resp.finished,
                    twoTurnsInARowCounter: twoTurnsInARowCounter
                });
                this.startTimeCounter();
            }
        })
    };

    /**
     * Invoked on mouse over unit
     * @param pos - position in a squad
     * @param foe - boolean of player's/foe's squad
     * @param mark - mark function of unit type
     * @param clear - clear own squad from targeting
     */
    calculateTargets = (pos, foe, mark, clear) => {
        let attacker = foe === true ? this.state.foesSquad : this.state.mySquad;
        let target = foe === true ? this.state.mySquad : this.state.foesSquad;
        this.clearMarking(target, foe);
        if (clear) this.clearMarking(attacker, !foe)
        let targets = mark(pos, attacker, target);
        targets.forEach(function (pos) {
            if (target[pos].hp > 0) {
                if (foe) target[pos].marktByFoe = true;
                else target[pos].marktByPlayer = true;
            }
        });

        if (foe) this.setState(clear ? {mySquad: target, foesSquad: attacker} : {mySquad: target});
        else this.setState(clear ? {foesSquad: target, mySquad: attacker} : {foesSquad: target});
    };

    /**
     * Clears target marks from units.
     * Invoked on mouseLeave
     * @param foe - define player's or opponent's squad
     */
    clearTargets = (foe) => {
        let squad = foe ? this.state.mySquad : this.state.foesSquad;
        this.clearMarking(squad, foe);
        if (foe) this.setState({mySquad: squad});
        else this.setState({foesSquad: squad});
    };

    /**
     * Clear old mark
     * @param squad - squad for clearing
     * @param foe - true if opponent, false if player
     */
    clearMarking = (squad, foe) => {
        let markType = foe ? "marktByFoe" : "marktByPlayer";
        squad.pos1[markType] = false;
        squad.pos2[markType] = false;
        squad.pos3[markType] = false;
        squad.pos4[markType] = false;
        squad.pos5[markType] = false;
    }
}

export default connect()(BattleComp);