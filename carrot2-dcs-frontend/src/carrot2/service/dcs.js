import { config } from "../config";

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

  return fetch(config.dcsServiceUrl, {
    method: "POST",
    headers: {
        "Content-Type": "application/json",
    },
    body: JSON.stringify(request)
  }).then(function (response) {
    return response.json();
  }).then(function (json) {
      let clusters = enrichClusters(json.clusters, "");
      // console.log(JSON.stringify(clusters, null, "  "));
      return clusters;
  });

    // Assign unique IDs to clusters and compute additional information about
    // their deep size, etc. This is done in-place.
  function enrichClusters(clusters, prefix) {
    var id = 0;
    for (let cluster of clusters) {
      const subclusters = cluster.subclusters || [];
      const documents = cluster.documents || [];
      enrichClusters(subclusters, prefix + id + ".");
      cluster.uniqueDocuments = Array.from(new Set(subclusters.reduce((acc, val) => ([...acc, ...val]), documents)));
      cluster.id = prefix + (id++);
      cluster.size = cluster.uniqueDocuments.length;
    }

    return clusters;
  }
}
