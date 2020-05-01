import React, {useState, useEffect} from 'react';
import {injectIntl} from 'react-intl';
import TheTint from "../profile/tint";

const APP_ROOT = DEPLOYED_URL;

const ProfileImage = (props) => {

    const {properties, titleMsg, intl, invoke} = props

    return (
        <div
            title={intl.formatMessage({id: titleMsg})}
            style={{
                width: 108,
                height: 108,
                backgroundColor: properties.background,
                borderWidth: 4,
                borderStyle: "solid",
                borderRadius: 15,
                borderColor: properties.borders,
                margin: "auto",
                cursor: "pointer"
            }}
            onClick={() => invoke()}>
            <TheTint src={APP_ROOT + "res/avatar/" + properties.name + ".png"}
                      color={properties.color} height={"100"} width={"100"}/>
        </div>
    )
}

export default injectIntl(ProfileImage)