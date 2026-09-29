import fs from "node:fs/promises";
import path from "node:path";

import { attributeOutlineHtml } from "./attributes-outline.js";
import { attributeDetailsHtml } from "./attributes-details.js";

/**
 * Replaces <div data-section="outline|details" data-parameters="descriptor.json">
 * with the rendered parameter outline or details. The descriptor path is relative
 * to the directory of the page.
 */
export const create = () => async ($, ctx) => {
  for (const el of $("div[data-parameters]").toArray()) {
    const $el = $(el);
    const file = path.resolve(
      ctx.sourceDir || ".",
      $el.attr("data-parameters")
    );
    const spec = JSON.parse(await fs.readFile(file, "utf8"));

    const section = $el.attr("data-section");
    let html = "";
    switch (section) {
      case "outline":
        html = attributeOutlineHtml(spec);
        break;
      case "details":
        html = attributeDetailsHtml(spec);
        break;
      default:
        console.warn(`[carrot2-doc] Unknown attribute section: ${section}.`);
    }
    $el.replaceWith(html);
  }
};
