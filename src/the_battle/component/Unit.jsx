import React, {Component} from 'react';

export function Unit(props) {
    const {characteristics, descrFunc} = props;
    return (
        <div className={"unitLogo" + getBorderColor(characteristics.type.type)}
             onMouseOver={() => descrFunc(characteristics)}>
            {characteristics.name}<br/>
            {characteristics.type.type}<br/>
            level {characteristics.level}
        </div>
    )
}

function getBorderColor(type) {
    switch (type) {
        case "FIGHTER":
            return " warriorLogo";
        case "SAGE":
            return " mageLogo";
        case "RANGER":
            return " archerLogo";
        default:
            return null
    }
}