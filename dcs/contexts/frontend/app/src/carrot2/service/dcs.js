import { finishingPeriod } from "@carrotsearch/ui/lang/humanize.js";
import { dcsServiceUrl } from "../config.js";

export function fetchClusters(requestJson, documents, fields) {
  // Just pick the content fields we want to cluster. No IDs, URLs, or anything else.
  const request = {
    ...requestJson,
    documents: documents.map(doc => {
      return fields.reduce((obj, key) => {
        return {
          ...obj,
          [key]: doc[key] + ""
        };
      }, {});
    })
  };

  return fetch(dcsServiceUrl(), {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(request)
  })
    .catch(function (e) {
      return {
        statusText: finishingPeriod(
          `Failed to connect to the DCS at ${dcsServiceUrl()}: ${e.message}`
        )
      };
    })
    .then(function (response) {
      if (!response.ok) {
        throw response;
      }
      return response.json();
    })
    .then(function (json) {
      enrichClusters(json.clusters, "");
      addOtherTopicsCluster(documents, json.clusters);
      return json;
    });

  // Assign unique IDs to clusters and compute additional information about
  // their deep size, etc. This is done in-place.
  function enrichClusters(clusters, prefix) {
    let id = 0;
    for (let cluster of clusters) {
      const subclusters = cluster.clusters || [];
      const documents = cluster.documents || [];

      enrichClusters(subclusters, prefix + id + ".");

      cluster.uniqueDocuments = Array.from(
        subclusters.reduce(function fold(set, sc) {
          (sc.clusters || []).reduce(fold, set);
          for (const doc of sc.documents) {
            set.add(doc);
          }
          return set;
        }, new Set(documents))
      );
      cluster.id = prefix + id++;
      cluster.size = cluster.uniqueDocuments.length;
    }
  }

  function addOtherTopicsCluster(documents, topClusters) {
    const clusteredDocs = new Set();
    topClusters.forEach(c => {
      c.uniqueDocuments.forEach(clusteredDocs.add.bind(clusteredDocs));
    });

    if (clusteredDocs.size < documents.length) {
      const unclustered = documents
        .map((d, i) => i)
        .filter(d => !clusteredDocs.has(d));
      topClusters.push({
        id: "unclustered",
        labels: ["Other topics "],
        documents: unclustered,
        uniqueDocuments: unclustered,
        size: unclustered.length,
        unclustered: true
      });
    }
  }
}
