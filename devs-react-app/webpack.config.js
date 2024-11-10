const path = require('path');
const htmlWebpackPlugin = require('html-webpack-plugin');
const TerserPlugin = require('terser-webpack-plugin');

module.exports = {
    mode: 'development',
    entry: {
        app: './src/index.tsx',
        "editor.worker": 'monaco-editor-core/esm/vs/editor/editor.worker.js',
        "rdevsLangWorker": './src/rdevs-lang/rdevslang.worker.ts'
    },
    output: {
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
        path: path.resolve(__dirname, 'build')
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
        })
    ],
    optimization: {
        minimize: true,
        minimizer: [new TerserPlugin({
            terserOptions: {
                // Agrega opciones de configuración aquí
                ecma: 2020,
            },
        })],
    }
}