import React, {Component} from 'react';

export function Unit(props) {
    const {characteristics, descrFunc} = props;
    return (
        <div className={"unitLogo" + getBorderColor(characteristics.name)}
             onMouseOver={() => descrFunc(characteristics)}>
            {characteristics.name}
        </div>
    )
}

function getBorderColor(type) {
    switch (type) {
        case "Warrior":
            return " warriorLogo";
        case "Mage":
            return " mageLogo";
        case "Archer":
            return " archerLogo";
        default:
            return null
    }
}