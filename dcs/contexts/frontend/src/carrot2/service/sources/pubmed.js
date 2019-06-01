import queryString from "query-string";

function XPathProcessor(xmlString) {
  const domParser = new DOMParser();
  const document = domParser.parseFromString(xmlString, "application/xml");
  const nsResolver = document.createNSResolver(document);

  this.getNodes = (xpath, context = document) => {
    const nodes = document.evaluate(xpath, context, nsResolver, XPathResult.ANY_TYPE, null);
    let node;
    const result = [];
    while (!!(node = nodes.iterateNext())) {
      result.push(node);
    }
    return result;
  };

  this.getString = (xpath, context = document) => {
    return document.evaluate(xpath, context, nsResolver, XPathResult.STRING_TYPE, null).stringValue;
  };

  this.getStrings = (xpath, context = document) => {
    return this.getNodes(xpath, context).map(n => n.textContent);
  };
}


// TODO: add support for aborting running requests a'la fetch API.
export function pubmed(query, params) {
  const isProduction = process.env.NODE_ENV === "production";
  const serviceESearch = isProduction ? liveESearch : cachedESearch;
  const serviceEFetchh = isProduction ? liveEFetch : cachedEFetch;

  return serviceESearch(query, params)
    .then( eSearchResult => {
      return serviceEFetchh(eSearchResult.esearchresult.idlist);
    })
    .then(xml => {
      const xpath = new XPathProcessor(xml);
      const articles = xpath.getNodes("//PubmedArticle");

      const documents = articles.map((article) => {
        const id = xpath.getString(".//PMID", article);
        const paragraphs = xpath.getNodes(".//AbstractText", article)
                            .map(a => {
                              return {
                                label: a.getAttribute("Label"),
                                text: a.textContent.replace("\u2003", "")
                              };
                            });
        return {
          id: id,
          title: xpath.getString(".//ArticleTitle", article),
          snippet: paragraphs.join(" "),
          paragraphs: paragraphs,
          journal: xpath.getString(".//Journal/Title", article),
          year: xpath.getString(".//Journal//Year", article),
          keywords: xpath.getStrings(".//Keyword", article),
          url: `https://www.ncbi.nlm.nih.gov/pubmed/${id}`
        };
      });

      return {
        query: query,
        matches: documents.length,
        documents: documents
      };
    });
}

function liveESearch(query, params) {
  const url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?" + queryString.stringify(
    Object.assign({
      db: "pubmed",
      term: query,
      retmax: 100,
      retmode: "json",
      partner: "Carrot2Json",
    }, params)
  );

  return window.fetch(url).then(response => response.json());
}

function cachedESearch() {
  return new Promise(resolve => {
    window.setTimeout(() => {
      resolve(import("./pubmed.esearch.result.json" /* webpackChunkName: "pubmed-esearch-result-json" */));
    }, 300);
  });
}

function liveEFetch(ids) {
  const url = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?" + queryString.stringify(
    {
      db: "pubmed",
      id: ids.join(","),
      retmode: "xml"
    }
  );

  return window.fetch(url).then(response => response.text());
}

function cachedEFetch(ids) {
  return new Promise(resolve => {
    window.setTimeout(() => {
      resolve(
        import("./pubmed.efetch.result.xml.js" /* webpackChunkName: "pubmed-efetch-result-xml" */)
          .then(response => response.xml)
      );
    }, 300);
  });
}