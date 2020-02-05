import React, {Component, Fragment} from 'react';
import {connect} from 'react-redux';
import {
    Container,
    Jumbotron,
    Button,
    Row,
    Col,
    Input, Form
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
            enterPw: "",
            playerName: "",
            authenticated: false,
            wins: 0,
            gamesTotal: 0
        };
        this.handleChange = this.handleChange.bind(this);
        this.handlePwChange = this.handlePwChange.bind(this)
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
                <Jumbotron style={{textAlign: "center"}}>
                    <h1 id={"theBattle"}><FormattedMessage id={'app.index.header'}/></h1>
                    {this.state.authenticated ? this.getPlayerForm() : this.getLoginForm()}
                </Jumbotron>
            </Container>
        )
    }

    getPlayerForm() {
        let {playerName} = this.state;
        return (
            <Fragment>
                <h2 style={{marginLeft: 20}}>{playerName}</h2>
                <span>Games: </span><span>{this.state.gamesTotal}</span>
                <br/>
                <span>Wins: </span><span>{this.state.wins}</span>
                <br/>
                <Button color={"info"} onClick={() => this.props.history.push(routes.manage())}>Manage squad</Button>
                <Button onClick={() => this.logout()}>Logout</Button>
            </Fragment>
        )
    }

    getLoginForm() {
        return (
            <Form onSubmit={(e) => this.login(e)}>
                <Row>
                    <Col  xs={{size: 2, offset: 2}} style={{textAlign: "right"}}>
                        <FormattedMessage id={"app.input.name"}/>
                    </Col>
                    <Col xs={{size: 4}}>
                        <Input type={"text"} value={this.state.enterName} onChange={this.handleChange}
                               placeholder={"Input your name"}/>
                    </Col>
                    <Col xs={"auto"}>
                        <Button color={"success"} type={"submit"} style={{height: 35, width: 75}}>Login</Button>
                    </Col>
                </Row>
                <Row>
                    <Col xs={{size: 2, offset: 2}} style={{textAlign: "right"}}>
                        <FormattedMessage id={"app.input.pw"}/>
                    </Col>
                    <Col xs={{size: 4}}>
                        <Input type={"text"} value={this.state.enterPw} onChange={this.handlePwChange}
                               placeholder={"Input password"}/>
                    </Col>
                    <Col xs={"auto"}>
                        <Button color={"info"} onClick={() => this.create()} style={{height: 35, width: 75}}>Create</Button>
                    </Col>
                </Row>
            </Form>
        )
    }

    handleChange(event) {
        this.setState({enterName: event.target.value})
    }

    handlePwChange(event) {
        this.setState({enterPw: event.target.value})
    }

    login(event) {
        event.preventDefault();
        let name = this.state.enterName;
        let pw = this.state.enterPw;
        player.login(name, pw).then(resp => {
            if (resp !== null) {
                this.setState({wins: resp.wins, gamesTotal: resp.games, playerName: resp.name, authenticated: true})
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
        let pw = this.state.enterPw;
        player.create(name, pw).then(resp => {
            if (resp !== null) {
                this.setState({playerName: resp.name, authenticated: true, gamesTotal: resp.games, wins: resp.wins})
            }
        });
    }
}

export default connect()(Index);