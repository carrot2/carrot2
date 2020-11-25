import React from "react";

import "./Export.css";

import { view } from "@risingstack/react-easy-state";
import { saveAs } from "file-saver";

import { Button, ControlGroup, Intent, Popover } from "@blueprintjs/core";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFileExcel } from "@fortawesome/pro-regular-svg-icons";
import { persistentStore } from "../../../../carrotsearch/store/persistent-store.js";
import { StoreCheckbox } from "../../../../carrotsearch/ui/form/StoreCheckbox.js";
import { buildFileName, clusterStore } from "../../../store/services.js";

const exportConfig = persistentStore("workbench:export:config", {
  format: "excel",
  includeClusters: true,
  includeDocuments: true
});

export const ExportFormatButton = view(({ value, ...props }) => {
  const active = exportConfig.format === value;
  return (
    <Button
      active={active}
      intent={active ? Intent.PRIMARY : Intent.NONE}
      onClick={() => (exportConfig.format = value)}
      {...props}
    />
  );
});

const ExportFormatConfig = view(() => {
  return (
    <ControlGroup className="ExportFormatConfig">
      <ExportFormatButton value={"excel"} text="Excel" />
      <ExportFormatButton value={"openoffice"} text="OpenOffice" />
      <ExportFormatButton value={"csv"} text="CSV" />
      <ExportFormatButton value={"json"} text="JSON" />
    </ControlGroup>
  );
});

const ExportOutputConfig = view(() => {
  return (
    <div className="ExportOutputConfig">
      <StoreCheckbox
        label="Clusters"
        store={exportConfig}
        property="includeClusters"
      />
      <StoreCheckbox
        label="Documents"
        store={exportConfig}
        property="includeDocuments"
      />
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

      <Button intent={Intent.PRIMARY} onClick={doExport}>
        Export
      </Button>
    </div>
  );
};

const doExport = async () => {
  switch (exportConfig.format) {
    case "json":
      exportJson();
      break;

    case "excel":
      await exportSheet("xlsx");
      break;

    case "openoffice":
      await exportSheet("ods");
      break;

    case "csv":
      await exportSheet("csv");
      break;

    default:
  }
};

const getExportDocs = () =>
  clusterStore.documents.map(d => {
    // remove our internal properties
    const { __id, __rank, clusters, ...toExport } = d;
    return toExport;
  });
const getExportClusters = () =>
  clusterStore.clusters.map(function prepare(c) {
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

const exportSheet = async (format) => {
  const XLSX = await import("xlsx");

  // TODO: Flatten arrays into comma-separated lists?
  const docs = getExportDocs();
  const ws = XLSX.utils.json_to_sheet(docs);
  const wb = XLSX.utils.book_new();
  wb.SheetNames.push("Export");
  wb.Sheets["Export"] = ws;
  XLSX.writeFile(wb, buildFileName("result", format));
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
