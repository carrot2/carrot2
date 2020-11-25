import React from "react";

import { view } from "@risingstack/react-easy-state";
import { saveAs } from "file-saver";

import { Button, ControlGroup, Intent, Popover } from "@blueprintjs/core";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFileExcel } from "@fortawesome/pro-regular-svg-icons";
import { persistentStore } from "../../../../carrotsearch/store/persistent-store.js";
import { StoreCheckbox } from "../../../../carrotsearch/ui/form/StoreCheckbox.js";
import { buildFileName, clusterStore } from "../../../store/services.js";

const exportConfig = persistentStore({
  format: "excel",
  includeClusters: true,
  includeDocuments: true
});

export const ExportFormatButton = view(({ value, ...props }) => {
  const active = exportConfig.format === value;
  return (
    <Button
      outlined={true}
      active={active}
      intent={active ? Intent.PRIMARY : Intent.NONE}
      onClick={() => (exportConfig.format = value)}
      {...props}
    />
  );
});

const ExportFormatConfig = view(() => {
  return (
    <ControlGroup style={{ marginBottom: "2em" }}>
      <ExportFormatButton value={"excel"} text="Excel" />
      <ExportFormatButton value={"openoffice"} text="OpenOffice" />
      <ExportFormatButton value={"csv"} text="CSV" />
      <ExportFormatButton value={"json"} text="JSON" />
    </ControlGroup>
  );
});

const ExportOutputConfig = view(() => {
  return (
    <div style={{ marginBottom: "2em" }}>
      <StoreCheckbox label="Clusters" store={exportConfig} property="includeClusters" />
      <StoreCheckbox label="Documents" store={exportConfig} property="includeDocuments" />
    </div>
  );
});

const ExportBody = () => {
  return (
    <div>
      <p>Choose the output format:</p>
      <ExportFormatConfig />

      <p>Chose what to export:</p>
      <ExportOutputConfig />

      <Button intent={Intent.PRIMARY} onClick={doExport}>Export</Button>
    </div>
  );
};

const doExport = () => {
  switch (exportConfig.format) {
    case "json":
      exportJson();
      break;
  }
};

const getExportDocs = () => clusterStore.documents.map(d => {
  // remove our internal properties
  const { __id, __rank, clusters, ...toExport } = d;
  return toExport;
});
const getExportClusters = () => clusterStore.clusters.map(function prepare(c) {
  // Remove the "uniqueDocs" property
  const { uniqueDocuments, ...toExport } = c;
  if (toExport.clusters) {
    toExport.clusters = toExport.clusters.map(prepare);
  }
  return toExport;
});

const exportJson = () => {
  const type = "application/json";

  let toExport = {};
  if (exportConfig.includeDocuments && exportConfig.includeClusters) {
    toExport = {
      documents: getExportDocs(),
      clusters: getExportClusters()
    };
  } else {
    if (exportConfig.includeDocuments) {
      toExport = getExportDocs();
    } else {
      toExport = getExportClusters();
    }
  }

  saveAs(
    new Blob([JSON.stringify(toExport)], { type: type }),
    buildFileName("result", `json`)
  );
};


export const Export = () => {
  return (
    <Popover className="Export" boundary="viewport">
      <Button
        minimal={true}
        small={true}
        text="Export"
        open={true}
        icon={<FontAwesomeIcon icon={faFileExcel} />}
      />
      <ExportBody />
    </Popover>
  );
};
