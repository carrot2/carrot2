import React from "react";

import { EToolsResult, EToolsResultConfig, EToolsSourceConfig, EToolsIpBannedError, etoolsSource } from "./search-app/ui/view/results/ETools.js";
import { PubMedResult, PubMedResultConfig, PubMedSourceConfig, pubmedSource } from "./search-app/ui/view/results/PubMed.js";

const GenericError = (props) => {
  return (
    <>
      <h3>Search engine error</h3>
      <p>Search could not be performed due to the following error:</p>
      <pre>{props.error.message}</pre>
      <p>That's all we know.</p>
    </>
  );
};

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
      return <GenericError {...props} />
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
      return <GenericError {...props} />
    },
    createConfig: () => {
      return <PubMedResultConfig />;
    },
    createSourceConfig: (props) => {
      return <PubMedSourceConfig {...props} />;
    }
  }
};