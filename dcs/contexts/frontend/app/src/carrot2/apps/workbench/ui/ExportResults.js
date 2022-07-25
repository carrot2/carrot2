import React from "react";

import "./ExportResults.css";

import { view } from "@risingstack/react-easy-state";
import { saveAs } from "file-saver";

import { Button, ControlGroup, Intent } from "@blueprintjs/core";

import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";
import { StoreCheckbox } from "@carrotsearch/ui/form/StoreCheckbox.js";
import { ToolPopover } from "@carrotsearch/ui/ToolPopover.js";

import { VscSave } from "react-icons/vsc";

import { buildFileName, clusterStore } from "../../../store/services.js";
import { branding } from "@carrot2/config/branding.js";

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

const ExportButton = view(() => {
  return (
    <Button
      intent={Intent.PRIMARY}
      onClick={doExport}
      disabled={!exportConfig.includeDocuments && !exportConfig.includeClusters}
    >
      Export
    </Button>
  );
});

const ExportBody = () => {
  return (
    <div>
      <p>Choose the output format:</p>
      <ExportFormatConfig />

      <p>Chose what to export:</p>
      <ExportOutputConfig />

      <ExportButton />
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

const exportSheet = async format => {
  const XLSX = await import(/* webpackChunkName: "xlsx" */ "xlsx");

  let rows = [];
  if (exportConfig.includeClusters) {
    const documents = getExportDocs();
    const collect = (c, parentLabels) => {
      c.documents.forEach(docIndex => {
        const doc = {};
        parentLabels.forEach((l, index) => {
          doc[`Cluster Level ${index + 1}`] = l.join(", ");
        });
        doc[`Cluster Level ${parentLabels.length + 1}`] = c.labels.join(",");

        if (exportConfig.includeDocuments) {
          Object.assign(doc, documents[docIndex]);
        }

        rows.push(doc);
      });

      const newParents = [...parentLabels, c.labels];
      c.clusters?.forEach(c => {
        collect(c, newParents);
      });
    };

    clusterStore.clusters.forEach(c => {
      collect(c, []);
    });
  } else {
    rows = getExportDocs();
  }

  // Flatten arrays into comma-separated lists
  rows.forEach(r => {
    Object.keys(r).forEach(p => {
      if (Array.isArray(r[p])) {
        r[p] = r[p].flat(Number.MAX_SAFE_INTEGER).join(", ");
      }
    });
  });

  const wb = XLSX.utils.book_new();
  wb.Props = {
    Title: `${branding.product} Clustering Workbench results export`,
    Author: `${branding.product} Clustering Workbench`,
    CreatedDate: new Date()
  };

  const ws = XLSX.utils.json_to_sheet(rows);
  wb.SheetNames.push("Export");
  wb.Sheets["Export"] = ws;
  XLSX.writeFile(wb, buildFileName("result", format));
};

export const ExportResults = () => {
  return (
    <ToolPopover className="Export" boundary="viewport">
      <Button
        minimal={true}
        small={true}
        text="Export"
        open={true}
        icon={<VscSave />}
      />
      <ExportBody />
    </ToolPopover>
  );
};
