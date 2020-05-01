// Get public path
const publicPath = process.env.ASSET_PATH;

// Pages
export const index = () => `${publicPath}`;
export const battle = () => `${publicPath}battle`;
export const manage = () => `${publicPath}manage`;
export const profile = () => `${publicPath}profile`;