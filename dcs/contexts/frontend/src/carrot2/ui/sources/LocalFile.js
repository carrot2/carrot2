import React from 'react';

import "./LocalFile.css";

import { store, view } from "@risingstack/react-easy-state";

import { GenericSearchEngineErrorMessage } from "../../apps/search-app/ui/ErrorMessage.js";
import { addFactory, Setting } from "../../../carrotsearch/ui/settings/Settings.js";
import { parseFile } from "./file-parser.js";
import { Checkbox } from "@blueprintjs/core";
import { Loading } from "../../../carrotsearch/ui/Loading.js";

const LocalFileResult = ({ document }) => {
  return (
      <>
        <strong>{document.title}</strong>
        <div>{document.snippet}</div>
      </>
  );
};

const LocalFileResultConfig = () => {
  return (
      <div>
        Result config
      </div>
  );
};

const fileContentsStore = store({
  loading: false,
  query: "",
  documents: [],
  fieldsAvailable: [],
  fieldsAvailableForClustering: [],
  fieldsToCluster: [],
  load: async file => {
    fileContentsStore.loading = true;
    try {
      const parsed = await parseFile(file, console);
      fileContentsStore.fieldsAvailableForClustering = parsed.fieldsAvailableForClustering;
      fileContentsStore.fieldsToCluster = new Set(parsed.fieldsToCluster);
      fileContentsStore.documents = parsed.documents;
      fileContentsStore.query = parsed.query;
    } finally {
      fileContentsStore.loading = false;
    }
  }
});

const FieldList = view(() => {
  const store = fileContentsStore;
  const toCluster = store.fieldsToCluster;
  return (
      <div>
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

const FieldChoiceSetting = view(({ setting, get, set }) => {
  const { label, description } = setting;

  const children = fileContentsStore.loading ?
      <Loading store={fileContentsStore} />
      :
      <FieldList />;

  return (
      <Setting className="FieldChoiceSetting" label={label} description={description}>
        {children}
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
    description: "Loads documents from a local file. Carrot2 XML, JSON, JSON records, CSV and Excel formats are supported.",
    settings: [
      {
        id: "file:file",
        type: "file",
        label: "File",
        get: () => null,
        set: (sett, file) => {
          fileContentsStore.load(file);
        }
      },
      {
        id: "file:fieldChoice",
        type: "field-choice",
        label: "Fields to cluster",
        get: () => fileContentsStore.fieldsToCluster,
        set: () => {
        }
      }
    ]
  }
];

const localFileSource = () => {
  return {
    query: fileContentsStore.query,
    matches: fileContentsStore.documents.length,
    documents: fileContentsStore.documents
  };
};

export const localFileSourceDescriptor = {
  label: "Local file",
  descriptionHtml: "content read from a local file",
  source: localFileSource,
  createResult: (props) => {
    return <LocalFileResult {...props} />;
  },
  createError: (error) => {
    return <GenericSearchEngineErrorMessage />
  },
  createConfig: () => {
    return <LocalFileResultConfig />;
  },
  createSourceConfig: (props) => {
    throw new Error("Not available in search app.");
  },
  getSettings: () => settings,
  getFieldsToCluster: () => [ "title", "snippet" ]
};

