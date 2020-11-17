import React from 'react';

import "./LocalFile.css";

import { autoEffect, store, view } from "@risingstack/react-easy-state";

import { GenericSearchEngineErrorMessage } from "../../apps/search-app/ui/ErrorMessage.js";
import { addFactory } from "../../../carrotsearch/ui/settings/Settings.js";
import { parseFile } from "./file-parser.js";
import { Loading } from "../../../carrotsearch/ui/Loading.js";

import {
  createResultConfigStore,
  CustomSchemaResult,
  CustomSchemaResultConfig
} from "./CustomSchemaResult.js";
import { persistentLruStore } from "../../../carrotsearch/store/persistent-store.js";
import { Setting } from "../../../carrotsearch/ui/settings/Setting.js";
import { FieldList } from "./CustomSchemaSource.js";
import { ArrayLogger, LogEntries } from "../../../carrotsearch/ui/LogEntries.js";

const resultConfigStore = createResultConfigStore("localFile");

// A non-reactive holder for the contents of the last loaded file. This is not reactive, so that
// the results display component does not update right after a new file is selected, but when
// documents are requested from the source for clustering.
const fileContentsHolder = {
  query: "",
  documents: [],
  fieldStats: [],
  fieldsAvailable: []
};

// A reactive store backing the local file loading user interface.
const schemaInfoStore = store({
  loading: false,
  log: [],
  fileLoaded: false,
  fieldsAvailableForClustering: [],
  fieldsToCluster: [],
  load: async file => {
    schemaInfoStore.loading = true;
    const logger = new ArrayLogger();
    try {
      const parsed = await parseFile(file, logger);
      schemaInfoStore.fieldsAvailableForClustering = parsed.fieldsAvailableForClustering;

      // We remember the fields the user selected for clustering on a per-schema (set of all fields)
      // basis, so that the user doesn't have to re-select the right fields every time they upload
      // a similar data set.
      const cachedToCluster = fieldsToClusterConfigs.get(fieldsToClusterConfigsKey(parsed.fieldsAvailable));
      let newToCluster;
      if (cachedToCluster && cachedToCluster.length > 0) {
        const cached = new Set(cachedToCluster);

        // Intersection of parsed and cached set of fields, in case this specific instance
        // had data that caused some field to be unsuitable for clustering.
        newToCluster = new Set([ ...parsed.fieldsToCluster ].filter(f => cached.has(f)))
      } else {
        newToCluster = new Set(parsed.fieldsToCluster);
      }

      schemaInfoStore.fieldsToCluster = newToCluster;
      schemaInfoStore.fileLoaded = true;

      fileContentsHolder.documents = parsed.documents;
      fileContentsHolder.fieldStats = parsed.fieldStats;
      fileContentsHolder.fieldsAvailable = parsed.fieldsAvailable;

    } finally {
      schemaInfoStore.log = logger.getEntries();
      schemaInfoStore.loading = false;
    }
  }
});

// Store the last selected set of fields to cluster for each schema
const fieldsToClusterConfigsKey = item => item.join("--");
const fieldsToClusterConfigs = persistentLruStore(
    "workbench:source:localFile:lastConfigs",
    () => {
      const fieldsAvailable = fileContentsHolder.fieldsAvailable;
      return fieldsAvailable.length > 0 ? fieldsToClusterConfigsKey(fieldsAvailable) : null;
    },
    () => {
      return Array.from(schemaInfoStore.fieldsToCluster);
    }
);

const FieldChoiceSetting = view(({ setting, get, set }) => {
  const { label, description } = setting;

  const children = schemaInfoStore.loading ?
      <Loading store={schemaInfoStore} />
      :
      <FieldList schemaInfoStore={schemaInfoStore} />;

  return (
      <Setting className="FieldChoiceSetting" label={label} description={description}>
        {children}
        <LogEntries entries={schemaInfoStore.log} />
      </Setting>
  );
});
addFactory("field-choice", (s, get, set) => {
  return <FieldChoiceSetting setting={s} get={get} set={set} />;
});

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
          schemaInfoStore.load(file);
        }
      },
      {
        id: "file:fieldChoice",
        type: "field-choice",
        label: "Fields to cluster",
        visible: () => schemaInfoStore.fileLoaded,
        get: () => schemaInfoStore.fieldsToCluster,
        set: () => {
        }
      }
    ]
  }
];

const localFileSource = () => {
  // Load the field display configuration only when the documents are requested
  // for clustering. If we did that right after the file was loaded, we'd swap
  // the configuration for the set of documents being currently displayed
  // (possibly with a different schema).
  resultConfigStore.load(fileContentsHolder.fieldStats);
  return {
    query: schemaInfoStore.query,
    matches: fileContentsHolder.documents.length,
    documents: fileContentsHolder.documents
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
      <CustomSchemaResultConfig configStore={resultConfigStore}
                                previewResultProvider={() => fileContentsHolder.documents[0]} />
  ),
  createSourceConfig: (props) => {
    throw new Error("Not available in search app.");
  },
  getSettings: () => settings,
  getFieldsToCluster: () => currentFieldsToCluster
};

