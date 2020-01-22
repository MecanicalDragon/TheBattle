import React, {useEffect, useState, useRef} from 'react';

const Console = (props) => {

    const {battleLogs} = props;
    const [log, addLog] = useState("");
    const ref = useRef();

    useEffect(() => {
        addLog(log.slice(0, -1) + battleLogs + "\n\n");
        if (ref) ref.current.scrollTop = ref.current.scrollHeight;
    }, [battleLogs]);

    return (
        <textarea value={log} readOnly={true} ref={ref}
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