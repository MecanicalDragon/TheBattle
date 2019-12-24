// Get public path
const publicPath = process.env.ASSET_PATH;

// Pages
export const index = () => `${publicPath}`;
export const battle = () => `${publicPath}battle`;
export const manage = () => `${publicPath}manage`;
// export const pageThree = () => `${publicPath}page3`;
// export const login = () => `${publicPath}login`;
// export const denied = () => `${publicPath}denied`;