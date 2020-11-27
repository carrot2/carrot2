import React from "react";

import "./JsonHighlighted.css";

import formatHighlight from "json-format-highlight";

export const JsonHighlighted = ({ jsonString }) => {
  const jsonHtml = formatHighlight(jsonString, {
    keyColor: "prop",
    numberColor: "number",
    stringColor: "string",
    trueColor: "true",
    falseColor: "false",
    nullColor: "null"
  });

  return (
    <pre
      className="JsonHighlighted"
      dangerouslySetInnerHTML={{ __html: jsonHtml }}
    />
  );
};
