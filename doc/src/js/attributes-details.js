const depthFirstAttributes = descriptor => {
  const collect = (prefix, descriptor, target) => {
    Object.keys(descriptor.attributes).forEach(k => {
      const attribute = descriptor.attributes[k];
      const path = prefix + k;
      const entry = {
        key: k,
        path: path,
        attribute: attribute
      };
      target.push(entry);

      if (attribute.attributes) {
        collect(path + ".", attribute, target);
      }
      if (attribute.implementations) {
        const keys = Object.keys(attribute.implementations);
        entry.implementations = keys.length;
        if (keys.length === 1) {
          collect(path + ".", attribute.implementations[keys[0]], target);
        }
      }
    });

    return target;
  };

  return collect("", descriptor, []);
};

const descriptionText = attribute => {
  const javadoc = attribute.javadoc;
  if (javadoc.text) {
    const summary = javadoc.summary;
    const text = javadoc.text;

    if (text.startsWith(summary) && text.length > summary.length) {
      return text.substring(summary.length);
    } else{
      return text;
    }
  } else {
    return attribute.description;
  }
};

const implementationDetailsHtml = implementation => (`<li>
  <p><code>${implementation.name}</code></p>
  
  <p>
    ${descriptionText(implementation)}
  </p>
  
  ${allAttributeDetailsHtml(implementation)}
</li>`);

const allImplementationDetailsHtml = attribute => (`<p>Available implementations:</p>
<ul>
${Object.keys(attribute.implementations)
    .map(k => attribute.implementations[k])
    .map(implementationDetailsHtml)
    .join("")}
</ul>`);

const attributeDetailsHtml = (entry) => {
  const attribute = entry.attribute;
  const implementationsHtml = entry.implementations > 1
      ? allImplementationDetailsHtml(attribute) : "";
  const constraintsHtml = attribute.constraints ?
      `<dt>Constraints</dt><dd>${attribute.constraints.join(" and ")}</dd>`
      : "";

  return `<section id="${attribute.path}" class="api attribute">
  <h3>${entry.key}</h3>
  
  <dl class="compact narrow">
    <dt>Type</dt>
    <dd>${attribute.type}</dd>
    <dt>Default</dt>
    <dd>${attribute.value}</dd>
    ${constraintsHtml}
    <dt>Path</dt>
    <dd>${entry.path}</dd>
    <dt>Java snippet</dt>
    <dd>${attribute.path}</dd>
  </dl>
  
  <p>
    ${descriptionText(attribute)}
  </p>
  
  ${implementationsHtml}
</section>`
};

const allAttributeDetailsHtml = descriptor => {
  return depthFirstAttributes(descriptor).map(attributeDetailsHtml).join("");
};

exports.attributeDetailsHtml = allAttributeDetailsHtml;