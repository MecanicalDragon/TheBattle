import React, {Fragment} from 'react';
import {Button} from "reactstrap";
import {ATTACK, BLOCK, WAIT} from "@/constants/ingameConstants";

const ControlPanel = (props) => {

    let {simpleAction, foe} = props;

    return (
        <div style={{width: 110, display: 'flex', flexDirection: 'column'}}>
            {foe ? null :
                <Fragment>
                    <Button color={"danger"} style={{margin: 5}} onClick={() => simpleAction(ATTACK)}>Attack!</Button>
                    <Button color={"info"} style={{margin: 5}} onClick={() => simpleAction(WAIT)}>Wait</Button>
                    <Button color={"primary"} style={{margin: 5}} onClick={() => simpleAction(BLOCK)}>Block</Button>
                </Fragment>
            }
        </div>
    )
};

export default ControlPanel