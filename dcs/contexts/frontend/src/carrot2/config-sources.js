import React from "react";

import { pubmed } from "./service/sources/pubmed.js";

import { EToolsResult, EToolsResultConfig, EToolsSourceConfig, etoolsSource } from "./search-app/ui/view/results/ETools.js";
import { PubMedResult, PubMedResultConfig, PubMedSourceConfig } from "./search-app/ui/view/results/PubMedResult.js";

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
    source: pubmed,
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