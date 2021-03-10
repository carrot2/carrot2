import { autoEffect, store } from "@risingstack/react-easy-state";

import { defer } from "@carrotsearch/ui/lang/lang.js";
import { equals } from "@carrotsearch/ui/lang/arrays.js";
import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";
import { errors } from "@carrotsearch/ui/store/errors.js";

import { algorithms } from "@carrot2/config/algorithms.js";

import { fetchClusters } from "../service/dcs.js";
import { createClusteringErrorElement } from "../apps/search-app/ui/ErrorMessage.js";
import { collectParameters } from "../service/algorithms/attributes.js";
import { triggerClusteringRequested } from "../customizations.js";

const EMPTY_ARRAY = [];

export const algorithmStore = persistentStore("clusteringAlgorithm", {
  clusteringAlgorithm: undefined,
  getAlgorithmInstance: () => algorithms[algorithmStore.clusteringAlgorithm]
});
if (!algorithms[algorithmStore.clusteringAlgorithm]) {
  algorithmStore.clusteringAlgorithm = Object.keys(algorithms)[0];
}

export const clusterStore = store({
  loading: false,
  clusters: EMPTY_ARRAY,
  documents: EMPTY_ARRAY,
  serviceInfo: null,
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

export const buildRequestJson = (onlyNonDefault = false, query) => {
  const algorithm = algorithmStore.getAlgorithmInstance();
  const settings = algorithm.getSettings();
  const defaults = algorithm.getDefaults();
  const currentParams = collectParameters(
    settings,
    settings[0].get,
    onlyNonDefault
      ? (setting, value) => {
          const def = defaults[setting.id];
          return !(Array.isArray(value) ? equals(value, def) : value === def);
        }
      : null
  );
  if (query && query.trim().length > 0) {
    algorithm.applyQueryHint(currentParams, query);
  }
  const currentLanguage = algorithm.getLanguage();

  return {
    algorithm: algorithmStore.clusteringAlgorithm,
    language: currentLanguage,
    parameters: currentParams
  };
};

const loadClusters = async function (searchResult) {
  const documents = searchResult.documents;
  const query = searchResult.query;

  if (documents.length === 0) {
    clusterStore.clusters = EMPTY_ARRAY;
    clusterStore.documents = EMPTY_ARRAY;
    clusterStore.loading = false;
  } else {
    // TODO: cancel currently running request
    clusterStore.loading = true;
    clusterStore.error = undefined;

    triggerClusteringRequested(searchResult.sourceId, documents.length);

    try {
      const fieldsToCluster = searchResult.source.getFieldsToCluster();

      const requestJson = buildRequestJson(false, query);

      const response = await fetchClusters(
        requestJson,
        documents,
        fieldsToCluster
      );
      clusterStore.clusters = response.clusters;
      clusterStore.serviceInfo = response.serviceInfo;
      clusterStore.documents = addClusterReferences(
        documents,
        clusterStore.clusters
      );
    } catch (e) {
      clusterStore.clusters = EMPTY_ARRAY;
      clusterStore.documents = EMPTY_ARRAY;
      try {
        e.bodyParsed = await e.json();
      } catch (ignored) {}
      errors.addError(createClusteringErrorElement(e));
    }
    clusterStore.loading = false;
  }

  // For each document, adds references to clusters to which the document belongs.
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
          map.set(doc, [cluster]);
        }
      }
    }, new Map());

    // Modify the existing documents, these are proxies and components
    // that reference those documents will render the clusters automatically.
    for (let doc of documents) {
      doc.clusters = docToClusters.get(doc.__id);
    }
    return documents;
  }
};

export const reloadClusters = async () => {
  const searchResult = searchResultStore.searchResult;

  // Call clustering in a clear stack frame to avoid reactivity.
  // Clustering reads from source, algorithm and parameter stores,
  // and if we called it directly, clustering would be called on
  // every change in the stores read during clustering.
  defer(async () => {
    return loadClusters(searchResult);
  });
};

export const searchResultStore = store({
  loading: false,
  error: false,
  initial: true,
  searchResult: {
    query: "",
    matches: 0,
    documents: EMPTY_ARRAY
  },
  load: async function (sourceId, source, query) {
    // TODO: cancel currently running request
    searchResultStore.loading = true;
    searchResultStore.error = false;
    try {
      searchResultStore.initial = false;
      searchResultStore.searchResult = assignDocumentIds(
        await source.source(query),
        sourceId,
        source
      );
    } catch (e) {
      errors.addError(await source.createError(e));
      searchResultStore.error = true;
      searchResultStore.searchResult = {
        query: query,
        matches: 0,
        documents: EMPTY_ARRAY
      };
    }
    searchResultStore.loading = false;
  }
});

function assignDocumentIds(result, sourceId, source) {
  return {
    ...result,
    sourceId: sourceId,
    source: source,
    documents: (result.documents || []).map((doc, index) => ({
      ...doc,
      __id: index,
      __rank: 1.0 - index / result.documents.length
    }))
  };
}

// Invoke clustering once search results are available or algorithm changes.
autoEffect(async () => {
  await reloadClusters();
});

// When search result is loading, also show that clusters are loading.
autoEffect(() => {
  if (searchResultStore.loading) {
    clusterStore.loading = true;
    clusterStore.clusters = EMPTY_ARRAY;
  }
});

export const buildFileName = (fileNameSuffix, extension) => {
  const queryCleaned = searchResultStore.searchResult.query
    .replace(/[\s:]+/g, "_")
    .replace(/[+-\\"'/\\?]+/g, "");
  const source = searchResultStore.searchResult.sourceId;
  return `${source}-${queryCleaned}${
    queryCleaned.length > 0 ? "-" : ""
  }${fileNameSuffix}.${extension}`;
};
