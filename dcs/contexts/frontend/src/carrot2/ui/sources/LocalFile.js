import React from 'react';

import "./LocalFile.css";

import { autoEffect, store, view } from "@risingstack/react-easy-state";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faExclamationTriangle, faInfoSquare } from "@fortawesome/pro-regular-svg-icons";

import { GenericSearchEngineErrorMessage } from "../../apps/search-app/ui/ErrorMessage.js";
import { addFactory, Setting } from "../../../carrotsearch/ui/settings/Settings.js";
import { parseFile } from "./file-parser.js";
import { Checkbox } from "@blueprintjs/core";
import { Loading } from "../../../carrotsearch/ui/Loading.js";

import {
  createResultConfigStore,
  CustomSchemaResult,
  CustomSchemaResultConfig
} from "./CustomSchemaResult.js";
import { persistentLruStore } from "../../util/persistent-store.js";

const resultConfigStore = createResultConfigStore("localFile");

const ArrayLogger = function () {
  const entries = [];

  this.log = message => entries.push({ level: "info", message: message });
  this.warn = message => entries.push({ level: "warning", message: message });
  this.error = message => entries.push({ level: "error", message: message });
  this.getEntries = () => entries.slice(0);
};

// A non-reactive store for the contents of the last loaded file. This is not reactive, so that
// the results display component does not update right after a new file is selected, but when
// documents are requested from the source for clustering.
const fileContentsStore = {
  query: "",
  documents: [],
  fieldStats: [],
  fieldsAvailable: []
};

// A reactive store backing the local file loading user interface.
const fileInfoStore = store({
  loading: false,
  log: [],
  query: "",
  fieldsAvailableForClustering: [],
  fieldsToCluster: [],
  load: async file => {
    fileInfoStore.loading = true;
    const logger = new ArrayLogger();
    try {
      const parsed = await parseFile(file, logger);
      fileInfoStore.fieldsAvailableForClustering = parsed.fieldsAvailableForClustering;

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

      fileInfoStore.fieldsToCluster = newToCluster;
      fileInfoStore.query = parsed.query;

      fileContentsStore.documents = parsed.documents;
      fileContentsStore.fieldStats = parsed.fieldStats;
      fileContentsStore.fieldsAvailable = parsed.fieldsAvailable;
    } finally {
      fileInfoStore.log = logger.getEntries();
      fileInfoStore.loading = false;
    }
  }
});

// Store the last selected set of fields to cluster for each schema
const fieldsToClusterConfigsKey = item => item.join("--");
const fieldsToClusterConfigs = persistentLruStore(
    "workbench:source:localFile:lastConfigs",
    () => {
      const fieldsAvailable = fileContentsStore.fieldsAvailable;
      return fieldsAvailable.length > 0 ? fieldsToClusterConfigsKey(fieldsAvailable) : null;
    },
    () => {
      return Array.from(fileInfoStore.fieldsToCluster);
    }
);

const FieldList = view(() => {
  const store = fileInfoStore;
  const toCluster = store.fieldsToCluster;
  return (
      <div className="FieldList">
        {
          store.fieldsAvailableForClustering.map(f => {
            return <Checkbox label={f} key={f} checked={toCluster.has(f)}
                             onChange={e => {
                               e.target.checked ? toCluster.add(f) : toCluster.delete(f);
                             }} />;
          })
        }
      </div>
  );
});

const LEVEL_ICONS = {
  "error": faExclamationTriangle,
  "warning": faExclamationTriangle,
  "info": faInfoSquare
}
export const LogEntry = ({ entry }) => {
  const { level, message } = entry;
  return (
      <div className={`LogEntry LogEntry-${level}`}>
        <FontAwesomeIcon icon={LEVEL_ICONS[level]} /> {message}
      </div>
  );
};

const FieldChoiceSetting = view(({ setting, get, set }) => {
  const { label, description } = setting;

  const children = fileInfoStore.loading ?
      <Loading store={fileInfoStore} />
      :
      <FieldList />;

  return (
      <Setting className="FieldChoiceSetting" label={label} description={description}>
        {children}
        {
          fileInfoStore.log.map((e, i) => <LogEntry entry={e} key={i} />)
        }
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
          fileInfoStore.load(file);
        }
      },
      {
        id: "file:fieldChoice",
        type: "field-choice",
        label: "Fields to cluster",
        get: () => fileInfoStore.fieldsToCluster,
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
  resultConfigStore.load(fileContentsStore.fieldStats);
  return {
    query: fileInfoStore.query,
    matches: fileContentsStore.documents.length,
    documents: fileContentsStore.documents
  };
};

// Create a local copy of fields to cluster. The cluster store calls the getFieldsToCluster() method
// before clustering and if the method returned a value from the fileInfoStore reactive store,
// clustering would be triggered right after the selection of fields changed, which we want to avoid.
let currentFieldsToCluster;
autoEffect(() => {
  currentFieldsToCluster = Array.from(fileInfoStore.fieldsToCluster);
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
      <CustomSchemaResult configStore={resultConfigStore}
                          previewResultProvider={() => fileContentsStore.documents[0]} />
  ),
  createSourceConfig: (props) => {
    throw new Error("Not available in search app.");
  },
  getSettings: () => settings,
  getFieldsToCluster: () => currentFieldsToCluster
};

