import React from 'react';
import Layout from './layout/layout'
import {Provider} from 'react-redux';
import {createStore, applyMiddleware} from 'redux';
import {reducers} from '@/reducer/reducers'
import {createBrowserHistory} from 'history';
import {withRouter} from 'react-router-dom';
import {routerMiddleware, ConnectedRouter} from 'react-router-redux';
import {composeWithDevTools} from 'redux-devtools-extension';
import LocaleProvider from '@/locale/localeProvider'
import 'react-notifications/lib/notifications.css';

export const history = createBrowserHistory();

export default function App() {

    const WrappedPage = withRouter(Layout);

    return (
        <Provider store={createStore(reducers, composeWithDevTools(applyMiddleware(routerMiddleware(history))))}>
            <LocaleProvider>
                <ConnectedRouter history={history}>
                    <WrappedPage/>
                </ConnectedRouter>
            </LocaleProvider>
        </Provider>
    );
}
