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
  PubMedResultConfig,
  pubmedSource,
  PubMedSourceConfig
} from "./apps/search-app/ui/view/results/PubMed.js";

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
    },
    getSettings: () => etoolsSettings
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
    getSettings: () => {
      return [
        {
          id: "pubmed",
          type: "group",
          label: "PubMed",
          settings: [
            {
              id: "pubmed:query",
              type: "string",
              label: "Query",
              description: `
<p>
  The search query to pass to PuMed. 
</p>
          `
            },
            {
              id: "pubmed:maxResults",
              type: "number",
              label: "Max results",
              min: 0,
              max: 500,
              step: 10,
              description: `
<p>
  The number of search results to fetch.
</p>
`
            }
          ]
        }
      ];
    }
  }
};