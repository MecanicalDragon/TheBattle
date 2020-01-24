import React from 'react';
import styled from "styled-components";
import {FormattedMessage} from "react-intl";
import {Badge} from "reactstrap";

const HeaderLine = styled.div`
    display: flex;
    flex-direction: ${props => (props.foe ? 'row-reverse' : 'row')}
`;

const TurnLabel = styled.div`
    display: flex;
    flex-direction: ${props => (props.foe ? 'row-reverse' : 'row')}
`;

const Header = (props) => {

    const {foe, playerName, yourTurn, won} = props;

    return (
        <HeaderLine foe={foe}>
            <div style={{width: 110}}/>
            <div style={{width: 200, textAlign: "center"}}>
                <h2>{playerName}</h2>
            </div>
            {won ? null :
                <div style={{width: 200, textAlign: "center"}}>
                    <h2>
                        <Badge color={foe ? "danger" : "success"}>
                            {foe ? yourTurn ? null : <FormattedMessage id={'app.battle.foes.turn.header'}/>
                                : yourTurn ? <FormattedMessage id={'app.battle.your.turn.header'}/> : null}
                        </Badge>
                    </h2>
                </div>
            }
        </HeaderLine>
    )
};

export default Header