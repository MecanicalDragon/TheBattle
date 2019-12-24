import * as routes from '@/router/routes'

export const Home = "Home";
export const Battle = "Battle";
export const Manage = "Manage";
// export const pageThree = "Page Three";
// export const Login = "Login";
// export const Denied = "Denied";

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
// paths[pageThree] =  {
//     formattedId: "app.breadcrump.pageThree",
//     pathElements: [Home, pageTwo],
//     link: routes.pageThree()
// };
// paths[Login] =  {
//     formattedId: "app.breadcrump.login",
//     pathElements: [],
//     link: routes.login()
// };
// paths[Denied] =  {
//     formattedId: "app.breadcrump.denied",
//     pathElements: [],
//     link: routes.denied()
// };
export {paths};