import React from "react";
import { GenericSearchEngineErrorMessage } from "./apps/search-app/ui/ErrorMessage.js";

import { EToolsResult, EToolsResultConfig, EToolsSourceConfig, EToolsIpBannedError, etoolsSource } from "./apps/search-app/ui/view/results/ETools.js";
import { PubMedResult, PubMedResultConfig, PubMedSourceConfig, pubmedSource } from "./apps/search-app/ui/view/results/PubMed.js";

export const sources = {
  "web": {
    label: "Web",
    descriptionHtml: "web search results provided by <a href='https://etools.ch'>etools.ch</a>. Extensive use may require special arrangements with the <a href='mailto:sschmid@comcepta.com' target='_blank'>owner</a> of the etools.ch service.",
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
    }
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
    }
  }
};