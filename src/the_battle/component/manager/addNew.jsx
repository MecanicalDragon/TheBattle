import React, {useState, useRef, Fragment} from 'react';
import {
    Dropdown,
    DropdownToggle,
    DropdownMenu,
    DropdownItem,
    Input,
    Button,
    Popover,
    PopoverHeader,
    PopoverBody
} from 'reactstrap';
import {FormattedMessage} from "react-intl";

const AddNew = (props) => {
    const {addNewHero} = props;
    const [ddOpen, setDdOpen] = useState(false);
    const [popOpen, setPopOpen] = useState(false);
    const [name, setName] = useState("");
    const [type, setType] = useState("");
    const inputRef = useRef();

    const toggleDd = () => {
        setDdOpen(prevState => !prevState);
        if (popOpen === true)
            setPopOpen(false)
    };
    const togglePop = () => setPopOpen(!popOpen);

    const setNewUnitType = (type) => {
        setType(type);
        togglePop();
        // inputRef.current.focus() //  But it will work only if you extract Input from popover
    };

    const changeName = (event) => {
        setName(event.target.value)
    };

    return (
        <Dropdown isOpen={ddOpen} toggle={toggleDd}>
            <DropdownToggle id={"AddUnitTypeSelector1"} color={"success"} className={"unitLogo"}
                            style={{lineHeight: 1}}>
                <FormattedMessage id={"app.manage.squad.add"}/>
            </DropdownToggle>
            <DropdownMenu>
                <DropdownItem onClick={() => setNewUnitType("fighter")}>Fighter</DropdownItem>
                <DropdownItem onClick={() => setNewUnitType("ranger")}>Ranger</DropdownItem>
                <DropdownItem onClick={() => setNewUnitType("sage")}>Sage</DropdownItem>
            </DropdownMenu>
            <Popover placement="bottom" isOpen={popOpen} target="AddUnitTypeSelector1" toggle={togglePop}>
                <PopoverHeader><FormattedMessage id={"app.manage.squad.inout.name.for.new"}/>{type}</PopoverHeader>
                <PopoverBody style={{display: "flex"}}>
                    <Input placeholder={"2-16 chars"} onChange={(e) => changeName(e)} value={name} innerRef={inputRef}
                           style={{width: 180}} minLength={2} maxLength={16}/>
                    <Button color={"success"} style={{width: 60, padding: 0, marginLeft: 10}}
                            onClick={() => {
                                addNewHero(name, type);
                                setPopOpen(false);
                                setName("")
                            }}>Add</Button>
                </PopoverBody>
            </Popover>
        </Dropdown>
    );
};

export default AddNew;