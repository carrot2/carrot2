import React from "react";

import "./ExportParameters.css";

import { view } from "@risingstack/react-easy-state";
import formatHighlight from 'json-format-highlight';

import { Button, Popover } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBracketsCurly } from "@fortawesome/pro-regular-svg-icons";
import { buildRequestJson } from "../../../store/services.js";

const ExportParametersBody = view(() => {
  const jsonString = JSON.stringify(buildRequestJson(true), null, "  ");
  const jsonHtml = formatHighlight(jsonString, {
    keyColor: 'prop',
    numberColor: 'number',
    stringColor: 'string',
    trueColor: 'true',
    falseColor: 'false',
    nullColor: 'null'
  });

  return (
      <div className="ExportParametersBody">
        <h4>Clustering parameters JSON</h4>
        <pre dangerouslySetInnerHTML={{__html: jsonHtml}} />
      </div>
  );
});

export const ExportParameters = () => {
  return (
      <Popover boundary="viewport">
        <Button
            icon={<FontAwesomeIcon icon={faBracketsCurly} />}
            title="Export parameters as request JSON"
            small={true}
        />
        <ExportParametersBody />
      </Popover>
  );
};