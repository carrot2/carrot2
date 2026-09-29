import escapeForHtml from "escape-html";

import { attributeOutlineHtml } from "./attributes-outline.js";

const depthFirstAttributes = descriptor => {
  const collect = (descriptor, target) => {
    Object.keys(descriptor.attributes).forEach(k => {
      const attribute = descriptor.attributes[k];

      target.push(attribute);

      if (attribute.attributes) {
        collect(attribute, target);
      }
      if (attribute.implementations) {
        const keys = Object.keys(attribute.implementations);
        if (keys.length === 1) {
          collect(attribute.implementations[keys[0]], target);
        }
      }
    });

    return target;
  };

  return collect(descriptor, []);
};

const attrValueToString = value => {
  if (Array.isArray(value)) {
    return "[]";
  } else {
    return value;
  }
};

const descriptionText = attribute => {
  const javadoc = attribute.javadoc;
  if (javadoc.text) {
    const summary = javadoc.summary;
    const text = javadoc.text;
    return text;
  } else {
    return attribute.description;
  }
};

const implementationDetailsHtml = implementation => `<li>
  <p><code>${implementation.name}</code></p>
  
  <p>
    ${descriptionText(implementation)}
  </p>
  
  ${childAttributesOutline(implementation)}
  
  ${allAttributeDetailsHtml(implementation)}
</li>`;

const childAttributesOutline = descriptor => {
  if (Object.keys(descriptor.attributes).length > 0) {
    return attributeOutlineHtml(descriptor);
  } else {
    return "";
  }
};

const allImplementationDetailsHtml =
  attribute => `<p>Available implementations:</p>
<ol>
${Object.keys(attribute.implementations)
  .map(k => attribute.implementations[k])
  .map(implementationDetailsHtml)
  .join("")}
</ol>`;

const attributeDetailsHtml = attribute => {
  const name = attribute.pathRest.split(".").pop();

  let implementationsHtml = "";
  let outline = "";

  const implementations = attribute.implementations;
  if (implementations) {
    const implementationKeys = Object.keys(implementations);
    if (implementationKeys.length > 1) {
      implementationsHtml = allImplementationDetailsHtml(attribute);
    }

    if (implementationKeys.length === 1) {
      outline = childAttributesOutline(implementations[implementationKeys[0]]);
    }
  }

  const constraintsHtml = attribute.constraints
    ? `<dt>Constraints</dt><dd>${escapeForHtml(
        attribute.constraints.join(" and ")
      )}</dd>`
    : "";

  // data-toc="omit": hundreds of parameters would flood the page outline.
  return `<section id="${attribute.id}" class="api attribute" data-toc="omit">
  <h3>${name}</h3>
  
  <dl class="compact narrow">
    <dt>Type</dt>
    <dd>${attribute.type}</dd>
    <dt>Default</dt>
    <dd>${attrValueToString(attribute.value)}</dd>
    ${constraintsHtml}
    <dt>Path</dt>
    <dd>${attribute.pathRest}</dd>
    <dt>Java snippet</dt>
    <dd>${attribute.pathJava}</dd>
  </dl>
  
  <p>
    ${descriptionText(attribute)}
  </p>
  
  ${outline}
  
  ${implementationsHtml}
</section>`;
};

const allAttributeDetailsHtml = descriptor => {
  return depthFirstAttributes(descriptor).map(attributeDetailsHtml).join("");
};

export { allAttributeDetailsHtml as attributeDetailsHtml };
