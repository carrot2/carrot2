import React from "react";

import "./ExportParameters.css";

import { view } from "@risingstack/react-easy-state";

import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";
import { StoreCheckbox } from "@carrotsearch/ui/form/StoreCheckbox.js";
import { CopyToClipboard } from "@carrotsearch/ui/CopyToClipboard.js";
import { JsonHighlighted } from "@carrotsearch/ui/JsonHighlighted.js";
import { ToolPopover } from "@carrotsearch/ui/ToolPopover.js";

import { Button, Position } from "@blueprintjs/core";

import { VscJson } from "react-icons/vsc";

import { buildRequestJson } from "../../../store/services.js";

const config = persistentStore("workbench:parameterExport:config", {
  onlyNonDefault: true
});

const ExportParametersBody = view(() => {
  const json = buildRequestJson(config.onlyNonDefault);
  const jsonString = JSON.stringify(json, null, "  ");
  return (
    <>
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
    </>
  );
});

export const ExportParameters = () => {
  return (
    <ToolPopover
      position={Position.TOP_LEFT}
      popoverClassName="ExportParameters"
    >
      <Button
        icon={<VscJson />}
        title="Clustering parameters as JSON"
        small={true}
        minimal={true}
      />
      <ExportParametersBody />
    </ToolPopover>
  );
};
