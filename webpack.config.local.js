const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const HtmlWebpackTemplate = require('html-webpack-template');
const CleanWebpackPlugin = require('clean-webpack-plugin');
const webpack= require('webpack');
const publicPath = '/';
const path = require('path');

module.exports = {
    devServer: {
        contentBase: `${publicPath}`,
        historyApiFallback: {
            rewrites: [
                {from: /./, to: `/index.html`},
            ]
        },
        open: true,
        port: 9095,
        publicPath: `${publicPath}`,
        // proxy: [{
        //     context: ['/api', '/endpoint'],
        //     target: {
        //         host: "localhost",
        //         protocol: 'http:',
        //         port: 8090
        //     }
        // }]
    },
    entry: {
        index: path.join(__dirname, 'src/the_battle/index.js'),
    },
    output: {
        filename: 'assets/javascripts/[name].[hash].js',
        path: path.join(__dirname, 'src/main/resources/static'),
        publicPath: `${publicPath}`,
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: "babel-loader"
            },
            {
                test: /\.css$/,
                use: [MiniCssExtractPlugin.loader, 'css-loader']
            },
            {
                test: /\.less$/,
                use: [MiniCssExtractPlugin.loader, 'css-loader', 'less-loader']
            },
            {
                test: /\.(ico|png|gif|jpe?g)$/,
                use: {
                    loader: 'file-loader',
                    options: {name: 'assets/images/[name]/[hash].[ext]'}
                }
            },
            {
                test: /\.(svg|woff|woff2|eot|ttf)$/,
                use: {
                    loader: 'file-loader',
                    options: {name: 'assets/fonts/[name]/[hash].[ext]'}
                }
            },
            {test: /\.html$/, use: 'html-loader'},
        ]
    },
    resolve: {
        extensions: ['.js', '.jsx'],
        modules: ['node_modules', 'src/the_battle'],
        symlinks: false,
        alias: {
            '@': path.resolve(__dirname, 'src/the_battle'),
        }
    },
    plugins: [
        new CleanWebpackPlugin(),
        new webpack.DefinePlugin({
            'process.env': {ASSET_PATH: JSON.stringify(publicPath)},
            SOME_VARIABLE: JSON.stringify('Caesar'),
            DEPLOYED_URL: JSON.stringify('http://localhost:9191/'),
        }),
        new MiniCssExtractPlugin({
            filename: 'assets/stylesheets/[name]/[hash].css'
        }),
        new HtmlWebpackPlugin({
            appMountId: 'root',
            filename: './index.html',
            inject: false,
            template: HtmlWebpackTemplate,
            favicon: 'src/the_battle/favicon.ico',
            title: 'The Battle',
            chunks: ['index'],
        }),
    ]
};