const path = require('path');
const htmlWebpackPlugin = require('html-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');
const CopyPlugin = require('copy-webpack-plugin');

module.exports = {
    mode: 'development',
    entry: {
        app: './src/index.tsx',
        "editor.worker": 'monaco-editor-core/esm/vs/editor/editor.worker.js',
        "rdevsLangWorker": './src/rdevs-lang/rdevslang.worker.ts'
    },
    output: {
        publicPath: '/a02/',
        globalObject: 'self',
        filename: (chunkData) => {
            switch (chunkData.chunk.name) {
                case 'editor.worker':
                    return 'editor.worker.js';
                case 'rdevsLangWorker':
                    return "rdevsLangWorker.js"
                default:
                    return 'bundle.[hash].js';
            }
        },
        // path: path.resolve(__dirname, 'build')
        path: path.resolve(__dirname, 'dist')
    },
    resolve: {
        extensions: ['.ts', '.tsx', '.js', '.jsx', '.css']
    },
    module: {
        rules: [
            {
                test: /\.tsx?/,
                loader: 'ts-loader'
            },
            {
                test: /\.css/,
                use: ['style-loader', 'css-loader']
            }
        ]
    },
    plugins: [
        new htmlWebpackPlugin({
            template: './src/index.html'
        }),
        new CopyPlugin({
            patterns: [
              { 
                from: 'public', 
                to: '', 
                globOptions: {
                  ignore: ['**/index.html']
                }
              },
            ],
        }),   
    ],
    optimization: {
        minimize: true,
        minimizer: [new TerserPlugin({
            terserOptions: {
                // Agrega opciones de configuración aquí
                ecma: 2020,
            },
        })],
    },
    devServer: {
        historyApiFallback: {
          index: '/a02/'
        },
        port: 3002,
        host: 'localhost',
        publicPath: '/a02/',
        // contentBase: path.join(__dirname, 'public'),
        contentBase: path.join(__dirname, 'dist'),
        open: true,
        openPage: 'a02/'
    }
}