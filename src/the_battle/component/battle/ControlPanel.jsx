import React, {Fragment} from 'react';
import {Button} from "reactstrap";
import {ATTACK, BLOCK, WAIT} from "@/constants/ingameConstants";

const ControlPanel = (props) => {

    let {simpleAction, foe, yourTurn, won} = props;

    return (
        <div style={{width: 110, display: 'flex', flexDirection: 'column'}}>
            {foe ? null :
                <Fragment>
                    <Button color={"danger"} style={{margin: 5}} disabled={!yourTurn && !won}
                            onClick={() => simpleAction(ATTACK)}>Attack!</Button>
                    <Button color={"info"} style={{margin: 5}} disabled={!yourTurn && !won}
                            onClick={() => simpleAction(WAIT)}>Wait</Button>
                    <Button color={"primary"} style={{margin: 5}} disabled={!yourTurn && !won}
                            onClick={() => simpleAction(BLOCK)}>Block</Button>
                </Fragment>
            }
        </div>
    )
};

export default ControlPanel