import React, {Component} from 'react';
import {connect} from 'react-redux';
import {
    Container,
    Jumbotron,
    Button,
    ButtonGroup,
    Row,
    Col
} from 'reactstrap'
import {setNavPosition} from "@/constants/actions";
import {Manage} from "@/constants/paths";
import SquadCol from "@/component/manager/SquadCol";
import * as SquadService from '@/service/SquadService'
import {getPlayerName} from '@/service/PlayerService'
import Pool from "@/component/manager/Pool";
import {FormattedMessage} from 'react-intl';
import {DragDropContext, Droppable} from "react-beautiful-dnd";

class ManageComp extends Component {

    constructor(props) {
        super(props);
        this.state = {
            // ...initialData,
            playerName: getPlayerName(),
            descr: "",
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
                }
            },
            sqType: 1,
            removeWindow: false
        };
        this.props.dispatch(setNavPosition(Manage));
    }

    componentDidMount() {
        //TODO: TODO_SECURITY: requestParam 'name' should be removed in release
        SquadService.getPool(this.state.playerName).then(resp => {
            let inReserve = [];
            for (let i of resp) {
                inReserve.push(i[0]);
            }
            this.setState({
                pool: resp, columns: {
                    ...this.state.columns,
                    "reserve": {
                        id: 'reserve',
                        heroes: inReserve
                    }
                }
            });
        });
    }

    //TODO: does not work now
    addNewHero = (name, type) => {
        SquadService.addNewHero(this.state.playerName, name, type).then(
            resp => {
                if (resp !== null) {
                    let updCol = this.state.columns["reserve"];
                    updCol.heroes.push(resp.id);
                    this.setState({
                        pool: {
                            ...this.state.pool, [resp.id]: resp
                        },
                        columns: {
                            ...this.state.columns, ["reserve"]: updCol
                        }
                    });
                }
            }
        )
    };

    setDescription = (text) => {
        let type = JSON.stringify(text.type).split(",").slice(0, 8).join("\n")
            .replace("{", "").replace("}", "").split("\"").join(" ");
        let string = "Name: " + text.name.concat("\n").concat("Level: ").concat(text.level).concat("\n").concat(type).concat("\n");
        this.setState({descr: string});
    };


    onDragStart = () => {this.setState({removeWindow: true})
    };

    onDragUpdate = update => {
        console.log("drag update")
    };

    onDragEnd = result => {

        const {destination, source, draggableId} = result;
        if (!destination) return;
        if (destination.droppableId === source.droppableId && destination.index === source.index) return;

        const start = this.state.columns[source.droppableId];
        const finish = this.state.columns[destination.droppableId];

        if (start === finish) {
            const newIds = Array.from(start.heroes);
            newIds.splice(source.index, 1);
            newIds.splice(destination.index, 0, draggableId);
            const newCol = {
                ...start,
                heroes: newIds
            };
            const newState = {
                ...this.state,
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
                let spliced = finishHeroes.splice(destination.index, 1, draggableId);
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
        let {sqType} = this.state;
        let smallRow = this.getSmallRow();
        let longRow = this.getLongRow();
        return (
            <Container>
                <Jumbotron style={{paddingTop: 30}}>
                    <DragDropContext onDragEnd={this.onDragEnd} onDragStart={this.onDragStart}>
                        <Row>
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
                                <Col xs={"auto"} style={{marginTop: 147}}>
                                    <textarea id={"mySquadStats"} value={this.state.descr} readOnly={true}
                                              style={{
                                                  width: "200px",
                                                  height: "274px",
                                                  resize: "none",
                                                  borderRadius: 7
                                              }}/>
                                </Col>
                            </Col>
                        </Row>
                    </DragDropContext>

                </Jumbotron>
            </Container>
        )
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