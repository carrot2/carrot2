const path = require("path");
const fs = require("fs");

exports.isContainer = descriptor => {
  return false;
};

const attributeOutlineHtml = require("./attributes-outline").attributeOutlineHtml;
const attributeDetailsHtml = require("./attributes-details").attributeDetailsHtml;

exports.attributeTransformer = ($, {dir, variables, reporter, loadEmbeddedContent}) => {
  $("div[data-parameters]")
      .replaceWith((i, el) => {
        const $el = $(el);
        const rawContent = loadEmbeddedContent($el.data("parameters"), dir, variables, reporter);
        if (rawContent === undefined) {
          return "";
        }
        const spec = JSON.parse(rawContent);

        const section = $el.data("section");
        switch (section) {
          case "outline":
            return attributeOutlineHtml(spec);

          case "details":
            return attributeDetailsHtml(spec);

          default:
            reporter.warn(`Unknown attribute section: ${section}.`);
        }

        return "";
      });
  return $;
};

exports.attributeOutlineHtml = spec => {
  return attributeOutlineHtml(spec) + attributeDetailsHtml(spec);
};
