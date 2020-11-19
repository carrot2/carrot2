import ky from "ky";

import { autoEffect } from "@risingstack/react-easy-state";

import { createResultConfigStore, } from "./CustomSchemaResult.js";
import { persistentStore } from "../../../carrotsearch/store/persistent-store.js";
import { createStateStore } from "../../../carrotsearch/ui/settings/ServiceUrlSetting.js";
import {
  createFieldChoiceSetting,
  createSchemaExtractorStores,
  createSource
} from "./CustomSchemaSource.js";
import { storeAccessors } from "../../../carrotsearch/ui/settings/Setting.js";

import { queryStore } from "../../apps/workbench/store/query-store.js";
import { workbenchSourceStore } from "../../apps/workbench/store/source-store.js";

const resultConfigStore = createResultConfigStore("solr");

const {
  schemaInfoStore,
  resultHolder
} = createSchemaExtractorStores("solr");

const solrServiceConfigStore = persistentStore("workbench:source:solr:serviceConfig", {
  serviceUrl: "http://localhost:8983/solr",
  core: undefined,
  maxResults: 100,
  extraHttpGetParams: ""
});

const solrServiceStateStore = createStateStore({
  isUrlValid: () => solrServiceStateStore.status === "ok",
  checkServiceUrl: async (url) => {
    solrServiceStateStore.status = "loading";
    solrServiceStateStore.message = "";
    try {
      const cores = await ky.get("admin/cores?action=STATUS", {
        prefixUrl: url,
        timeout: 4000
      }).json();

      solrServiceConfigStore.serviceUrl = url;
      solrServiceStateStore.status = "ok";

      const coreIds = Object.keys(cores.status);
      solrServiceStateStore.cores = coreIds;
      if (coreIds.indexOf(solrServiceConfigStore.core) < 0) {
        solrServiceConfigStore.core = coreIds[0];
      }
    } catch (e) {
      solrServiceStateStore.status = "error";
      solrServiceStateStore.message = e instanceof Error ? e.toString() : e;
    }
  },
  cores: []
});

const isSearchPossible = () =>
    solrServiceStateStore.isUrlValid() &&
    !!solrServiceConfigStore.core;

// Check Solr service URL when the page loads.
autoEffect(() => {
  if (workbenchSourceStore.source === "solr") {
    solrServiceStateStore.checkServiceUrl(solrServiceConfigStore.serviceUrl);
  }
});
autoEffect(() => {
  if (workbenchSourceStore.source === "solr" && isSearchPossible()) {
    schemaInfoStore.load(async () => {
      const result = await searchCurrentCore("*:*", 50);
      resultHolder.documents = result.documents;
      return result;
    });
  }
});

const searchCurrentCore = async (query, results = 50, extraParams) => {
  const url = solrServiceConfigStore.serviceUrl;
  const core = solrServiceConfigStore.core;

  if (!core) {
    return {
      documents: [],
      matches: 0,
      query: ""
    }
  }

  const result = await ky.get(`${core}/select`, {
    prefixUrl: url,
    timeout: 4000,
    searchParams: Object.assign({}, extraParams,{
      q: query,
      rows: results
    })
  }).json();

  return {
    documents: result.response.docs,
    matches: result.response.numFound,
    query: query
  };
};

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
        urlStore: solrServiceStateStore,
        get: () => solrServiceConfigStore.serviceUrl,
        stateStore: solrServiceStateStore,
        checkUrl: solrServiceStateStore.checkServiceUrl
      },

      {
        id: "solr:core",
        type: "enum",
        ui: "select",
        label: "Solr collection to search",
        noOptionsMessage: "Collection list is empty, no content to search.",
        options: () => solrServiceStateStore.cores.map(c => ({ value: c })),
        visible: () => solrServiceStateStore.isUrlValid(),
        get: () => solrServiceConfigStore.core,
        set: (sett, core) => solrServiceConfigStore.core = core
      },

      createFieldChoiceSetting("solr", schemaInfoStore, {
        visible: () => isSearchPossible()
      }),

      {
        id: "solr:query",
        ...storeAccessors(queryStore, "query"),
        type: "string",
        label: "Query",
        description: `
<p>
  The search query to pass to Solr. Use 
  <a target=_blank href="https://lucene.apache.org/solr/guide/8_6/the-standard-query-parser.html#specifying-terms-for-the-standard-query-parser">Solr query syntax</a>.
</p>`,
        visible: () => isSearchPossible()
      },

      {
        id: "solr:maxResults",
        type: "number",
        label: "Max results",
        min: 0,
        max: 1000,
        step: 10,
        description: `<p>The number of search results to fetch.</p>`,
        visible: () => isSearchPossible(),
        ...storeAccessors(solrServiceConfigStore, "maxResults")
      },

      {
        id: "solr:extraParameters",
        type: "string",
        label: "Additional search request parameters",
        description: `
<p>
  The extra HTTP GET parameters to pass to the <code>/select</code> endpoint, for example: 
</p>
<pre>defType=dismax&fq=category:important</pre>`,
        visible: () => isSearchPossible(),
        ...storeAccessors(solrServiceConfigStore, "extraHttpGetParams")
      }
    ]
  }
];

const solrFileSource = async (query) => {
  resultConfigStore.load(schemaInfoStore.fieldStats, resultHolder);

  const params = {};
  new URLSearchParams(solrServiceConfigStore.extraHttpGetParams)
      .forEach((val, key) => {
        params[key] = val;
      });

  return searchCurrentCore(query, solrServiceConfigStore.maxResults, params);
};

export const solrSourceDescriptor = createSource(schemaInfoStore, resultConfigStore, {
  label: "Solr",
  descriptionHtml: "queries Apache Solr",
  source: solrFileSource,
  getSettings: () => settings,
});

