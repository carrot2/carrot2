import { observe } from '@nx-js/observer-util';
import { store } from 'react-easy-state';
import { fetchClusters } from "../../service/dcs";

import { etools } from "../../service/sources/etools";

const EMPTY_ARRAY = [];

export const clusterStore = store({
  loading: false,
  clusters: EMPTY_ARRAY,
  documents: EMPTY_ARRAY,
  error: false,
  load: async function (searchResult) {
    const documents = searchResult.documents;
    const query = searchResult.query;

    if (query.length === 0 || documents.length === 0) {
      clusterStore.clusters = EMPTY_ARRAY;
      clusterStore.documents = EMPTY_ARRAY;
      clusterStore.loading = false;
    } else {
      // TODO: cancel currently running request
      clusterStore.loading = true;
      clusterStore.clusters = EMPTY_ARRAY;
      clusterStore.documents = documents;
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
      "id": index,
      "rank": 1.0 - index / result.documents.length
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
    clusterStore.clusters = EMPTY_ARRAY;
  }
});
