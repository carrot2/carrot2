import React from "react";

import { etools } from "./service/sources/etools.js";
import { pubmed } from "./service/sources/pubmed.js";

import { EToolsResult, EToolsResultConfig } from "./search-app/ui/view/results/ETools.js";
import { PubMedResult, PubMedResultConfig } from "./search-app/ui/view/results/PubMedResult.js";

export const sources = {
  "web": {
    label: "Web",
    source: etools,
    createResult: (props) => {
      return <EToolsResult {...props} />;
    },
    createConfig: () => {
      return <EToolsResultConfig />;
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
    }
  }
};