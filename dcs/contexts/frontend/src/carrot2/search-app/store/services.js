import { observe } from '@nx-js/observer-util';
import { store } from 'react-easy-state';
import { fetchClusters } from "../../service/dcs";

import { etools } from "../../service/sources/etools";

export const clusterStore = store({
  loading: false,
  clusters: [],
  error: false,
  load: async function (searchResult) {
    const documents = searchResult.documents;
    const query = searchResult.query;

    if (query.length === 0 || documents.length === 0) {
      clusterStore.clusters = [];
      clusterStore.loading = false;
    } else {
      // TODO: cancel currently running request
      clusterStore.loading = true;
      clusterStore.clusters = await fetchClusters(query, documents);
      clusterStore.loading = false;
    }
  }
});

export const searchResultStore = store({
  loading: false,
  searchResult: {
    query: "",
    matches: 0,
    documents: []
  },
  error: false,
  load: async function (source, query) {
    // TODO: cancel currently running request
    searchResultStore.loading = true;
    searchResultStore.searchResult = assignDocumentIds(await etools(query, {}));
    searchResultStore.loading = false;
  }
});

function assignDocumentIds(result) {
  return {
    ...result,
    "documents": (result.documents || []).map((doc, index) => ({
      ...doc,
      "id": index
    }))
  };
}

// Invoke clustering once search results are available.
observe(function () {
  clusterStore.load(searchResultStore.searchResult);
});

// When search result is loading, also show that clusters are loading.
observe(function () {
  if (searchResultStore.loading) {
    clusterStore.loading = true;
  }
});
