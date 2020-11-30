const escapeForHtml = require('escape-html');

const implementationWrapper = content => {
  return `<div class="implementation">${content}</div>`;
};

const attributeValue = (attribute, descriptor) => {
  const implementations = descriptor.implementations;
  if (implementations) {
    const implementationKeys = Object.keys(implementations);
    const multipleImplementations = implementationKeys.length > 1;
    const multipleImplementationsNote = multipleImplementations ?
        `<div class='multiple implementations note'>// ${implementationKeys.length} configuration variants available, choose one</div>` : "";
    return {
      value:
          multipleImplementationsNote +
          implementationKeys
              .map(i => {
                    if (multipleImplementations) {
                      return implementationWrapper(
                          attributesAndTypeHtml(implementations[i].attributes, i,
                              multipleImplementations));
                    } else {
                      return attributesHtml(implementations[i].attributes);
                    }
                  }
              )
              .join("")
    };
  } else {
    return descriptor.value;
  }
};

function attributeProperty(attribute, descriptor) {
  if (!descriptor) {
    return `"${attribute}"`;
  }

  // cheerio seems to decode entities when replacing nodes with new content, so encode twice.
  // https://github.com/cheeriojs/cheerio/issues/1219
  const title = descriptor ? escapeForHtml(escapeForHtml(descriptor.javadoc.summary)) : "";
  const href = escapeForHtml(escapeForHtml(descriptor.id));
  const link = descriptor ? `<a href="#${href}" title="${title}">${attribute}</a>` : attribute;
  return token("property", `"${link}"`);
}

const attributeHtml = (attribute, val, comma, descriptor) => (
    `<div class="attribute">${(attributeProperty(attribute,
        descriptor))}: ${value(val)}${comma ? punctuation(",") : ""}</div>`
);

const attributesHtml = attributes => {
  return Object.keys(attributes)
      .map((attribute, index, array) => {
        const value = attributeValue(attribute, attributes[attribute]);
        const isLast = index < array.length - 1;
        return attributeHtml(attribute, value, isLast, attributes[attribute]);
      })
      .join("");
};

const attributesAndTypeHtml = (attributes, type) => {
  const otherAttributes = attributesHtml(attributes);
  return attributeHtml("@type", type, otherAttributes.length > 0)
      + otherAttributes;
};

const token = (type, content) => `<span class="token ${type}">${content}</span>`;
const punctuation = char => token("punctuation", char);
const value = v => {
  const isNumber = Number.isFinite(v);
  const isNull = v === "null" || v === null;
  const isBoolean = typeof v === "boolean";
  const isNested = typeof v === "object" && v !== null && v.value !== undefined;

  if (Array.isArray(v)) {
    if (v.length !== 0) {
      throw "Non-empty arrays not implemented."
    }
    return punctuation("[") + punctuation("]");
  } else if (isNested) {
    return wrapInBrackets(v.value);
  } else if (isNull) {
    return token("keyword", "null");
  } else if (isNumber) {
    return token("number", v);
  } else if (isBoolean) {
    return token("keyword", v);
  } else {
    return token("string", `"${v}"`);
  }
};
const wrapInBrackets = string => punctuation("{") + string + punctuation("}");

const attributeOutlineHtml = spec => {
  return `<div class="gatsby-highlight"><pre class="language-json"><code>`
      + wrapInBrackets(attributesHtml(spec.attributes)) + "</code></pre></div>";
};

exports.attributeOutlineHtml = attributeOutlineHtml;