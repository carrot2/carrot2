import React from "react";
import { GenericSearchEngineErrorMessage } from "./apps/search-app/ui/ErrorMessage.js";

import {
  EToolsIpBannedError,
  EToolsResult,
  EToolsResultConfig, etoolsSettings,
  etoolsSource,
  EToolsSourceConfig
} from "./apps/search-app/ui/view/results/ETools.js";
import {
  PubMedResult,
  PubMedResultConfig, pubmedSettings,
  pubmedSource,
  PubMedSourceConfig
} from "./apps/search-app/ui/view/results/PubMed.js";
import { localFileSourceDescriptor } from "./ui/sources/LocalFile.js";

export const sources = {
  "web": {
    label: "Web",
    descriptionHtml: "web search results provided by <a href='https://etools.ch' target='_blank'>etools.ch</a>. Extensive use may require special arrangements with the <a href='mailto:sschmid@comcepta.com' target='_blank'>owner</a> of the etools.ch service.",
    source: etoolsSource,
    createResult: (props) => {
      return <EToolsResult {...props} />;
    },
    createError: (error) => {
      if (error && error.status === 403) {
        return <EToolsIpBannedError />;
      }
      return <GenericSearchEngineErrorMessage />
    },
    createConfig: () => {
      return <EToolsResultConfig />;
    },
    createSourceConfig: (props) => {
      return <EToolsSourceConfig {...props} />;
    },
    getSettings: () => etoolsSettings,
    getFieldsToCluster: () => [ "title", "snippet" ]
  },

  "pubmed": {
    label: "PubMed",
    descriptionHtml: "abstracts of medical papers from the PubMed database provided by NCBI.",
    source: pubmedSource,
    createResult: (props) => {
      return <PubMedResult {...props} />;
    },
    createError: (props) => {
      return <GenericSearchEngineErrorMessage {...props} />
    },
    createConfig: () => {
      return <PubMedResultConfig />;
    },
    createSourceConfig: (props) => {
      return <PubMedSourceConfig {...props} />;
    },
    getSettings: () =>  pubmedSettings,
    getFieldsToCluster: () => [ "title", "snippet" ]
  },

  "file": localFileSourceDescriptor
};

export const searchAppSources = {
  "web": sources.web,
  "pubmed": sources.pubmed
};