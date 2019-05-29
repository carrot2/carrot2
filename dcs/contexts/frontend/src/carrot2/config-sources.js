import React from "react";

import { view } from "react-easy-state";

import { etools } from "./service/sources/etools.js";
import { pubmed } from "./service/sources/pubmed.js";

import { EToolsResult, EToolsResultConfig, etoolsConfigStore } from "./search-app/ui/view/results/ETools.js";
import { PubMedResult } from "./search-app/ui/view/results/PubMedResult.js";

const EToolsResultView = view(EToolsResult);
const EToolsResultConfigView = view(EToolsResultConfig);
const PubMedResultView = view(PubMedResult);

export const sources = {
  "web": {
    label: "Web",
    source: etools,
    createResult: (props) => {
      return <EToolsResultView {...props} configStore={etoolsConfigStore} />;
    },
    createConfig: (props) => {
      return <EToolsResultConfigView configStore={etoolsConfigStore} />;
    }
  },
  "pubmed": {
    label: "PubMed",
    source: pubmed,
    createResult: (props) => {
      return <PubMedResultView {...props} />;
    },
    createConfig: (props) => {
      return <div>PubMed config</div>;
    }
  }
};