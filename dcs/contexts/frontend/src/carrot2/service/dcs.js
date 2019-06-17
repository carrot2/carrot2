import { dcsConfig } from "../config";

export function fetchClusters(query, documents) {
  // Just pick the content fields we want to cluster. No IDs, URLs, or anything else.
  const fields = ['title', 'snippet'];
  const request = {
    "documents": documents.map((doc) => {
      return fields.reduce((obj, key) => {
        return {
          ...obj,
          [key]: doc[key]
        };
      }, {});
    })
  };

  return fetch(dcsConfig.dcsServiceUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request)
  }).then(function (response) {
    if (!response.ok) {
      throw response;
    }
    return response.json();
  }).then(function (json) {
    return addOtherTopicsCluster(documents, enrichClusters(json.clusters, ""));
  });

  // Assign unique IDs to clusters and compute additional information about
  // their deep size, etc. This is done in-place.
  function enrichClusters(clusters, prefix) {
    let id = 0;
    for (let cluster of clusters) {
      const subclusters = cluster.clusters || [];
      const documents = cluster.documents || [];

      enrichClusters(subclusters, prefix + id + ".");

      cluster.uniqueDocuments = Array.from(subclusters.reduce(function fold(set, sc) {
        (sc.clusters || []).reduce(fold, set);
        for (const doc of sc.documents) { set.add(doc); }
        return set;
      }, new Set(documents)));
      cluster.id = prefix + (id++);
      cluster.size = cluster.uniqueDocuments.length;
    }

    return clusters;
  }

  function addOtherTopicsCluster(documents, topClusters) {
    const clusteredDocs = new Set();
    topClusters.forEach(c => {
      c.uniqueDocuments.forEach(clusteredDocs.add.bind(clusteredDocs));
    });

    if (clusteredDocs.size < documents.length) {
      const unclustered = documents.map(d => d.id).filter(d => !clusteredDocs.has(d));
      topClusters.push({
        id: "unclustered",
        labels: [ "Other topics "],
        documents: unclustered,
        uniqueDocuments: unclustered,
        size: unclustered.length,
        unclustered: true
      });
    }

    return topClusters;
  }
}
