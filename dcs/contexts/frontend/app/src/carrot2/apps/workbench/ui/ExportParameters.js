import React from "react";

import "./ExportParameters.css";

import { view } from "@risingstack/react-easy-state";

import { Button, Popover, Position } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBracketsCurly } from "@fortawesome/pro-regular-svg-icons";
import { buildRequestJson } from "../../../store/services.js";
import { persistentStore } from "../../../../carrotsearch/store/persistent-store.js";
import { StoreCheckbox } from "../../../../carrotsearch/form/StoreCheckbox.js";
import { CopyToClipboard } from "../../../../carrotsearch/CopyToClipboard.js";
import { JsonHighlighted } from "../../../../carrotsearch/JsonHighlighted.js";

const config = persistentStore("workbench:parameterExport:config", {
  onlyNonDefault: true
});

const ExportParametersBody = view(() => {
  const json = buildRequestJson(config.onlyNonDefault);
  const jsonString = JSON.stringify(json, null, "  ");
  return (
    <div className="ExportParametersBody">
      <h4>Clustering parameters JSON</h4>
      <div className="ExportParametersTools">
        <StoreCheckbox
          label="Include only non-default"
          store={config}
          property="onlyNonDefault"
        />
        <CopyToClipboard contentProvider={() => jsonString} />
      </div>
      <JsonHighlighted jsonString={jsonString} />
    </div>
  );
});

export const ExportParameters = () => {
  return (
    <Popover boundary="viewport" position={Position.TOP_LEFT}>
      <Button
        icon={<FontAwesomeIcon icon={faBracketsCurly} />}
        title="Clustering parameters as JSON"
        small={true}
      />
      <ExportParametersBody />
    </Popover>
  );
};
