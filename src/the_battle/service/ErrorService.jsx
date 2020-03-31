import {NotificationManager} from "react-notifications";
import {FormattedMessage} from "react-intl";
import * as routes from "@/router/routes";
import {history} from "@/App";
import {logout} from "@/service/PlayerService";
import React from "react";

/**
 * Default exception handling
 * @param resp - not 200 status response
 * @param msg - true if you want to show error notification
 */
export default function defaultHandling(resp, msg) {
    switch (resp.status) {
        case 400:
            if (msg) {
                resp.text().then(msg => {
                    NotificationManager.warning(msg, "BAD REQUEST", 5000);
                });
            }
            break;
        case 401:
            NotificationManager.warning(<FormattedMessage id={"app.unauthorized.message"}/>,
                <FormattedMessage id={"app.unauthorized"}/>,5000);
            logout().then(e => {
                history.push(routes.index());
            });
            break;
        case 555:
            NotificationManager.error(<FormattedMessage id={"app.db.message"}/>,
                <FormattedMessage id={"app.db"}/>, 5000);
            break;
        default:
            NotificationManager.error(<FormattedMessage id={"app.oops.message"}/>,
                <FormattedMessage id={"app.oops"}/>, 5000);
    }
}