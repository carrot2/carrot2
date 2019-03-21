import { config } from "../config";

export function fetchClusters(algorithm, query, documents) {
  const data = new FormData();
  data.append("dcs.c2stream", documentsAsXML(query,documents));
  data.append("dcs.output.format", "JSON");
  data.append("dcs.clusters.only", "true");

  return fetch(config.dcsServiceUrl, {
    method: "POST",
    body: data
  }).then(function (response) {
    return response.json();
  }).then(function (json) {
    return json.clusters;
  });
}

function documentsAsXML(query, documents) {
  let xml = "";
  xml += "<searchresult>";
  xml += `<query>${escapeForXml(query)}</query>`;

  for (const doc of documents) {
    xml += "<document>";
    xml += `<title>${escapeForXml(doc.title)}</title>`;
    xml += `<snippet>${escapeForXml(doc.snippet)}</snippet>`;
    xml += `<url>${escapeForXml(doc.url)}</url>`;
    xml += "</document>";
  }

  xml += "</searchresult>";

  return xml;

  function escapeForXml(unsafe) {
    if (!unsafe) {
      return "";
    }
    return unsafe.replace(/[<>&'"]/g, function (c) {
      switch (c) {
        case '<': return '&lt;';
        case '>': return '&gt;';
        case '&': return '&amp;';
        case '\'': return '&apos;';
        case '"': return '&quot;';
        default: throw new Error("Unexpected");
      }
    });
  }
}