import React from 'react';
import {Route, Redirect} from 'react-router-dom';
import * as routes from './routes';

import {isPlayerLoggedInInRedux} from '@/service/PlayerService';

const AuthRouter = ({component: Component, ...rest}) => (
    <Route {...rest} render={props => {
        if (!isPlayerLoggedInInRedux())
            return <Redirect to={{pathname: routes.index(), state: {from: props.location}}}/>
        return <Component {...props} />
    }}/>
);

export default AuthRouter;