import React, {Component, Fragment} from 'react';
import {connect} from 'react-redux';
import {Button, ButtonGroup, Row, Col} from 'reactstrap'
import {setNavPosition} from "@/constants/actions";
import {Manage} from "@/constants/paths";
import SquadCol from "@/component/manager/SquadCol";
import * as SquadService from '@/service/SquadService'
import * as BattleService from '@/service/BattleService'
import {getPlayerName} from '@/service/PlayerService'
import Pool from "@/component/manager/Pool";
import {FormattedMessage} from 'react-intl';
import {DragDropContext} from "react-beautiful-dnd";
import Remove from "@/component/manager/remove";
import {NotificationManager} from "react-notifications";
import SockJsClient from 'react-stomp';
import * as routes from '@/router/routes'
import DescriptionArea from "@/component/common/DescriptionArea";
import {STATUS} from "@/constants/ingameConstants";

const appApi = DEPLOYED_URL;

class ManageComp extends Component {

    constructor(props) {
        super(props);
        this.state = {
            playerName: getPlayerName(),
            descr: "",
            playerData: {
                status: STATUS[0]
            },
            pool: {},
            columns: {
                'reserve': {
                    id: 'reserve',
                    heroes: []
                },
                'short1': {
                    id: 'short1',
                    heroes: []
                },
                'short2': {
                    id: 'short2',
                    heroes: []
                },
                'long1': {
                    id: 'long1',
                    heroes: []
                },
                'long2': {
                    id: 'long2',
                    heroes: []
                },
                'long3': {
                    id: 'long3',
                    heroes: []
                },
                'remove': {
                    id: 'remove',
                    heroes: []
                }
            },
            sqType: 1,
            removeWindow: false,
            toBattleDisabled: false
        };
        this.props.dispatch(setNavPosition(Manage));
    }

    componentDidMount() {
        SquadService.getPool().then(resp => {
            if (resp) {
                let inReserve = [];
                for (let i of resp.pool) {
                    inReserve.push(i[0]);
                }
                this.setState({
                    playerData: resp.playerData,
                    pool: resp.pool,
                    columns: {
                        ...this.state.columns,
                        "reserve": {
                            id: 'reserve',
                            heroes: inReserve
                        }
                    }
                });
            }
        });
    }

    addNewHero = (name, type) => {
        SquadService.addNewHero(name, type).then(
            resp => {
                if (resp !== null) {
                    let updCol = this.state.columns["reserve"];
                    updCol.heroes.push(resp.id);
                    let pool = this.state.pool;
                    pool.set(resp.id, resp);
                    let newState = {
                        pool: pool,
                        columns: {
                            ...this.state.columns, ["reserve"]: updCol
                        }
                    };
                    this.setState(newState);
                }
            }
        )
    };

    retireHero = (draggableId, start, index) => {
        SquadService.retireHero(draggableId).then(resp => {
            if (resp === true) {

                const newIds = Array.from(start.heroes);
                newIds.splice(index, 1);
                const newCol = {
                    ...start,
                    heroes: newIds
                };

                const pool = this.state.pool;
                pool.delete(draggableId);
                let newState = {
                    ...this.state, pool: pool,
                    removeWindow: false,
                    columns: {
                        ...this.state.columns,
                        [newCol.id]: newCol,
                        ["remove"]: {id: "remove", heroes: []}
                    }
                };
                this.setState(newState)
            }
        })
    };

    setDescription = (text) => this.setState({descr: text});

    toBattle = () => {

        // If the battle already goes
        if (this.state.playerData.status === STATUS[2]) {
            this.props.history.push(routes.battle());
            return;
        }

        this.setState({toBattleDisabled: true});

        // If player is in process of battle searching
        if (this.state.playerData.status === STATUS[1]) {
            BattleService.cancelBid().then(
                resp => {
                    if (resp) {
                        NotificationManager.success("", <FormattedMessage id={"app.manage.cancelled"}/>, 5000);
                        this.setState({
                            toBattleDisabled: false, playerData: {
                                ...this.state.playerData,
                                status: STATUS[0]
                            }
                        })
                    } else {
                        NotificationManager.warning("", <FormattedMessage id={"app.manage.not.cancelled"}/>, 5000);
                        this.setState({toBattleDisabled: false})
                    }
                }
            );
            return;
        }

        // Otherwise
        let {columns} = this.state;
        let {sqType} = this.state;
        let {pool} = this.state;
        let define = function () {
            let s1 = columns.short1.heroes[0];
            if (s1 === undefined) return;
            let s2 = columns.short2.heroes[0];
            if (s2 === undefined) return;
            let l1 = columns.long1.heroes[0];
            if (l1 === undefined) return;
            let l2 = columns.long2.heroes[0];
            if (l2 === undefined) return;
            let l3 = columns.long3.heroes[0];
            if (l3 === undefined) return;
            squad = {
                type: sqType === 1 ? "FORCED_FRONT" : "FORCED_BACK",
                pos1: pool.get(l1),
                pos2: pool.get(s1),
                pos3: pool.get(l2),
                pos4: pool.get(s2),
                pos5: pool.get(l3)
            };
        };
        let squad = undefined;
        define();
        if (squad) {
            BattleService.battleBid(squad).then(resp => {
                if (resp.status === "AWAIT") {
                    this.setState({
                        toBattleDisabled: false, playerData: {
                            ...this.state.playerData,
                            status: STATUS[1]
                        }
                    });
                } else if (resp.status === "START") {
                    this.startBattle();
                } else {
                    this.setState({toBattleDisabled: false});
                }
            })
        } else {
            NotificationManager.error("", <FormattedMessage id={"app.manage.need5"}/>, 5000);
            this.setState({toBattleDisabled: false});
        }
    };

    startBattle = () => {
        this.props.history.push(routes.battle())
    };

    onDragStart = () => {
        this.setState({removeWindow: true})
    };

    // onDragUpdate = update => {
    //     console.log("drag update")
    // };

    onDragEnd = result => {

        const {destination, source, draggableId} = result;
        if (!destination || (destination.droppableId === source.droppableId && destination.index === source.index)) {
            this.setState({removeWindow: false});
            return;
        }

        const start = this.state.columns[source.droppableId];
        const finish = this.state.columns[destination.droppableId];

        if (destination.droppableId === "remove") {
            this.retireHero(draggableId, start, source.index);
        } else if (start === finish) {
            const newIds = Array.from(start.heroes);
            newIds.splice(source.index, 1);
            newIds.splice(destination.index, 0, draggableId);
            const newCol = {
                ...start,
                heroes: newIds
            };
            const newState = {
                ...this.state,
                removeWindow: false,
                columns: {
                    ...this.state.columns,
                    [newCol.id]: newCol
                }
            };
            this.setState(newState)
        } else {
            const startHeroes = Array.from(start.heroes);
            const finishHeroes = Array.from(finish.heroes);

            if (finishHeroes.length > 0 && finish.id !== "reserve") {
                let spliced = finishHeroes.splice(0, 1, draggableId);
                startHeroes.splice(source.index, 1, spliced[0]);
            } else {
                startHeroes.splice(source.index, 1);
                finishHeroes.splice(destination.index, 0, draggableId);
            }

            const newStart = {
                ...start, heroes: startHeroes
            };
            const newFinish = {
                ...finish, heroes: finishHeroes
            };

            const newState = {
                ...this.state,
                removeWindow: false,
                columns: {
                    ...this.state.columns,
                    [newStart.id]: newStart,
                    [newFinish.id]: newFinish,
                }
            };
            this.setState(newState);
        }
    };

    render() {
        let {sqType, playerData} = this.state;
        let smallRow = this.getSmallRow();
        let longRow = this.getLongRow();
        return (
            <Fragment>
                <DragDropContext onDragEnd={this.onDragEnd} onDragStart={this.onDragStart}
                                 onDragUpdate={this.onDragUpdate}>
                    <Row style={{textAlign: "left"}}>
                        <Col xs={"auto"}>
                            <Pool reserved={this.state.columns.reserve.heroes} pool={this.state.pool}
                                  descrFunc={this.setDescription} addNewHero={this.addNewHero}/>
                        </Col>
                        <Col xs={"auto"}>
                            <div style={{marginTop: 60, marginLeft: 38, marginBottom: 15}}>
                                <h5><FormattedMessage id={"app.manage.squad.type"}/></h5>
                                <ButtonGroup>
                                    <Button color={this.state.sqType === 2 ? "success" : "warning"}
                                            onClick={() => this.setState({sqType: 2})}
                                            active={this.state.sqType === 2}>FORCED BACK</Button>
                                    <Button color={this.state.sqType === 1 ? "success" : "warning"}
                                            onClick={() => this.setState({sqType: 1})}
                                            active={this.state.sqType === 1}>FORCED FRONT</Button>
                                </ButtonGroup>
                            </div>
                            <Row className={sqType === 1 ? "squadPlace" : "squadPlaceReverse"}>
                                {smallRow}
                                {longRow}
                            </Row>
                        </Col>
                        <Col xs={"auto"}>
                            <Col xs={"auto"} style={{marginTop: 92}}>
                                <Button onClick={() => this.toBattle()} disabled={this.state.toBattleDisabled}
                                        color={playerData.status === STATUS[1] ? "danger"
                                            : playerData.status === STATUS[0] ? "success" : "info"}
                                        style={{marginBottom: 17, marginRight: 15, width: 100}}>
                                    <FormattedMessage id={playerData.status === STATUS[1]
                                        ? "app.manage.battle.cancel" : playerData.status === STATUS[0] ?
                                            "app.manage.to.battle" : "app.manage.battle.goes"}/></Button>
                                {playerData.status === STATUS[1] ? <span>Searching...</span> : null}
                                {/*<Button onClick={() => this.fillWithFighters()} color={"info"}*/}
                                {/*        style={{marginBottom: 17, marginRight: 15}}>Test</Button>*/}
                                <br/>
                                <DescriptionArea description={this.state.descr} height={274}/>
                                <Remove show={this.state.removeWindow}/>
                            </Col>
                        </Col>
                    </Row>
                </DragDropContext>
                <SockJsClient url={appApi + 'battleStomp'} topics={['/searching/' + this.state.playerName]}
                              onMessage={(msg) => {
                                  if (msg === "GAME_FOUND") this.startBattle()
                              }}
                              ref={(client) => {
                                  this.clientRef = client
                              }}/>
            </Fragment>
        )
    }

    /**
     * Function for testing, fills squad with fighters
     */
    fillWithFighters() {
        if (this.state.playerName === "asdd")
            this.setState({
                columns: {
                    'reserve': {
                        id: 'reserve',
                        heroes: []
                    },
                    'short1': {
                        id: 'short1',
                        heroes: [14]
                    },
                    'short2': {
                        id: 'short2',
                        heroes: [15]
                    },
                    'long1': {
                        id: 'long1',
                        heroes: [16]
                    },
                    'long2': {
                        id: 'long2',
                        heroes: [17]
                    },
                    'long3': {
                        id: 'long3',
                        heroes: [18]
                    },
                    'remove': {
                        id: 'remove',
                        heroes: []
                    }
                },
            });
        if (this.state.playerName === "asds")
            this.setState({
                columns: {
                    'reserve': {
                        id: 'reserve',
                        heroes: []
                    },
                    'short1': {
                        id: 'short1',
                        heroes: [19]
                    },
                    'short2': {
                        id: 'short2',
                        heroes: [20]
                    },
                    'long1': {
                        id: 'long1',
                        heroes: [21]
                    },
                    'long2': {
                        id: 'long2',
                        heroes: [22]
                    },
                    'long3': {
                        id: 'long3',
                        heroes: [23]
                    },
                    'remove': {
                        id: 'remove',
                        heroes: []
                    }
                },
            })
    }

    getSmallRow() {
        let {pool, columns} = this.state;
        return (
            <div>
                <SquadCol pool={pool} col={columns.short1} descrFunc={this.setDescription} short={true}/>
                <SquadCol pool={pool} col={columns.short2} descrFunc={this.setDescription} short={true}/>
            </div>
        )
    }

    getLongRow() {
        let {pool, columns} = this.state;
        return (
            <div>
                <SquadCol pool={pool} col={columns.long1} descrFunc={this.setDescription}/>
                <SquadCol pool={pool} col={columns.long2} descrFunc={this.setDescription}/>
                <SquadCol pool={pool} col={columns.long3} descrFunc={this.setDescription}/>
            </div>
        )
    }
}

export default connect()(ManageComp);