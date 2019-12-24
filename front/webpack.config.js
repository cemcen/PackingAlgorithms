'use strict';
let path = require('path');
const { VueLoaderPlugin } = require('vue-loader');

module.exports = {
    entry: path.resolve('./src/main.js'),
    output: {
        path: path.resolve('../public/js'),
        filename: 'build.js'
    },
    module: {
        rules: [
            {
                test: /\.js$/,
                loader: 'babel-loader',
                exclude: /node_modules/
            },
            {
                test: /\.vue$/,
                loader: 'vue-loader',
                options: {
                }
            },
            { test: /\.s(c|a)ss$/,
                use: [ 'vue-style-loader', 'css-loader',
                    {
                        loader: 'sass-loader',
                        options:
                            {
                                implementation: require('sass'),
                                sassOptions:
                                    {
                                        indentedSyntax: true
                                    }
                            }
                    }]
            },
            {
                test: /\.css$/,
                loader: ['style-loader', 'css-loader']
            },
            {
                test: /\.(png|jpg|gif|svg)$/,
                loader: 'file-loader',
                options: {
                    name: '[name].[ext]?[hash]'
                }
            },
            {
                test: /\.(eot|svg|ttf|woff|woff2)$/,
                loader: 'file-loader?name=[name].[ext]'
            },
        ],
    },
    plugins:[
        new VueLoaderPlugin()
    ]
};