import React, {Fragment, useState} from 'react';
import {Button, Popover, PopoverHeader, PopoverBody} from "reactstrap";
import {ATTACK, BLOCK, CONCEDE, WAIT} from "@/constants/ingameConstants";
import {FormattedMessage} from "react-intl";

const ControlPanel = (props) => {

    let {simpleAction, foe, yourTurn, won} = props;

    const [popoverOpen, setPopoverOpen] = useState(false);
    const toggle = (show) => setPopoverOpen(show);

    return (
        <div style={{width: 110, display: 'flex', flexDirection: 'column'}}>
            {foe ? null :
                <Fragment>
                    <Button color={"danger"} style={{margin: 5}} disabled={!yourTurn || won}
                            onClick={() => simpleAction(ATTACK)}>Attack!</Button>
                    <Button color={"info"} style={{margin: 5}} disabled={!yourTurn || won}
                            onClick={() => simpleAction(WAIT)}>Wait</Button>
                    <Button color={"primary"} style={{margin: 5}} disabled={!yourTurn || won}
                            onClick={() => simpleAction(BLOCK)}>Block</Button>
                    <Button id={"concede_button"} color={"secondary"} style={{margin: 5}} disabled={won}
                            onClick={() => {
                                toggle(false);
                                simpleAction(CONCEDE)
                            }}
                            onMouseOver={() => toggle(true)}
                            onMouseLeave={() => toggle(false)}
                    >Concede</Button>
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