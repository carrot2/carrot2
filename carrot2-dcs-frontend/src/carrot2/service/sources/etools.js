import queryString from "query-string";

// TODO: add support for aborting running requests a'la fetch API.
export function etools(query, params) {
  const service = process.env.NODE_ENV === "production" ? live : cached;
  return service(query, params)
    .then(function (json) {
      return {
        query: json.request.query,
        matches: json.response.totalEstimatedRecords,
        documents: json.response.mergedRecords.map((record, index) => ({
          id: index.toString(),
          title: record.title,
          snippet: record.text,
          url: record.url,
          sources: record.sources
        }))
      };
    });
}

function live(query, params) {
  const url = "https://www.etools.ch/partnerSearch.do?" + queryString.stringify(
    Object.assign({
      partner: "Carrot2Json",
      query: query,
      dataSourceResults: 40,
      maxRecords: 200
    }, params)
  );

  return window.fetch(url)
    .then(function (response) {
      return response.json();
    });
}

function cached() {
  return new Promise(function (resolve) {
    window.setTimeout(function () {
      resolve(import("./etools.result" /* webpackChunkName: "etools-result-json" */));
    }, 300);
  });
}