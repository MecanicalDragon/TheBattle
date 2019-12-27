import React, {Component} from 'react';
import {connect} from 'react-redux';
import Dnd from './dnd/dnd';
import initialData from "@/component/manager/dnd/initData";
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
import MySquad from "@/component/MySquad";
import * as SquadService from '@/service/SquadService'
import {getPlayerName} from '@/service/PlayerService'
import Pool from "@/component/manager/Pool";
import {FormattedMessage} from 'react-intl';
import {DragDropContext} from "react-beautiful-dnd";

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
                }
            },
            sqType: 1
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


    onDragStart = () => {
        console.log("drag start")
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
            startHeroes.splice(source.index, 1);
            const newStart = {
                ...start, heroes: startHeroes
            };
            const finishHeroes = Array.from(finish.heroes);
            finishHeroes.splice(destination.index, 0, draggableId)
            const newFinish = {
                ...finish, heroes: finishHeroes
            };

            const newState = {
                ...this.state,
                columns: {
                    ...this.state.columns,
                    [newStart.id]: newStart,
                    [newFinish.id]: newFinish,
                }
            };
            this.setState(newState)
        }
    };


    render() {
        return (
            <Container>
                <Jumbotron style={{paddingTop: 30}}>
                    <DragDropContext onDragEnd={this.onDragEnd}>

                        <Pool reserved={this.state.columns.reserve.heroes} pool={this.state.pool}
                              descrFunc={this.setDescription} addNewHero={this.addNewHero}/>
                        <h5><FormattedMessage id={"app.manage.squad.type"}/></h5>
                        <Row style={{marginBottom: 15}}>
                            <ButtonGroup>
                                <Button color={this.state.sqType === 2 ? "success" : "warning"}
                                        onClick={() => this.setState({sqType: 2})} active={this.state.sqType === 2}>FORCED
                                    BACK</Button>
                                <Button color={this.state.sqType === 1 ? "success" : "warning"}
                                        onClick={() => this.setState({sqType: 1})} active={this.state.sqType === 1}>FORCED
                                    FRONT</Button>
                            </ButtonGroup>
                        </Row>
                        <Row>
                            <Col xs={"auto"}>
                            <textarea id={"mySquadStats"} value={this.state.descr} readOnly={true}
                                      style={{width: "200px", height: "275px", resize: "none"}}/>
                            </Col>
                            <MySquad type={this.state.sqType}/>
                        </Row>
                        {/*<Row>*/}
                        {/*    <Dnd/>*/}
                        {/*</Row>*/}
                    </DragDropContext>

                </Jumbotron>
            </Container>
        )
    }
}

export default connect()(ManageComp);