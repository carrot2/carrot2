import React from "react";
import { SearchEngineErrorMessage } from "./search-app/ui/ErrorMessage.js";

import { EToolsResult, EToolsResultConfig, EToolsSourceConfig, EToolsIpBannedError, etoolsSource } from "./search-app/ui/view/results/ETools.js";
import { PubMedResult, PubMedResultConfig, PubMedSourceConfig, pubmedSource } from "./search-app/ui/view/results/PubMed.js";

export const sources = {
  "web": {
    label: "Web",
    source: etoolsSource,
    createResult: (props) => {
      return <EToolsResult {...props} />;
    },
    createError: (props) => {
      if (props.error.status === 402) {
        return <EToolsIpBannedError {...props} />;
      }
      return <SearchEngineErrorMessage {...props} />
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
    source: pubmedSource,
    createResult: (props) => {
      return <PubMedResult {...props} />;
    },
    createError: (props) => {
      return <SearchEngineErrorMessage {...props} />
    },
    createConfig: () => {
      return <PubMedResultConfig />;
    },
    createSourceConfig: (props) => {
      return <PubMedSourceConfig {...props} />;
    }
  }
};