const path = require("path");
const { NormalModuleReplacementPlugin } = require("webpack");
const { getLoader, loaderByName } = require("@craco/craco");

const absolutePath = path.join(__dirname, "../ui-components");


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

      // Create React App processes through Babel only the files found in /src.
      // To use common components in the source form, we need to add the
      // components directory to Babel processing as well.
      const { isFound, match } = getLoader(
        webpackConfig,
        loaderByName("babel-loader")
      );
      if (isFound) {
        const include = Array.isArray(match.loader.include)
          ? match.loader.include
          : [match.loader.include];
        match.loader.include = include.concat(absolutePath);
      }

      return webpackConfig;
    }
  },
  plugins: []
};
