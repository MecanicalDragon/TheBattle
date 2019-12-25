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
import Pool from "@/component/Pool";
import {FormattedMessage} from 'react-intl';

class ManageComp extends Component {

    constructor(props) {
        super(props);
        this.state = {
            playerName: getPlayerName(),
            descr: "",
            squad: [],
            sqType: 1
        };
        this.props.dispatch(setNavPosition(Manage));
    }

    componentDidMount() {
        SquadService.getPool().then(resp => this.setState({squad: resp}));
    }

    setDescription = (text) => {
        let string = JSON.stringify(text).split(",").join("\n").replace("{", "")
            .replace("}", "").split("\"").join(" ").replace("name", "class");
        this.setState({descr: string});
    };

    render() {
        return (
            <Container>
                <Jumbotron style={{paddingTop: 30}}>
                    <Pool descrFunc={this.setDescription}/>
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
                                      style={{width: "150px", height: "200px", resize: "none"}}/>
                        </Col>
                        <MySquad type={this.state.sqType}/>
                    </Row>
                </Jumbotron>
            </Container>
        )
    }
}

export default connect()(ManageComp);