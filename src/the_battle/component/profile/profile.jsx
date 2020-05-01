import React, {useState, useEffect} from 'react';
import {connect} from 'react-redux';
import Img from 'react-image'
import * as routes from '@/router/routes'
import {setNavPosition} from "../../constants/actions";
import {saveProfileImage, getAvatarsPage} from "../../service/PlayerService";
import {Profile} from "../../constants/paths";
import {
    Container,
    Jumbotron,
    Row,
    Col, Button,
} from 'reactstrap';
import iro from '@jaames/iro';
import {FormattedMessage} from "react-intl";
import TheTint from "./tint";

const APP_ROOT = DEPLOYED_URL;

function ProfilePage(props) {

    const [background, setBackground] = useState("#e9ecef")
    const [outline, setOutline] = useState("#e9ecef")
    const [borders, setBorders] = useState("#e9ecef")
    const [image, setImage] = useState("transparent")
    const [page, setPage] = useState(0)
    const [totalPages, setTotalPages] = useState(1)
    const [content, setContent] = useState([])

    const getColorPicker = (tag, color) => {
        return new iro.ColorPicker('#' + tag, {
            color: color,
            width: 170,
            sliderSize: 15,
            handleRadius: 5,
            borderWidth: 1,
            borderColor: color,
            padding: 3,
            margin: 5,
            layout: [
                {
                    component: iro.ui.Wheel,
                    options: {
                        borderColor: color
                    }
                },
                {
                    component: iro.ui.Slider,
                    options: {
                        sliderType: "saturation",
                        borderColor: color
                    }
                },
                {
                    component: iro.ui.Slider,
                    options: {
                        sliderType: "value",
                        borderColor: color
                    }
                }
            ]
        });
    }

    useEffect(() => {
        props.dispatch(setNavPosition(Profile))
        const avatar = JSON.parse(localStorage.getItem("profileImageData"))
        setImage(avatar.name)
        setBorders(avatar.borders)
        setBackground(avatar.background)
        setOutline(avatar.color)
        let bordersColorPicker = getColorPicker("bordersColorPicker", avatar.borders)
        bordersColorPicker.on('color:change', function (color) {
            setBorders(color.hexString)
        });
        let backgroundColorPicker = getColorPicker("backgroundColorPicker", avatar.background)
        backgroundColorPicker.on('color:change', function (color) {
            setBackground(color.hexString)
        });
        let outlineColorPicker = getColorPicker("outlineColorPicker", avatar.color)
        outlineColorPicker.on('color:change', function (color) {
            setOutline(color.hexString)
        });
        getAvatarsPage(page).then(resp => {
            setContent(resp.content);
            setPage(resp.number)
            setTotalPages(resp.totalPages)
        })
    }, []);

    const prev = () => {
        if (page > 0) {
            getAvatarsPage(page - 1).then(resp => {
                setContent(resp.content);
                setPage(resp.number)
                setTotalPages(resp.totalPages)
            })
        }
    }

    const next = () => {
        let number = page + 1
        if (number < totalPages) {
            getAvatarsPage(number).then(resp => {
                setContent(resp.content);
                setPage(resp.number)
                setTotalPages(resp.totalPages)
            })
        }
    }

    const saveAndReturn = () => {
        saveProfileImage({name: image, color: outline, background: background, borders: borders}).then(b => {
            if (b === true) {
                localStorage.removeItem("profileImageData")
                props.history.push(routes.index())
            }
        })
    }

    return (
        <Container>
            <Jumbotron style={{textAlign: "center"}}>
                <Row>
                    <Col>
                        <div style={{
                            width: 162,
                            height: 162,
                            backgroundColor: background,
                            borderWidth: 6,
                            borderStyle: "solid",
                            borderRadius: 22,
                            borderColor: borders,
                            margin: "auto"
                        }}>
                            <TheTint src={APP_ROOT + "res/avatar/" + image + ".png"}
                                     color={outline} width={"150"} height={"150"}/>
                        </div>
                        <br/>
                        <Button style={{width: 162, backgroundColor: "var(--app-primary-color)"}}
                                onClick={saveAndReturn}><FormattedMessage id={"app.avatar.save"}/></Button>
                    </Col>
                    <Col>
                        {/*<h6>Borders</h6>*/}
                        <div id={"bordersColorPicker"}/>
                    </Col>
                    <Col>
                        {/*<h6>Background</h6>*/}
                        <div id={"backgroundColorPicker"}/>
                    </Col>
                    <Col>
                        {/*<h6>Outline</h6>*/}
                        <div id={"outlineColorPicker"}/>
                    </Col>
                </Row>
                <br/>
                <br/>
                <Row>
                    <Col style={{textAlign: "right"}}>
                        <Button color={"info"} onClick={() => prev()} disabled={page === 0}>
                            <FormattedMessage id={"app.avatar.prev"}/></Button>
                    </Col>
                    <Col>
                        <Row style={{
                            backgroundColor: "white",
                            borderRadius: 15,
                            borerWidth: 5,
                            borderColor: "darkGrey",
                            borderStyle: "solid",
                            width: 660,
                            height: 266
                        }}>
                            {
                                content.map((image, index) => {
                                    return <Col key={index} style={{padding: 15}}>
                                        <Img style={{maxHeight: 100, maxWidth: 100, cursor: "pointer"}}
                                             onClick={() => setImage(image)}
                                             src={APP_ROOT + "res/avatar/" + image + ".png"}/>
                                    </Col>
                                })
                            }
                        </Row>
                    </Col>
                    <Col style={{textAlign: "left"}}>
                        <Button color={"info"} onClick={() => next()} disabled={page + 1 === totalPages}>
                            <FormattedMessage id={"app.avatar.next"}/></Button>
                    </Col>
                </Row>
            </Jumbotron>
        </Container>
    )
}

export default connect()(ProfilePage);