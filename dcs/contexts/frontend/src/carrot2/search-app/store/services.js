import { observe } from '@nx-js/observer-util';
import { store } from 'react-easy-state';
import { sources } from "../../config-sources.js";
import { fetchClusters } from "../../service/dcs";

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
      clusterStore.clusters = await fetchClusters(query, documents);
      clusterStore.documents = addClusterReferences(documents, clusterStore.clusters);
      clusterStore.loading = false;
    }

    // For each document, adds references to clusters to which the document below.
    function addClusterReferences(documents, clusters) {
      const docToClusters = clusters.reduce(function process(map, cluster) {
        for (let doc of cluster.uniqueDocuments) {
          addToMap(map, doc, cluster);
        }
        return (cluster.clusters || EMPTY_ARRAY).reduce(process, map);

        function addToMap(map, doc, cluster) {
          if (map.has(doc)) {
            map.get(doc).push(cluster);
          } else {
            map.set(doc, [ cluster ]);
          }
        }
      }, new Map());

      // Modify the existing documents, these are proxies and components
      // that reference those documents will render the clusters automatically.
      for (let doc of documents) {
        doc.clusters = docToClusters.get(doc.id);
      }
      return documents;
    }
  }
});

export const searchResultStore = store({
  loading: false,
  source: undefined,
  searchResult: {
    query: "",
    matches: 0,
    documents: EMPTY_ARRAY
  },
  error: undefined,
  load: async function (source, query) {
    const src = sources[source] || sources.etools;

    // TODO: cancel currently running request
    searchResultStore.loading = true;
    searchResultStore.error = undefined;
    searchResultStore.source = source;
    try {
      searchResultStore.searchResult = assignDocumentIds(await src.source(query));
    } catch (e) {
      searchResultStore.error = e;
      searchResultStore.searchResult = {
        query: query,
        matches: 0,
        documents: EMPTY_ARRAY
      }
    }
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
