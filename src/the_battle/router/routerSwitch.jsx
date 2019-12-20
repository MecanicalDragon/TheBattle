import React from 'react';
import {Switch, Route} from 'react-router';
import * as routes from './routes';

const LazyIndex = React.lazy(() => import( '@/component/index'));
const LazyBattle = React.lazy(() => import( '@/component/battle'));
// const LazyDenied = React.lazy(() => import( '@/component/denied'));
// const LazyLoginPage = React.lazy(() => import( '@/component/login'));
// const LazyFirstPage = React.lazy(() => import( '@/component/first'));
// const LazySecondPage = React.lazy(() => import( '@/component/second'));
// const LazyThirdPage = React.lazy(() => import( '@/component/third'));

export default () => (
    <Switch>
        <Route exact path={routes.index()} component={LazyIndex}/>
        <Route exact path={routes.battle()} component={LazyBattle}/>
        {/*<SecuredRouter exact path={routes.pageOne()} roles={[roles.user]} component={LazyFirstPage}/>*/}
        {/*<SecuredRouter exact path={routes.pageTwo()} roles={[roles.user]} component={LazySecondPage}/>*/}
        {/*<SecuredRouter exact path={routes.pageThree()} roles={[roles.admin]} component={LazyThirdPage}/>*/}
        {/*<Route exact path={routes.login()} component={LazyLoginPage}/>*/}
        {/*<Route exact path={routes.denied()} component={LazyDenied}/>*/}
    </Switch>
);