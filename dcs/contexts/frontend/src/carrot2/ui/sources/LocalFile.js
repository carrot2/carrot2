import { createResultConfigStore, } from "./CustomSchemaResult.js";
import {
  createFieldChoiceSetting,
  createSchemaExtractorStores,
  createSource
} from "./CustomSchemaSource.js";
import { parseFile } from "./file-parser.js";
import { GenericSearchEngineErrorMessage } from "../../apps/search-app/ui/ErrorMessage.js";
import React from "react";

const resultConfigStore = createResultConfigStore("localFile");

const {
  schemaInfoStore,
  resultHolder
} = createSchemaExtractorStores("localFile");

const settings = [
  {
    id: "file",
    type: "group",
    label: "Local file",
    description: "Loads documents from a local file. Carrot2 XML, JSON, CSV and Excel formats are supported.",
    settings: [
      {
        id: "file:file",
        type: "file",
        label: "File",
        get: () => null,
        set: (sett, file) => {
          schemaInfoStore.load(async (logger) => {
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

export const localFileSourceDescriptor = createSource(schemaInfoStore, resultConfigStore, {
  label: "Local file",
  descriptionHtml: "content read from a local file",
  source: localFileSource,
  getSettings: () => settings,
  createError: (e) => <GenericSearchEngineErrorMessage error={e} />,
});

