import { store, autoEffect } from "@risingstack/react-easy-state";
import { algorithms } from "../config-algorithms.js";
import { sources } from "../config-sources.js";
import { fetchClusters } from "../service/dcs.js";
import { persistentStore } from "../util/persistent-store.js";

import { errors } from "./errors.js";
import { createClusteringErrorElement } from "../apps/search-app/ui/ErrorMessage.js";
import { collectParameters } from "../service/algorithms/attributes.js";

const EMPTY_ARRAY = [];

export const algorithmStore = persistentStore("clusteringAlgorithm",{
  clusteringAlgorithm: undefined
});
if (!algorithms[algorithmStore.clusteringAlgorithm]) {
  algorithmStore.clusteringAlgorithm = Object.keys(algorithms)[0];
}

// Build a map of current algorithm parameters on every parameter or algorithm change.
// We cannot do this in the clusterStore.load method because the method is reactive
// and would re-run immediately on any parameter change.
let currentParams;
autoEffect(() => {
  const algorithm = algorithms[algorithmStore.clusteringAlgorithm];
  const settings = algorithm.getSettings();
  currentParams = collectParameters(settings, settings[0].get);
});

let currentAlgorithm;
autoEffect(() => {
  currentAlgorithm = algorithmStore.clusteringAlgorithm;
});

export const clusterStore = store({
  loading: false,
  clusters: EMPTY_ARRAY,
  documents: EMPTY_ARRAY,
  serviceInfo: null,
  load: async function (searchResult, algorithm) {
    const documents = searchResult.documents;
    const query = searchResult.query;

    if (documents.length === 0) {
      clusterStore.clusters = EMPTY_ARRAY;
      clusterStore.documents = EMPTY_ARRAY;
      clusterStore.loading = false;
    } else {
      // TODO: cancel currently running request
      clusterStore.loading = true;
      clusterStore.clusters = EMPTY_ARRAY;
      clusterStore.error = undefined;
      try {
        const fieldsToCluster = sources[searchResult.source].getFieldsToCluster();
        const response = await fetchClusters(query, documents, fieldsToCluster, algorithm, currentParams);
        clusterStore.clusters = response.clusters;
        clusterStore.serviceInfo = response.serviceInfo;
        clusterStore.documents = addClusterReferences(documents, clusterStore.clusters);
      } catch (e) {
        clusterStore.clusters = EMPTY_ARRAY;
        clusterStore.documents = EMPTY_ARRAY;
        try {
          e.bodyParsed = await e.json();
        } catch (ignored) { }
        errors.addError(createClusteringErrorElement(e));
      }
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
  },
  reload: () => {
    clusterStore.load(searchResultStore.searchResult, currentAlgorithm);
  },
  getClusteredDocsRatio: () => {
    const docSet = clusterStore.clusters.reduce(function collect(set, cluster) {
      if (cluster.unclustered) {
        return set;
      }
      cluster.documents.forEach(d => set.add(d));
      cluster.clusters.reduce(collect, set);
      return set;
    }, new Set());

    const docCount = clusterStore.documents.length;
    return docCount > 0 ? docSet.size / docCount : 0;
  }
});

export const searchResultStore = store({
  loading: false,
  error: false,
  searchResult: {
    query: "",
    matches: 0,
    documents: EMPTY_ARRAY
  },
  load: async function (source, query) {
    const sourceId = source || Object.keys(sources)[0];
    const src = sources[sourceId];

    // TODO: cancel currently running request
    searchResultStore.loading = true;
    searchResultStore.error = false;
    try {
      searchResultStore.searchResult = assignDocumentIds(await src.source(query), sourceId);
    } catch (e) {
      errors.addError(src.createError(e));
      searchResultStore.error = true;
      searchResultStore.searchResult = {
        query: query,
        matches: 0,
        documents: EMPTY_ARRAY
      }
    }
    searchResultStore.loading = false;
  }
});

function assignDocumentIds(result, sourceId) {
  return {
    ...result,
    "source": sourceId,
    "documents": (result.documents || []).map((doc, index) => ({
      ...doc,
      "id": index,
      "rank": 1.0 - index / result.documents.length
    }))
  };
}

// Invoke clustering once search results are available or algorithm changes.
autoEffect(() => {
  clusterStore.reload();
});

// When search result is loading, also show that clusters are loading.
autoEffect(() => {
  if (searchResultStore.loading) {
    clusterStore.loading = true;
    clusterStore.clusters = EMPTY_ARRAY;
  }
});
