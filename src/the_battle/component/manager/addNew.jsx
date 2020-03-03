import React, {useState, useRef, Fragment} from 'react';
import {
    Dropdown,
    DropdownToggle,
    DropdownMenu,
    DropdownItem,
    Input,
    Button,
    Form
} from 'reactstrap';
import {FormattedMessage} from "react-intl";
import styled from "styled-components";

const NameArea = styled.div`
    position: absolute;
    left: 200px;
    bottom: 0;
    width: 300px;
    padding: 7px;
    background-color: white;
    text-align: center;
    border: 4px double green;
    border-radius: 10px;
`;

const AddNew = (props) => {
    const {addNewHero} = props;
    const [ddOpen, setDdOpen] = useState(false);
    const [popOpen, setPopOpen] = useState(false);
    const [name, setName] = useState("");
    const [type, setType] = useState("");
    const inputRef = useRef();

    const toggleDd = () => {
        if (popOpen === true)
            setPopOpen(false);
        else setDdOpen(prevState => !prevState);
    };
    const togglePop = () => setPopOpen(!popOpen);

    const setNewUnitType = (type) => {
        setType(type);
        togglePop();
        inputRef.current.focus() //  But it will work only if you extract Input from popover
    };

    const changeName = (event) => {
        setName(event.target.value)
    };

    const submit = (event) => {
        event.preventDefault();
        if (popOpen)
            addNewHero(name, type);
        setPopOpen(false);
        setName("")
    };

    return (
        <Fragment>
            <Dropdown isOpen={ddOpen} toggle={toggleDd} direction={"right"}>
                <DropdownToggle id={"AddUnitTypeSelector1"} color={popOpen ? "danger" : "success"}
                                className={"unitLogo"}
                                style={{lineHeight: 1, borderRadius: 10}}>
                    {popOpen ? <FormattedMessage id={"app.manage.squad.cancel"}/>
                        : <FormattedMessage id={"app.manage.squad.add"}/>}
                </DropdownToggle>
                <DropdownMenu>
                    <DropdownItem onClick={() => setNewUnitType("fighter")}>Fighter</DropdownItem>
                    <DropdownItem onClick={() => setNewUnitType("ranger")}>Ranger</DropdownItem>
                    <DropdownItem onClick={() => setNewUnitType("sage")}>Sage</DropdownItem>
                </DropdownMenu>
            </Dropdown>
            <div style={{position: "relative", zIndex: popOpen ? 1 : -1}}>
                <NameArea>
                    <h6><FormattedMessage id={"app.manage.squad.inout.name.for.new"}/>{type}</h6>
                    <Form onSubmit={(e) => submit(e)}>
                        <div style={{display: "flex"}}>
                            <Input placeholder={"2-16 chars"} onChange={(e) => changeName(e)} value={name}
                                   innerRef={inputRef}
                                   style={{width: 220}} minLength={2} maxLength={16}/>
                            <Button color={"success"} style={{width: 60, padding: 0, marginLeft: 10}}
                                    type={"submit"}>Add</Button>
                        </div>
                    </Form>
                </NameArea>
            </div>
        </Fragment>
    );
};

export default AddNew;