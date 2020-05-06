import React, {Fragment, useState} from 'react';
import {
    Button,
    Popover,
    PopoverHeader,
    PopoverBody,
    Dropdown,
    DropdownItem,
    DropdownToggle,
    DropdownMenu
} from "reactstrap";
import {ATTACK, BLOCK, CONCEDE, WAIT} from "@/constants/ingameConstants";
import {FormattedMessage} from "react-intl";
import ProfileImage from "../common/ProfileImage";
import {sendMessage} from "../../service/ActionService";

const ControlPanel = (props) => {

    let {simpleAction, foe, yourTurn, won, ava} = props;

    const [popoverOpen, setPopoverOpen] = useState(false);
    const toggle = (show) => setPopoverOpen(show);

    const [dropdownOpen, setDropdownOpen] = useState(false);
    const toggleDD = () => setDropdownOpen(prevState => !prevState);

    return (
        <div style={{width: 110, padding: 5, display: 'flex', flexDirection: 'column'}}>
            {foe ? ava ?
                <div>
                    <Dropdown isOpen={dropdownOpen} toggle={toggleDD} direction={"left"}>
                        <DropdownToggle
                            tag="span"
                            data-toggle="dropdown"
                            aria-expanded={dropdownOpen}>
                            <ProfileImage properties={ava} titleMsg={'app.avatar.send.message'} invoke={() => {
                            }}/>
                        </DropdownToggle>
                        <DropdownMenu style={{width: 190, marginRight: 10, paddingTop: 0, paddingBottom: 0}}>
                            <DropdownItem onClick={() => sendMessage(1)}>
                                <FormattedMessage id={"app.battle.message.default.1"}/></DropdownItem>
                            <DropdownItem onClick={() => sendMessage(2)}><FormattedMessage
                                id={"app.battle.message.default.2"}/></DropdownItem>
                            <DropdownItem onClick={() => sendMessage(3)}><FormattedMessage
                                id={"app.battle.message.default.3"}/></DropdownItem>
                            <DropdownItem onClick={() => sendMessage(4)}><FormattedMessage
                                id={"app.battle.message.default.4"}/></DropdownItem>
                            <DropdownItem onClick={() => sendMessage(5)}><FormattedMessage
                                id={"app.battle.message.default.5"}/></DropdownItem>
                            <DropdownItem onClick={() => sendMessage(6)}><FormattedMessage
                                id={"app.battle.message.default.6"}/></DropdownItem>
                            <DropdownItem onClick={() => sendMessage(7)}><FormattedMessage
                                id={"app.battle.message.default.7"}/></DropdownItem>
                            <DropdownItem onClick={() => sendMessage(8)}><FormattedMessage
                                id={"app.battle.message.default.8"}/></DropdownItem>
                            <DropdownItem onClick={() => sendMessage(9)}><FormattedMessage
                                id={"app.battle.message.default.9"}/></DropdownItem>
                        </DropdownMenu>
                    </Dropdown>
                </div> : null
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