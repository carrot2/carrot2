import { createResultConfigStore } from "./CustomSchemaResult.js";
import {
  createFieldChoiceSetting,
  createSchemaExtractorStores,
  createSource
} from "./CustomSchemaSource.js";
import { parseFile } from "./file-parser.js";
import { GenericSearchEngineErrorMessage } from "../../apps/search-app/ui/ErrorMessage.js";
import React from "react";

const resultConfigStore = createResultConfigStore("localFile");

const { schemaInfoStore, resultHolder } = createSchemaExtractorStores(
  "localFile"
);

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

const LocalFileFormatInfo = () => {
  return (
    <>
      <p>
        Data for clustering is extracted from the file you provide. The
        following file types are supported:
      </p>
      <ul>
        <li>
          <p>
            <strong>Excel, OpenOffice, CSV</strong> &mdash; one document per
            row, the first row is treated as a header with document field names.
          </p>
        </li>
        <li>
          <p>
            <strong>JSON</strong> (<SampleDownload file={"serverfault.json"} />)
            &mdash; an array of flat JSON objects representing documents to
            cluster:
          </p>
          <pre>
            {"[\n" +
              exampleDocs.map(d => "  " + JSON.stringify(d)).join(",\n") +
              ",\n  ...\n]"}
          </pre>
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

export const localFileSourceDescriptor = createSource(
  schemaInfoStore,
  resultConfigStore,
  {
    label: "Local file",
    descriptionHtml: "content read from a local file",
    source: localFileSource,
    getSettings: () => settings,
    createError: e => <GenericSearchEngineErrorMessage error={e} />,
    createIntroHelp: () => <LocalFileFormatInfo />
  }
);
