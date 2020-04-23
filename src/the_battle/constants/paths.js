import * as routes from '@/router/routes'

export const Home = "Home";
export const Battle = "Battle";
export const Manage = "Manage";

const paths = {};
paths[Home] = {
    formattedId: "app.breadcrump.home",
    pathElements: [],
    link: routes.index()
};
paths[Battle] = {
    formattedId: "app.breadcrump.battle",
    pathElements: [Home],
    link: routes.battle()
};
paths[Manage] =  {
    formattedId: "app.breadcrump.manage",
    pathElements: [Home],
    link: routes.manage()
};
export {paths};