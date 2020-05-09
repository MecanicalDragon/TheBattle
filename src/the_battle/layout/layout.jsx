import React, {Fragment, Suspense} from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import {connect} from 'react-redux';
import {FormattedMessage} from 'react-intl';
import Loader from 'react-loader-spinner';
import Locale from '../locale/locale';
import BreadCrump from './breadcrump';
import ApplicationRouter from '@/router/routerSwitch';
import {NotificationContainer} from 'react-notifications';
import {
    Container,
    Jumbotron,
    Nav,
    Navbar,
    NavbarBrand
} from 'reactstrap';

import "./layout.css";
import * as routes from '@/router/routes'
import logo from '@/img/logo.png';

class Layout extends React.Component {

    constructor(props) {
        super(props)
    }

    render() {
        return (
            <Fragment>
                {/*Header*/}
                <Navbar className="app-header">
                    <Container>
                        <NavbarBrand onClick={() => this.props.history.push(routes.index())}>
                            <img src={logo} alt="Medrag logo"/>
                        </NavbarBrand>
                        <Nav className='ml-auto'>
                            <Locale history={this.props.history}/>
                        </Nav>
                    </Container>
                </Navbar>

                {/*Content*/}
                <div className="content" style={{position: "relative"}}>
                    <BreadCrump history={this.props.history}/>
                    <Container>
                        <Jumbotron style={{width: 1110, height: 700, paddingTop: 30, textAlign: "center"}}>
                            <Suspense fallback={Layout.getLoader()}>
                                <ApplicationRouter/>
                            </Suspense>
                        </Jumbotron>
                    </Container>
                </div>

                {/*Footer*/}
                <Navbar className="app-footer">
                    <Container>
                        <NavbarBrand onClick={() => this.props.history.push(routes.index())}>
                            <img src={logo} alt="Medrag logo"/>
                        </NavbarBrand>
                        <Nav className='ml-auto'>
                            <b><FormattedMessage id={'app.index.header'}/></b>
                        </Nav>
                    </Container>
                </Navbar>
                <NotificationContainer/>
            </Fragment>
        )
    }

    static getLoader() {
        const get300 = function () {
            return 300
        };
        return (
            <Loader
                style={{position: "absolute", top: "50%", left: "50%", margin: "-150px 0 0 -150px"}}
                type="Rings"
                color="var(--app-primary-color)"
                height={get300()}
                width={get300()}
            />
        )
    }
}

export default connect()(Layout);