const rawLoader = require('craco-raw-loader');

module.exports = {
  plugins: [
    {
      plugin: rawLoader,
      options: { test: /\.html$/ }
    }
  ]
}