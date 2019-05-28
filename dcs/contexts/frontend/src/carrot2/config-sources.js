import React from "react";

import { view } from "react-easy-state";

import { etools } from "./service/sources/etools.js";
import { pubmed } from "./service/sources/pubmed.js";

import { EToolsResult } from "./search-app/ui/view/results/EToolsResult.js";
import { PubMedResult } from "./search-app/ui/view/results/PubMedResult.js";

const EToolsResultView = view(EToolsResult);
const PubMedResultView = view(PubMedResult);

export const sources = {
  "web": {
    label: "Web",
    source: etools,
    createResult: (props) => {
      return <EToolsResultView {...props} />;
    }
  },
  "pubmed": {
    label: "PubMed",
    source: pubmed,
    createResult: (props) => {
      return <PubMedResultView {...props} />;
    }
  }
};