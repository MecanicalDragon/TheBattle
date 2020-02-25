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
import {STATUS} from "@/constants/ingameConstants";

class Index extends Component {

    //TODO: update advertise
    constructor(props) {
        super(props);
        this.props.dispatch(setNavPosition(Home));
        this.state = {
            enterName: "",
            enterPw: "",
            playerName: "",
            authenticated: undefined,
            wins: 0,
            gamesTotal: 0,
            status: STATUS[0],
            newsUrl: "http://localhost:9191/assets/no-add_1.jpg"
        };
        this.handleChange = this.handleChange.bind(this);
        this.handlePwChange = this.handlePwChange.bind(this)
    }

    componentDidMount() {
        player.isPlayerLoggedInWithData().then(player => {
            if (player) {
                this.setState({
                    playerName: player.name,
                    gamesTotal: player.games,
                    wins: player.wins,
                    status: player.status,
                    authenticated: true
                });
            } else {
                this.setState({authenticated: false});
            }
        })
    }

    render() {
        return (
            <Container>
                <Jumbotron style={{textAlign: "center"}}>
                    <h1 id={"theBattle"} style={{color: "var(--magenta-color)"}}><FormattedMessage
                        id={'app.index.header'}/></h1>
                    <br/>
                    {this.state.authenticated === true ? this.getPlayerForm() :
                        this.state.authenticated === false ? this.getLoginForm() : null}
                </Jumbotron>
            </Container>
        )
    }

    getPlayerForm() {
        let {playerName, gamesTotal, wins} = this.state;
        return (
            <Fragment>
                <Row>
                    <Col>
                        <h2 style={{color: "black"}}>{playerName}</h2>
                        <br/>
                        <Row>
                            <Col>
                                <h6 style={{color: "blue"}}>Games: {gamesTotal}</h6>
                            </Col>
                            <Col>
                                <h6 style={{color: "darkgreen"}}>Wins: {wins}</h6>
                            </Col>
                        </Row>
                        <br/>
                        {
                            gamesTotal === 0 ? null :
                                <h5 style={{color: "green"}}>Win Rate: {(wins * 100 / gamesTotal).toPrecision(4)}%</h5>
                        }
                        <br/>
                        <Button color={"info"} onClick={() => this.props.history.push(routes.manage())}>Manage
                            squad</Button>
                        <br/>
                        <br/>
                        <Button onClick={() => this.logout()}>Logout</Button>
                    </Col>
                    <Col>
                        <img src={this.state.newsUrl} alt="HOT_NEWS" style={{
                            borderStyle: "solid",
                            borderWidth: 5,
                            borderRadius: 15,
                            borderColor: "var(--magenta-color)"
                        }}/>
                    </Col>
                </Row>
            </Fragment>
        )
    }

    getLoginForm() {
        return (
            <Form onSubmit={(e) => this.login(e)}>
                <Row>
                    <Col xs={{size: 2, offset: 2}} style={{textAlign: "right"}}>
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
                        <Input type={"password"} value={this.state.enterPw} onChange={this.handlePwChange}
                               placeholder={"Input password"}/>
                    </Col>
                    <Col xs={"auto"}>
                        <Button color={"info"} onClick={() => this.create()}
                                style={{height: 35, width: 75}}>Create</Button>
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
            this.successfulLogin(resp)
        });
    }

    create() {
        let name = this.state.enterName;
        let pw = this.state.enterPw;
        player.create(name, pw).then(resp => {
            this.successfulLogin(resp)
        });
    }

    successfulLogin(resp) {
        if (resp !== null) {
            this.setState({
                playerName: resp.name,
                wins: resp.wins,
                gamesTotal: resp.games,
                enterName: "",
                enterPw: "",
                authenticated: true
            })
        }
    }

    //TODO: give warning about consequences
    logout() {
        player.logout().then(logout => {
            if (logout) {
                this.setState({playerName: "", authenticated: false})
            }
        });
    }
}

export default connect()(Index);