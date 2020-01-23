import React from 'react';

const DescriptionArea = (props) => {

    let {description, height} = props;

    const setDescription = (unit) => {
        if (unit) {
            return `  Name: ${unit.name}
  level: ${unit.level}
  class: ${unit.type.classType}
  range: ${unit.type.distance}
  health: ${unit.hp}
  initiative: ${unit.type.initiative}
  attack: ${unit.type.attack}
  defence: ${unit.type.defence}
  accuracy: ${unit.type.accuracy}
  evasion: ${unit.type.evasion}`;
        } else return ""
    };

    return (
        <textarea value={setDescription(description)} readOnly={true}
                  style={{
                      width: "200px",
                      height: height || 300,
                      resize: "none",
                      borderRadius: 7,
                      marginLeft: 3,
                      marginRight: 3
                  }}/>
    )
};

export default DescriptionArea