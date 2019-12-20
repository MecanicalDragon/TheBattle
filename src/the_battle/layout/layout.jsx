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
    NavbarBrand,
    Suspence,
    Button
} from 'reactstrap';

import "./layout.css";
import * as routes from '@/router/routes'
import logo from '@/logo.png';

class Layout extends React.Component {

    constructor(props) {
        super(props)
    }

    render() {
        return (
            <Fragment>
                {/*Header*/}
                <Navbar className="magenta-bg">
                    <Container>
                        <NavbarBrand href="javascript:void(0);"
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
                    <Suspense fallback={<Loader
                        style={{position: "absolute", top: "50%", left: "50%", margin: "-150px 0 0 -150px"}}
                        type="Rings"
                        color="#e20074"
                        height="300"
                        width="300"
                    />}>
                        <ApplicationRouter/>
                    </Suspense>
                </div>

                {/*Footer*/}
                <Navbar style={{backgroundColor: "grey"}}>
                    <Container>
                        <NavbarBrand href="javascript:void(0);"
                                     onClick={() => this.props.history.push(routes.index())}>
                            <img style={{height: 30, width: 30}} src={logo} alt="Medrag logo"/>
                        </NavbarBrand>
                        <Nav className='ml-auto'>
                            <b><FormattedMessage id={'app.title'}/></b>
                        </Nav>
                    </Container>
                </Navbar>
                <NotificationContainer/>
            </Fragment>
        )
    }
}

export default connect()(Layout);