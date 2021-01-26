import { finishingPeriod } from "@carrotsearch/ui/lang/humanize.js";

// TODO: add support for aborting running requests a'la fetch API.
export function etools(query, params) {
  const service = process.env.NODE_ENV === "production" ? live : cached;
  return service(query, params).then(function (json) {
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
  const url =
    "https://www.etools.ch/partnerSearch.do?" +
    new URLSearchParams(
      Object.assign(
        {
          partner: "Carrot2Json",
          query: query,
          dataSourceResults: 40,
          maxRecords: 200
        },
        params
      )
    );

  return window
    .fetch(url)
    .catch(e => {
      return {
        statusText: finishingPeriod(
          `Failed to connect to eTools service at ${url}: ${e.message}`
        )
      };
    })
    .then(function (response) {
      if (!response.ok) {
        throw response;
      }
      return response.json();
    });
}

function cached() {
  return new Promise(function (resolve, reject) {
    window.setTimeout(function () {
      // reject({ status: 403, statusText: "IP banned" });
      resolve(
        import(
          "./etools.result.json" /* webpackChunkName: "etools-result-json" */
        )
      );
    }, 300);
  });
}
