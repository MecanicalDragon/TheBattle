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
import MySquad from "@/component/MySquad";
import * as SquadService from '@/service/SquadService'
import {getPlayerName} from '@/service/PlayerService'
import Pool from "@/component/manager/Pool";
import {FormattedMessage} from 'react-intl';

class ManageComp extends Component {

    constructor(props) {
        super(props);
        this.state = {
            playerName: getPlayerName(),
            descr: "",
            pool: [],
            sqType: 1
        };
        this.props.dispatch(setNavPosition(Manage));
    }

    componentDidMount() {
        //TODO: TODO_SECURITY: requestParam 'name' should be removed in release
        SquadService.getPool(this.state.playerName).then(resp => {
            this.setState({pool: resp})
        });
    }

    addNewHero = (name, type) => {
        SquadService.addNewHero(this.state.playerName, name, type).then(
            resp => {
                if (resp !== null) {
                    this.setState((prevState) => prevState.pool.push(resp));
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

    render() {
        return (
            <Container>
                <Jumbotron style={{paddingTop: 30}}>
                    <Pool pool={this.state.pool} descrFunc={this.setDescription} addNewHero={this.addNewHero}/>
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
                </Jumbotron>
            </Container>
        )
    }
}

export default connect()(ManageComp);