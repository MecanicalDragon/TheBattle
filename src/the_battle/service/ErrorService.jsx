import {NotificationManager} from "react-notifications";
import {FormattedMessage} from "react-intl";
import * as routes from "@/router/routes";
import {history} from "@/App";
import {logout} from "@/service/PlayerService";
import React from "react";

export default function defaultHandling(resp, msg) {
    switch (resp.status) {
        case 400:
            if (msg) {
                resp.text().then(msg => {
                    NotificationManager.warning("BAD REQUEST", msg, 3000);
                });
            }
            break;
        case 401:
            NotificationManager.warning(<FormattedMessage id={"app.unauthorized"}/>,
                <FormattedMessage id={"app.unauthorized.message"}/>, 5000);
            logout().then(e => {
                history.push(routes.index());
            });
            break;
        case 555:
            NotificationManager.error(<FormattedMessage id={"app.db"}/>,
                <FormattedMessage id={"app.db.message"}/>, 5000);
            break;
        default:
            NotificationManager.error(<FormattedMessage id={"app.oops"}/>,
                <FormattedMessage id={"app.oops.message"}/>, 5000);
    }
}