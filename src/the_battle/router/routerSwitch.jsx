import React from 'react';
import {Switch, Route} from 'react-router';
import * as routes from './routes';
import AuthRouter from "@/router/authRouter";

const LazyIndex = React.lazy(() => import( '@/component/index'));
const LazyBattle = React.lazy(() => import( '@/component/battle/battle'));
const LazyManage = React.lazy(() => import( '@/component/manager/manage'));

export default () => (
    <Switch>
        <Route exact path={routes.index()} component={LazyIndex}/>
        <AuthRouter exact path={routes.battle()} component={LazyBattle}/>
        <AuthRouter exact path={routes.manage()} component={LazyManage}/>
    </Switch>
);