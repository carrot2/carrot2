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
      resolve: `gatsby-source-filesystem`,
      options: {
        name: `content`,
        path: `${__dirname}/src/content`,
      },
    },
    {
      resolve: `@carrotsearch/gatsby-plugin-apidocs`,
      options: {
        navigation: `${__dirname}/src/navigation.json`,
        logo:   `${__dirname}/src/logo.html`,
        footer: `${__dirname}/src/footer.html`,
        basePath: "src/content"
      }
    },
    {
      resolve: `gatsby-plugin-nprogress`,
      options: {
        color: `#ffaa00`,
        showSpinner: false
      }
    },
    `gatsby-plugin-react-helmet`,
    `gatsby-plugin-dark-mode`,
    {
      resolve: `@carrotsearch/gatsby-transformer-html`,
      options: {
        variables: {
          "PROJECT_VERSION": process.env.REACT_APP_VERSION || "unset",
          "JAVA_EXAMPLES": `${__dirname}/../core-examples/src/test/java/org/carrot2/examples`,
          "CORE": `${__dirname}/../core/src/main/java`,
        }
      }
    },
    '@carrotsearch/gatsby-plugin-content-search',
    '@carrotsearch/gatsby-plugin-relativize',
    `gatsby-plugin-offline`,
    `gatsby-plugin-catch-links`
  ]
};