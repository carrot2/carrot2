import ky from "ky";

import { storeAccessors } from "@carrotsearch/ui/settings/Setting.js";

import {
  HttpErrorMessage,
  SearchEngineErrorMessage
} from "../../apps/search-app/ui/ErrorMessage.js";
import React from "react";
import { createLocalSearch } from "./LocalSearchServerSource.js";
import { queryStore } from "../../apps/workbench/store/query-store.js";

const {
  serviceConfigStore,
  schemaInfoStore,
  isSearchPossible,
  settings,
  afterSuccessfulSearch,
  createLocalSearchSource
} = createLocalSearch({
  id: "solr",
  serviceName: "Solr",
  configOverrides: {
    serviceUrl: "http://localhost:8983/solr",
    extraHttpGetParams: "",
    useHighlightedResults: false
  },

  querySetting: id => ({
    id: `${id}:query`,
    ...storeAccessors(queryStore, "query"),
    type: "string",
    label: "Query",
    description: `
<p>
  The search query to pass to Solr. Use 
  <a target="_blank" href="https://lucene.apache.org/solr/guide/8_6/the-standard-query-parser.html#specifying-terms-for-the-standard-query-parser">Solr query syntax</a>.
</p>`,
    visible: () => isSearchPossible()
  }),

  fetchCollections: async url => {
    const cores = await ky
      .get("admin/cores?action=STATUS", {
        prefixUrl: url,
        timeout: 4000
      })
      .json();

    return Object.keys(cores.status);
  },
  fetchResultsForSchemaInference: async () =>
    searchCurrentCore("*:*", 50, {}, false)
});

const searchCurrentCore = async (
  query,
  results = 50,
  extraParams,
  allowHighlighting
) => {
  const url = serviceConfigStore.serviceUrl;
  const core = serviceConfigStore.collection;

  if (!core) {
    return {
      documents: [],
      matches: 0,
      query: ""
    };
  }

  const hlParams = {},
    hlForcedParams = {};
  const useHighlighting =
    allowHighlighting && serviceConfigStore.useHighlightedResults;
  if (useHighlighting) {
    hlParams["hl"] = true;
    hlParams["hl.fl"] = Array.from(schemaInfoStore.fieldsToCluster).join(",");
    hlForcedParams["hl.tag.pre"] = "<b class='hl'>";
    hlForcedParams["hl.simple.pre"] = hlForcedParams["hl.tag.pre"];
    hlForcedParams["hl.tag.post"] = "</b>";
    hlForcedParams["hl.simple.post"] = hlForcedParams["hl.tag.post"];
  }

  const result = await ky
    .get(`${core}/select`, {
      prefixUrl: url,
      timeout: 4000,
      searchParams: Object.assign(
        {},
        hlParams,
        extraParams,
        {
          q: query,
          rows: results
        },
        hlForcedParams
      )
    })
    .json();

  // If the user requests highlighting, overwrite the complete field value
  // with the highlighted version. A further optimization would be to
  // skip the download of the complete field value.
  if (useHighlighting) {
    result.response.docs.forEach(doc => {
      const hl = result.highlighting[doc.id];
      if (hl) {
        Object.keys(hl).forEach(h => {
          doc[h] = hl[h];
        });
      }
    });
  }

  return {
    documents: result.response.docs,
    matches: result.response.numFound,
    query: query
  };
};

const extraGetParametersLabel = "Additional search request parameters";
const solrSettings = [
  {
    id: "solr",
    type: "group",
    label: "Solr",
    description: "Queries Apache Solr.",
    settings: [
      ...settings,
      {
        id: "solr:useHighlightedResults",
        type: "boolean",
        label: "Use highlights for clustering",
        description: `
<p>
  If enabled, Workbench will request Solr to highlight query occurrences
  in search results and use the contextual snippets for clustering. 
</p>
<p>
  Use the <strong>${extraGetParametersLabel}</strong> parameter to
  <a href="https://solr.apache.org/guide/8_8/highlighting.html" target="_blank">configure</a>
  the details of highlighting.
</p>`,
        visible: () => isSearchPossible(),
        ...storeAccessors(serviceConfigStore, "useHighlightedResults")
      },
      {
        id: "solr:extraParameters",
        type: "string",
        advanced: true,
        label: extraGetParametersLabel,
        description: `
<p>
  The extra HTTP GET parameters to pass to the <code>/select</code> endpoint, for example: 
</p>
<pre>defType=dismax&fq=category:important</pre>`,
        visible: () => isSearchPossible(),
        ...storeAccessors(serviceConfigStore, "extraHttpGetParams")
      }
    ]
  }
];

const solrSource = async query => {
  const params = {};
  new URLSearchParams(serviceConfigStore.extraHttpGetParams).forEach(
    (val, key) => {
      params[key] = val;
    }
  );

  return searchCurrentCore(
    query,
    serviceConfigStore.maxResults,
    params,
    true
  ).then(result => {
    afterSuccessfulSearch();
    return result;
  });
};

export const SolrIntro = () => {
  return (
    <>
      <p>
        Provide the address of your Solr instance in the{" "}
        <strong>Solr service URL</strong> field and press{" "}
        <strong>Connect</strong>. Make sure the instance emits correct CORS
        headers, otherwise the connection will fail.
      </p>

      <p>
        Once Workbench connects to your Solr instance, it will fetch the list of
        available Solr cores. Choose the core you'd like to query and type your
        query using{" "}
        <a
          target="_blank"
          rel="noreferrer"
          href="https://lucene.apache.org/solr/guide/8_6/the-standard-query-parser.html#specifying-terms-for-the-standard-query-parser"
        >
          Solr query syntax
        </a>
        .
      </p>
    </>
  );
};

export const solrSourceDescriptor = createLocalSearchSource({
  label: "Solr",
  descriptionHtml: "queries an Apache Solr instance.",
  contentSummary: "Apache Solr search results",
  source: solrSource,
  getSettings: () => solrSettings,
  createError: async e => {
    // Parse the body of the error response. The JSON response contains an error message.
    let details;
    if (e.response) {
      let body;
      try {
        body = await e.response.json();
        details = <pre>{body.error.msg}</pre>;
      } catch (ignored) {
        body = await e.response.text();
        details = <pre>{body}</pre>;
      }
    }
    return (
      <SearchEngineErrorMessage>
        <HttpErrorMessage error={e}>{details}</HttpErrorMessage>
      </SearchEngineErrorMessage>
    );
  },
  createIntroHelp: () => <SolrIntro />
});
