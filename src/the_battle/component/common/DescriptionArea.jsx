import React, {useState} from 'react';

const DescriptionArea = (props) => {

    let {description} = props;

    const setDescription = (text) => {
        if (text) {
            let type = JSON.stringify(text.type).split(",").slice(0, 8).join("\n")
                .replace("{", "").replace("}", "").split("\"").join(" ");
            return "Name: " + text.name.concat("\n").concat("Level: ").concat(text.level).concat("\n").concat(type).concat("\n");
        } else return ""
    };

    return (
        <textarea value={setDescription(description)} readOnly={true}
                  style={{
                      width: "200px",
                      height: "274px",
                      resize: "none",
                      borderRadius: 7,
                      // marginTop: 17
                  }}/>
    )
};

export default DescriptionArea