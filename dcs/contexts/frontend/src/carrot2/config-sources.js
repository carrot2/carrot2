import React from "react";
import { GenericSearchEngineErrorMessage } from "./apps/search-app/ui/ErrorMessage.js";

import { etoolsSourceDescriptor } from "./ui/sources/ETools.js";
import {
  PubMedResult,
  PubMedResultConfig,
  pubmedSettings,
  pubmedSource,
  PubMedSourceConfig
} from "./ui/sources/PubMed.js";
import { localFileSourceDescriptor } from "./ui/sources/LocalFile.js";
import { solrSourceDescriptor } from "./ui/sources/Solr.js";
import { esSourceDescriptor } from "./ui/sources/Elasticsearch.js";

export const sources = {
  web: etoolsSourceDescriptor,

  pubmed: {
    label: "PubMed",
    descriptionHtml:
      "abstracts of medical papers from the PubMed database provided by NCBI.",
    source: pubmedSource,
    createResult: props => {
      return <PubMedResult {...props} />;
    },
    createError: props => {
      return <GenericSearchEngineErrorMessage {...props} />;
    },
    createConfig: () => {
      return <PubMedResultConfig />;
    },
    createSourceConfig: props => {
      return <PubMedSourceConfig {...props} />;
    },
    getSettings: () => pubmedSettings,
    getFieldsToCluster: () => ["title", "snippet"]
  },

  file: localFileSourceDescriptor,
  solr: solrSourceDescriptor,
  es: esSourceDescriptor
};

export const searchAppSources = {
  web: sources.web,
  pubmed: sources.pubmed
};
