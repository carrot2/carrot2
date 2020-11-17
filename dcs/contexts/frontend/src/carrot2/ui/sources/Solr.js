import React from 'react';


import { GenericSearchEngineErrorMessage } from "../../apps/search-app/ui/ErrorMessage.js";

import {
  createResultConfigStore,
  CustomSchemaResult,
  CustomSchemaResultConfig
} from "./CustomSchemaResult.js";
import { persistentStore } from "../../../carrotsearch/store/persistent-store.js";

const resultConfigStore = createResultConfigStore("localFile");

/*
const {
  schemaInfoStore,
  resultHolder
} = createSchemaExtractorStores("localFile");

*/
const solrServiceConfigStore = persistentStore("workbench:source:solr:serviceConfig", {
  serviceUrl: "http://localhost:8983/solr/",
  collection: undefined
});


const settings = [
  {
    id: "solr",
    type: "group",
    label: "Solr",
    description: "Queries Apache Solr.",
    settings: [
      {
        id: "solr:serviceUrl",
        type: "service-url",
        label: "Solr service URL",
        get: () => solrServiceConfigStore.serviceUrl,
        set: (sett, url) => solrServiceConfigStore.serviceUrl = url,
        pingUrl: url => `${url}/admin/cores?action=STATUS`,
        check: async response => {
          const json = await response.json();
          console.log(json);
          return true;
        }
      }
    ]
  }
];

const solrFileSource = () => {
  return {
    query: "",
    matches: 0,
    documents: []
  };
};

export const solrSourceDescriptor = {
  label: "Solr",
  descriptionHtml: "queries Apache Solr",
  source: solrFileSource,
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
  getFieldsToCluster: () => []
};

