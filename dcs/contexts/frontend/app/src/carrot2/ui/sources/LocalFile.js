import React from "react";

import { JsonHighlighted } from "@carrotsearch/ui/JsonHighlighted.js";

import { createResultConfigStore } from "./CustomSchemaResult.js";
import {
  createFieldChoiceSetting,
  createSchemaExtractorStores,
  createSource
} from "./CustomSchemaSource.js";

import { parseFile } from "./file-parser.js";
import { GenericSearchEngineErrorMessage } from "../../apps/search-app/ui/ErrorMessage.js";

const resultConfigStore = createResultConfigStore("localFile");

const { schemaInfoStore, resultHolder } =
  createSchemaExtractorStores("localFile");

const LocalFileFormatInfo = () => {
  const jsonString =
    "[\n" +
    exampleDocs.map(d => "  " + JSON.stringify(d)).join(",\n") +
    ",\n  ...\n]";
  return (
    <>
      <p>The following file types are supported:</p>
      <ul>
        <li>
          <p>
            <strong>Excel, OpenOffice, CSV</strong> (
            <SampleDownload file={"serverfault.xlsx"} />) &mdash; one document
            per row, the first row is treated as a header with document field
            names.
          </p>
        </li>
        <li>
          <p>
            <strong>JSON</strong> (<SampleDownload file={"serverfault.json"} />)
            &mdash; an array of flat JSON objects representing documents to
            cluster:
          </p>
          <JsonHighlighted jsonString={jsonString} />
          <p>
            The objects can have text and non-text fields, Carrot<sup>2</sup>{" "}
            will try to detect the ones to cluster.
          </p>
        </li>
        <li>
          <p>
            <strong>XML</strong> (<SampleDownload file={"seattle.xml"} />)
            &ndash; the{" "}
            <a
              href="https://doc.carrot2.org/#section.architecture.input-xml"
              target="_blank"
              rel="noreferrer"
            >
              legacy Carrot<sup>2</sup> XML format
            </a>
            .
          </p>
        </li>
      </ul>
    </>
  );
};

const settings = [
  {
    id: "file",
    type: "group",
    label: "Local file",
    description:
      "Loads documents from a local file. Carrot2 XML, JSON, CSV and Excel formats are supported.",
    settings: [
      {
        id: "file:file",
        type: "file",
        label: "File",
        description: (
          <>
            <p>The file from which to read data for clustering.</p>
            <LocalFileFormatInfo />
          </>
        ),
        get: () => null,
        set: (sett, file) => {
          schemaInfoStore.load(async logger => {
            return parseFile(file, logger);
          });
        }
      },
      createFieldChoiceSetting("file", schemaInfoStore)
    ]
  }
];

const localFileSource = () => {
  // Load the field display configuration only when the documents are requested
  // for clustering. If we did that right after the file was loaded, we'd swap
  // the configuration for the set of documents being currently displayed
  // (possibly with a different schema).
  resultConfigStore.load(schemaInfoStore.fieldStats, resultHolder);
  return {
    query: resultHolder.query,
    matches: resultHolder.documents.length,
    documents: resultHolder.documents
  };
};

const exampleDocs = [
  { title: "Title 1", body: "Text", views: 583 },
  { title: "Title 2", body: "Text", views: 23 }
];

export const SampleDownload = ({ file }) => {
  return (
    <a href={`workbench/examples/${file}`} download={true}>
      download sample
    </a>
  );
};

export const localFileSourceDescriptor = createSource(
  schemaInfoStore,
  resultConfigStore,
  {
    label: "Local file",
    descriptionHtml:
      "content read from a local file in Carrot2 XML, JSON, CSV or Excel format.",
    contentSummary: "Excel, CSV, OpenOffice, JSON or XML files",
    source: localFileSource,
    getSettings: () => settings,
    createError: e => <GenericSearchEngineErrorMessage error={e} />,
    createIntroHelp: () => (
      <>
        <p>Data for clustering is extracted from the file you provide.</p>
        <LocalFileFormatInfo />
      </>
    )
  }
);
