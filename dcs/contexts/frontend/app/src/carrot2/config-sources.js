import { etoolsSourceDescriptor } from "./ui/sources/ETools.js";
import { pubmedSourceDescriptor } from "./ui/sources/PubMed.js";
import { localFileSourceDescriptor } from "./ui/sources/LocalFile.js";
import { solrSourceDescriptor } from "./ui/sources/Solr.js";
import { esSourceDescriptor } from "./ui/sources/Elasticsearch.js";

export const sources = {
  web: etoolsSourceDescriptor,
  pubmed: pubmedSourceDescriptor,
  file: localFileSourceDescriptor,
  solr: solrSourceDescriptor,
  es: esSourceDescriptor
};

export const searchAppSources = {
  web: sources.web,
  pubmed: sources.pubmed
};
