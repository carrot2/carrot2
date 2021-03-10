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
  settings,
  afterSuccessfulSearch,
  createLocalSearchSource,
  isSearchPossible
} = createLocalSearch({
  id: "es",
  serviceName: "Elasticsearch",
  configOverrides: {
    serviceUrl: "http://localhost:9200/",
    extraHttpGetParams: ""
  },

  querySetting: id => ({
    id: `${id}:query`,
    ...storeAccessors(queryStore, "query"),
    type: "string",
    label: "Query",
    description: `
<p>
  The search query to pass to Elasticsearch. Use 
  <a target=_blank href="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html#query-string-syntax">Elasticsearch DSL query syntax</a>.
</p>`,
    visible: () => isSearchPossible()
  }),

  fetchCollections: async url => {
    const indices = await ky
      .get("_cat/indices?format=json", {
        prefixUrl: url,
        timeout: 4000
      })
      .json();

    return indices.map(e => e.index);
  },
  fetchResultsForSchemaInference: async () => searchCurrentCollection("*:*", 50)
});

const searchCurrentCollection = async (query, results = 50) => {
  const url = serviceConfigStore.serviceUrl;
  const collection = serviceConfigStore.collection;

  if (!collection) {
    return {
      documents: [],
      matches: 0,
      query: ""
    };
  }

  const result = await ky
    .get(`${collection}/_search`, {
      prefixUrl: url,
      timeout: 4000,
      searchParams: {
        source: JSON.stringify({
          query: {
            query_string: {
              query: query
            }
          },
          from: 0,
          size: results
        }),
        source_content_type: "application/json"
      }
    })
    .json();

  return {
    documents: result.hits.hits.map(h => h._source),
    matches: result.hits.total,
    query: query
  };
};

const esSettings = [
  {
    id: "es",
    type: "group",
    label: "Elasticsearch",
    description: "Queries Elasticsearch.",
    settings: [...settings]
  }
];

const esSource = async query => {
  return searchCurrentCollection(query, serviceConfigStore.maxResults).then(
    result => {
      afterSuccessfulSearch();
      return result;
    }
  );
};

export const ElasticsearchIntro = () => {
  return (
    <>
      <p>
        Provide the address of your Elasticsearch instance in the{" "}
        <strong>Elasticsearch service URL</strong> field and press{" "}
        <strong>Connect</strong>.
      </p>
      <p>
        Make sure Elasticsearch is configured with{" "}
        <a
          href="https://www.elastic.co/guide/en/elasticsearch/reference/current/modules-http.html"
          target="_blank"
          rel="noreferrer"
        >
          CORS headers
        </a>{" "}
        enabled, otherwise the connection will fail.
      </p>

      <p>
        Once Workbench connects to your Elasticsearch instance, it will fetch
        the list of available indices. Choose the index you'd like to query and
        type your query using{" "}
        <a
          target="_blank"
          rel="noreferrer"
          href="https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html#query-string-syntax"
        >
          Elasticsearch DSL query syntax
        </a>
        .
      </p>
    </>
  );
};

export const esSourceDescriptor = createLocalSearchSource({
  label: "Elasticsearch",
  descriptionHtml: "queries an Elasticsearch instance.",
  contentSummary: "Elasticsearch results",
  source: esSource,
  getSettings: () => esSettings,
  createError: async e => {
    // Parse the body of the error response. The JSON response contains an error message.
    let details;
    if (e.response) {
      let body;
      try {
        body = await e.response.json();
        details = <pre>{JSON.stringify(body, null, "  ")}</pre>;
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
  createIntroHelp: () => <ElasticsearchIntro />
});
