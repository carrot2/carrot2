const path = require("path");
const { NormalModuleReplacementPlugin } = require("webpack");

module.exports = {
  webpack: {
    plugins: [
      new NormalModuleReplacementPlugin(
        /.*\/generated\/iconSvgPaths.*/,
        path.resolve(__dirname, "src/blueprint/iconSvgPaths.js")
      )
    ],
    configure: webpackConfig => {
      // Remove the ModuleScopePlugin. The plugin disallows importing
      // modules from outside of the src/ directory. We need to import
      // code from libraries inside the workspace and this appears to be
      // the only way to go.
      const scopePluginIndex = webpackConfig.resolve.plugins.findIndex(
        ({ constructor }) => constructor && constructor.name === 'ModuleScopePlugin'
      );

      webpackConfig.resolve.plugins.splice(scopePluginIndex, 1);
      return webpackConfig;
    }
  },
  plugins: []
};
