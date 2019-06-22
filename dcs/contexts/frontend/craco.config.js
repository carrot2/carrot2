const path = require("path");
const rawLoader = require('craco-raw-loader');
const { NormalModuleReplacementPlugin, ProgressPlugin } = require('webpack');

module.exports = {
  webpack: {
    plugins: [
      new NormalModuleReplacementPlugin(
        /.*\/generated\/iconSvgPaths.*/,
        path.resolve(__dirname, "src/blueprint/iconSvgPaths.js"),
      )
    ]
  },
  plugins: [
    {
      plugin: rawLoader,
      options: { test: /\.html$/ }
    }
  ]
};