import React, {Component, Fragment} from 'react';
import {connect} from 'react-redux';
import {
    Container,
    Jumbotron,
    Button,
    Row,
    Col,
    Input, Form, Popover, PopoverHeader, PopoverBody
} from 'reactstrap'
import * as routes from '@/router/routes'
import * as player from '@/service/PlayerService'
import {setNavPosition} from "@/constants/actions";
import {Home} from "@/constants/paths";
import {FormattedMessage} from 'react-intl';
import {STATUS} from "@/constants/ingameConstants";

const APP_ROOT = DEPLOYED_URL;

class Index extends Component {

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
            logoutWarning: false,
            newsUrl: APP_ROOT + "ad/no-add_1.jpg"
        };
        this.handleChange = this.handleChange.bind(this);
        this.handlePwChange = this.handlePwChange.bind(this);
        this.toggleLogoutWarning = this.toggleLogoutWarning.bind(this)
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
                        <Button id={"logoutButton"} onClick={() => this.toggleLogoutWarning()}>Logout</Button>
                        <Popover placement="bottom" isOpen={this.state.logoutWarning} target="logoutButton"
                                 toggle={this.toggleLogoutWarning}>
                            <PopoverHeader><FormattedMessage id={'app.index.logout.warning'}/></PopoverHeader>
                            <PopoverBody>
                                <FormattedMessage id={'app.index.logout.span1'}/>
                                <br/>
                                <FormattedMessage id={'app.index.logout.span2'}/>
                                <br/>
                                <Row style={{paddingTop: 7}}>
                                    <Col style={{textAlign: "center"}}>
                                        <Button color={"secondary"}
                                                onClick={() => this.toggleLogoutWarning()}><FormattedMessage
                                            id={'app.index.logout.btn.cancel'}/></Button>
                                    </Col>
                                    <Col style={{textAlign: "center"}}>
                                        <Button color={"danger"} onClick={() => this.logout()}><FormattedMessage
                                            id={'app.index.logout.btn.logout'}/></Button>
                                    </Col>
                                </Row>
                            </PopoverBody>
                        </Popover>
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
                <Row style={{margin: 7}}>
                    <Col xs={{size: 4}}/>
                    <Col xs={{size: 4}}>
                        <Input type={"text"} value={this.state.enterName} onChange={this.handleChange}
                               placeholder={"Input your name"}/>
                    </Col>
                </Row>
                <Row style={{margin: 7}}>
                    <Col xs={{size: 4}}/>
                    <Col xs={{size: 4}}>
                        <Input type={"password"} value={this.state.enterPw} onChange={this.handlePwChange}
                               placeholder={"Input password"}/>
                    </Col>
                </Row>
                <Row style={{margin: 7}}>
                    <Col xs={{size: 4}}/>

                    <Col xs={{size: 2}}>
                        <Button color={"success"} type={"submit"}
                                style={{width: 140}}>Login</Button>
                    </Col>
                    <Col xs={{size: 2}}>
                        <Button color={"info"} onClick={() => this.create()}
                                style={{width: 140}}>Create</Button>
                    </Col>
                    <Col xs={{size: 4}}/>

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

    toggleLogoutWarning() {
        this.setState({logoutWarning: !this.state.logoutWarning})
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

    logout() {
        this.toggleLogoutWarning()
        player.logout().then(logout => {
            if (logout) {
                this.setState({playerName: "", authenticated: false})
            }
        });
    }
}

export default connect()(Index);