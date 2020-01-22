import React, {useEffect, useState} from 'react';

const Console = (props) => {

    const {battleLogs} = props;
    const [log, addLog] = useState("");

    useEffect(() => {
        let l = log;
        if (l !== "") l += "\n";
        addLog(l + battleLogs)
    }, [battleLogs]);

    return (
        <textarea value={log} readOnly={true}
                  style={{
                      width: 800,
                      height: 200,
                      resize: "none",
                      borderRadius: 7,
                      marginLeft: 3,
                      marginRight: 3
                  }}/>
    )
};

export default Console