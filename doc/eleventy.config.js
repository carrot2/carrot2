import apidocs from "@carrotsearch/eleventy-apidocs";

import { create as attributeTransformer } from "./src/js/attributes.js";

export default async function (eleventyConfig) {
  // The servlet context descriptor and the favicon, copied verbatim to the site root.
  eleventyConfig.addPassthroughCopy({ static: "/" });

  return apidocs(eleventyConfig, {
    navigation: "src/navigation.json",
    logo: "src/logo.html",
    footer: "src/footer.html",
    contentDir: "src/content",
    styles: [
      "src/styles/theme.css",
      "src/styles/custom.css",
      "src/styles/tables.css",
      "src/styles/attributes.css"
    ],
    variables: {
      PROJECT_VERSION: process.env.REACT_APP_VERSION || "(version unset)"
    },
    transformers: [attributeTransformer()],
    linkCheck: {
      skip: [
        // The reader's own DCS instance (the crawler itself runs on localhost, so
        // only this port is excluded).
        "^https?://localhost:8080(/|$)"
      ]
    }
  });
}
