import React from "react";

import { EToolsResult, EToolsResultConfig, EToolsSourceConfig, etoolsSource } from "./search-app/ui/view/results/ETools.js";
import { PubMedResult, PubMedResultConfig, PubMedSourceConfig, pubmedSource } from "./search-app/ui/view/results/PubMed.js";

export const sources = {
  "web": {
    label: "Web",
    source: etoolsSource,
    createResult: (props) => {
      return <EToolsResult {...props} />;
    },
    createConfig: () => {
      return <EToolsResultConfig />;
    },
    createSourceConfig: () => {
      return <EToolsSourceConfig />;
    }
  },
  "pubmed": {
    label: "PubMed",
    source: pubmedSource,
    createResult: (props) => {
      return <PubMedResult {...props} />;
    },
    createConfig: () => {
      return <PubMedResultConfig />;
    },
    createSourceConfig: () => {
      return <PubMedSourceConfig />;
    }
  }
};