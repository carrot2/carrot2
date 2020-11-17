import React from 'react';

import "./LocalFile.css";

import { autoEffect } from "@risingstack/react-easy-state";

import { GenericSearchEngineErrorMessage } from "../../apps/search-app/ui/ErrorMessage.js";

import {
  createResultConfigStore,
  CustomSchemaResult,
  CustomSchemaResultConfig
} from "./CustomSchemaResult.js";
import { createFieldChoiceSetting, createSchemaExtractorStores } from "./CustomSchemaSource.js";
import { parseFile } from "./file-parser.js";

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

// Create a local copy of fields to cluster. The cluster store calls the getFieldsToCluster() method
// before clustering and if the method returned a value from the schemaInfoStore reactive store,
// clustering would be triggered right after the selection of fields changed, which we want to avoid.
let currentFieldsToCluster;
autoEffect(() => {
  currentFieldsToCluster = Array.from(schemaInfoStore.fieldsToCluster);
});

export const localFileSourceDescriptor = {
  label: "Local file",
  descriptionHtml: "content read from a local file",
  source: localFileSource,
  createResult: (props) => {
    return <CustomSchemaResult {...props} configStore={resultConfigStore} />;
  },
  createError: () => {
    return <GenericSearchEngineErrorMessage />
  },
  createConfig: () => (
      <CustomSchemaResultConfig configStore={resultConfigStore} />
  ),
  createSourceConfig: (props) => {
    throw new Error("Not available in search app.");
  },
  getSettings: () => settings,
  getFieldsToCluster: () => currentFieldsToCluster
};

