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
                <Navbar className="primary-bg">
                    <Container>
                        <NavbarBrand style={{cursor: "pointer"}}
                                     onClick={() => this.props.history.push(routes.index())}>
                            <img style={{height: 50, width: 50}} src={logo} alt="Medrag logo"/>
                        </NavbarBrand>
                        <Nav className='ml-auto'>
                            <Locale history={this.props.history}/>
                        </Nav>
                    </Container>
                </Navbar>

                {/*Content*/}
                <div className="content" style={{position: "relative"}}>
                    <BreadCrump history={this.props.history}/>
                    <Suspense fallback={Layout.getLoader()}>
                        <ApplicationRouter/>
                    </Suspense>
                </div>

                {/*Footer*/}
                <Navbar style={{backgroundColor: "grey"}}>
                    <Container>
                        <NavbarBrand style={{cursor: "pointer"}}
                                     onClick={() => this.props.history.push(routes.index())}>
                            <img style={{height: 30, width: 30}} src={logo} alt="Medrag logo"/>
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
        const get300 = function(){
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