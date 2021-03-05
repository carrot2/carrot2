const path = require("path");
const { NormalModuleReplacementPlugin } = require("webpack");
const { getLoader, loaderByName } = require("@craco/craco");

const uiPath = path.resolve(__dirname, "../ui");
const appPath = path.resolve(__dirname, "src/carrot2/");

// Configuration of algorithms and branding. The location must be
// inside the directory in which the build scripts are executed,
// otherwise various things will fail.
const configPath = path.resolve(__dirname,
  process.env.CARROT2_CONFIG_PATH || "src/carrot2/config");

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

      // webpackConfig.resolve.modules = [ overridesPath, ...webpackConfig.resolve.modules ];
      webpackConfig.resolve.plugins.splice(scopePluginIndex, 1);
      webpackConfig.resolve.alias["@carrot2/app"] = appPath;
      webpackConfig.resolve.alias["@carrot2/config"] = configPath;

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
        match.loader.include = [ configPath, uiPath, ...include ];
      }

      return webpackConfig;
    }
  },
  plugins: []
};
