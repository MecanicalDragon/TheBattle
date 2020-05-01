import React, {Component, Fragment} from 'react';
import {connect} from 'react-redux';
import ProfileImage from "./common/ProfileImage";
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
            profileImage: {
                background: "#ffffff",
                borders: "#000000",
                color: "#000000",
                name: "dragon1"
            },
            status: STATUS[0],
            logoutWarning: false,
            newsUrl: APP_ROOT + "res/ad/no-add_1.jpg"
        };
        this.handleChange = this.handleChange.bind(this);
        this.handlePwChange = this.handlePwChange.bind(this);
        this.toggleLogoutWarning = this.toggleLogoutWarning.bind(this)
    }

    componentDidMount() {
        player.isPlayerLoggedInWithData().then(resp => {
            if (resp) {
                let player = resp.player;
                this.setState({
                    playerName: player.name,
                    gamesTotal: player.games,
                    wins: player.wins,
                    status: player.status,
                    authenticated: true,
                    profileImage: player.profileImage,
                    newsUrl: APP_ROOT + resp.newsUrl
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
                    <h1 id={"theBattle"} style={{color: "var(--app-primary-color)"}}><FormattedMessage
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
                                <h6 style={{color: "blue"}}>
                                    <FormattedMessage id={"app.index.games.count"}/>{gamesTotal}
                                </h6>
                            </Col>
                            <Col>
                                <h6 style={{color: "darkgreen"}}>
                                    <FormattedMessage id={"app.index.wins.count"}/>{wins}
                                </h6>
                            </Col>
                        </Row>
                        <br/>
                        <ProfileImage properties={this.state.profileImage} invoke={this.manageProfile}
                                      titleMsg={"app.avatar.change"}/>
                        <br/>
                        {
                            gamesTotal === 0 ? null :
                                <h5 style={{color: "green"}}><FormattedMessage id={"app.index.win.rate"}/>
                                    {(wins * 100 / gamesTotal).toPrecision(4)}%</h5>
                        }
                        <br/>
                        <Button color={"info"} onClick={() => this.props.history.push(routes.manage())}>
                            <FormattedMessage id={"app.index.prepare.to.battle"}/></Button>
                        <br/>
                        <br/>
                        <Button id={"logoutButton"} onClick={() => this.toggleLogoutWarning()}>
                            <FormattedMessage id={"app.index.logout.btn.logout"}/></Button>
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
                            borderColor: "var(--app-primary-color)"
                        }}/>
                    </Col>
                </Row>
            </Fragment>
        )
    }

    manageProfile = () => {
        localStorage.setItem("profileImageData", JSON.stringify(this.state.profileImage))
        this.props.history.push(routes.profile())
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
                playerName: resp.player.name,
                wins: resp.player.wins,
                gamesTotal: resp.player.games,
                enterName: "",
                enterPw: "",
                authenticated: true,
                profileImage: resp.player.profileImage,
                newsUrl: APP_ROOT + resp.newsUrl
            })
        }
    }

    logout() {
        this.toggleLogoutWarning();
        player.logout().then(logout => {
            if (logout) {
                this.setState({playerName: "", authenticated: false})
            }
        });
    }
}

export default connect()(Index);