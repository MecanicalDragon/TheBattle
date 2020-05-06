import React from 'react';
import {connect} from "react-redux";
import {setLocale} from '@/constants/actions';
import {
    Dropdown,
    DropdownItem,
    DropdownToggle,
    DropdownMenu,
} from 'reactstrap';

import * as routes from '@/router/routes'

class Locale extends React.Component {

    constructor(props) {
        super(props);
        this.toggle = this.toggle.bind(this);
        this.state = {
            open: false,
            locale: props.state.locale
        };
        document.getElementsByTagName("html")[0].setAttribute("lang", props.state.locale);
    }

    toggle() {
        this.setState({
            open: !this.state.open
        });
    }

    setLanguage(locale) {
        document.getElementsByTagName("html")[0].setAttribute("lang", locale);
        this.props.history.push(routes.index());
        this.props.dispatch(setLocale(locale));
    }

    render() {
        const {open, locale} = this.state;
        return (
            <Dropdown nav isOpen={open} toggle={this.toggle}>
                <DropdownToggle nav caret style={{border: "1px solid #b2b2b2", borderRadius: "4px", backgroundColor: "white"}}>
                    {locale.toUpperCase()}
                </DropdownToggle>
                <DropdownMenu style={{minWidth: "unset", width: "100%", fontSize: "inherit"}}>
                    <DropdownItem active={locale === 'en'}
                                  onClick={() => this.setLanguage('en')}>EN</DropdownItem>
                    {/*<DropdownItem active={locale === 'de'}*/}
                    {/*              onClick={() => this.setLanguage('de')}>DE</DropdownItem>*/}
                    <DropdownItem active={locale === 'ru'}
                                  onClick={() => this.setLanguage('ru')}>RU</DropdownItem>
                </DropdownMenu>
            </Dropdown>
        );
    }
}

const mapStateToProps = state => {
    return ({
        state: state.intl
    })
};

export default connect(mapStateToProps)(Locale);