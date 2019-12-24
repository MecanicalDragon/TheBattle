import React, {Component} from 'react';
import {connect} from 'react-redux';
import {
    Container,
    Jumbotron,
    Button,
    Row,
    Col,
    Input
} from 'reactstrap'
import * as routes from '@/router/routes'
import * as player from '@/service/PlayerService'
import {setNavPosition} from "@/constants/actions";
import {Home} from "@/constants/paths";
import {FormattedMessage} from 'react-intl';

class Index extends Component {

    constructor(props) {
        super(props);
        this.props.dispatch(setNavPosition(Home));
        this.state = {
            enterName: "",
            playerName: "",
            authenticated: false
        };
        this.handleChange = this.handleChange.bind(this)
    }

    componentDidMount() {
        if (player.isPlayerLoggedIn()) {
            let pn = player.getPlayerName();
            this.setState({playerName: pn, authenticated: true});
        }
    }

    render() {
        return (
            <Container>
                <Jumbotron>
                    <h1 id={"theBattle"}><FormattedMessage id={'app.index.header'}/></h1>
                    {this.state.authenticated ? this.getPlayerForm() : this.getLoginForm()}
                    <Button id={"start"} color={"primary"} onClick={() => {
                        this.props.history.push(routes.battle())
                    }}><FormattedMessage id={'app.index.start'}/></Button>
                </Jumbotron>
            </Container>
        )
    }

    getPlayerForm() {
        let {playerName} = this.state;
        return (
            <Row>
                <h2 style={{marginLeft: 20}}>{playerName}</h2>
                <Button color={"info"} onClick={() => this.props.history.push(routes.manage())}>Manage squad</Button>
                <Button onClick={() => this.logout()}>Logout</Button>
            </Row>
        )
    }

    getLoginForm() {
        return (
            <Row>
                <Col xs={"auto"}>
                    <FormattedMessage id={"app.input.name"}/>
                </Col>
                <Col xs={"auto"}>
                    <Input type={"text"} value={this.state.enterName} onChange={this.handleChange}
                           placeholder={"Input your name"}/>
                </Col>
                <Col xs={"auto"}>
                    <Button color={"success"} onClick={() => this.login()}>Login</Button>
                </Col>
                <Col xs={"auto"}>
                    <Button color={"info"} onClick={() => this.create()}>Create</Button>
                </Col>
            </Row>
        )
    }

    handleChange(event) {
        this.setState({enterName: event.target.value})
    }

    login() {
        let name = this.state.enterName;
        player.login(name).then(resp => {
            if (resp !== null) {
                console.log("Logged in:");
                console.log(resp);
                this.setState({playerName: resp.name, authenticated: true})
            }
        });
    }

    logout() {
        player.logout().then(logout => {
            if (logout) {
                this.setState({playerName: "", authenticated: false})
            }
        });
    }

    create() {
        let name = this.state.enterName;
        player.create(name).then(resp => {
            if (resp !== null) {
                this.setState({playerName: resp, authenticated: true})
            }
        });
    }
}

export default connect()(Index);