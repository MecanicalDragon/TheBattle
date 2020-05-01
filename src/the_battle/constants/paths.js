import * as routes from '@/router/routes'

export const Home = "Home";
export const Battle = "Battle";
export const Manage = "Manage";
export const Profile = "Profile";

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
paths[Profile] =  {
    formattedId: "app.breadcrump.profile",
    pathElements: [Home],
    link: routes.profile()
};
export {paths};