import React, {Fragment, useState} from 'react';
import {Button, Popover, PopoverHeader, PopoverBody} from "reactstrap";
import {ATTACK, BLOCK, CONCEDE, WAIT} from "@/constants/ingameConstants";
import {FormattedMessage} from "react-intl";
import ProfileImage from "../common/ProfileImage";

const ControlPanel = (props) => {

    let {simpleAction, foe, yourTurn, won, ava} = props;

    const [popoverOpen, setPopoverOpen] = useState(false);
    const toggle = (show) => setPopoverOpen(show);

    return (
        <div style={{width: 110, padding: 5, display: 'flex', flexDirection: 'column'}}>
            {foe ? ava ? <div><ProfileImage properties={ava} titleMsg={'app.avatar.send.message'} invoke={() => {
                    console.log("message sent")
                }}/></div> : null
                :
                <Fragment>
                    <Button color={"danger"} style={{marginBottom: 10}} disabled={!yourTurn || won}
                            onClick={() => simpleAction(ATTACK)}><FormattedMessage id={"app.battle.btn.attack"}/></Button>
                    <Button color={"info"} style={{marginBottom: 10}} disabled={!yourTurn || won}
                            onClick={() => simpleAction(WAIT)}><FormattedMessage id={"app.battle.btn.wait"}/></Button>
                    <Button color={"primary"} style={{marginBottom: 10}} disabled={!yourTurn || won}
                            onClick={() => simpleAction(BLOCK)}><FormattedMessage id={"app.battle.btn.block"}/></Button>
                    <Button id={"concede_button"} color={"secondary"} style={{marginBottom: 10}} disabled={won}
                            onClick={() => {
                                toggle(false);
                                simpleAction(CONCEDE)
                            }}
                            onMouseOver={() => toggle(true)}
                            onMouseLeave={() => toggle(false)}
                    ><FormattedMessage id={"app.battle.btn.concede"}/></Button>
                    <Popover placement="bottom" isOpen={popoverOpen} target="concede_button" toggle={toggle}>
                        <PopoverHeader><FormattedMessage id={"app.battle.concede.header"}/></PopoverHeader>
                        <PopoverBody><FormattedMessage id={"app.battle.concede.body"}/></PopoverBody>
                    </Popover>
                </Fragment>
            }
        </div>
    )
};

export default ControlPanel