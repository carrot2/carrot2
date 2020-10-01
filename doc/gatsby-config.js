const attributeTransformer = require("./src/js/attributes").attributeTransformer;

module.exports = {
  pathPrefix: '__RELATIVIZE_PREFIX__',
  siteMetadata: {
    title: `Carrot2 docs`,
    description: `User and developer manual for the Carrot2 text clustering engine.`,
    lang: `en`,
    indexAlias: `/hello-carrot2/`
  },
  plugins: [
    {
      resolve: `@carrotsearch/gatsby-theme-apidocs`,
      options: {
        navigation: `${__dirname}/src/navigation.json`,
        logo:   `${__dirname}/src/logo.html`,
        footer: `${__dirname}/src/footer.html`,
        basePath: "src/content",
        variables: {
          "PROJECT_VERSION": process.env.REACT_APP_VERSION || "unset",
          "JAVA_EXAMPLES": `${__dirname}/../core-examples/src/test/java/org/carrot2/examples`,
          "DCS_EXAMPLES": `${__dirname}/../dcs/examples/src/main/java/org/carrot2/dcs/examples`,
          "CORE": `${__dirname}/../core/src/main/java`
        },
        transformers: [
          attributeTransformer
        ]
      }
    }
  ]
};