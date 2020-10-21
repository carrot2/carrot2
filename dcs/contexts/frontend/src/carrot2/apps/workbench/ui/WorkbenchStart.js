import React from 'react';

import "./WorkbenchStart.css"

import { Views } from "../../../../carrotsearch/ui/Views.js";
import { Button, InputGroup } from "@blueprintjs/core";
import { EToolsSourceConfig } from "../../search-app/ui/view/results/ETools.js";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFlask } from "@fortawesome/pro-regular-svg-icons";

export const QueryWithSettings = ({ children }) => {
  return (
      <div>
        <InputGroup />
        <div style={{ margin: "2em 0", overflowY: "auto", maxHeight: "12em" }}>
          {children}
        </div>

        <Button intent="primary" icon={<FontAwesomeIcon icon={ faFlask }/>}>Cluster</Button>
      </div>
  );
};

const views = [
  {
    views: {
      "web": {
        label: "Web",
        createContentElement: (props) => {
          return (
              <QueryWithSettings>
                <EToolsSourceConfig />
              </QueryWithSettings>
          );
        }
      },
      "pubmed": {
        label: "PubMed",
        createContentElement: (props) => {
          return <div>PubMed</div>;
        }
      },
      "solr": {
        label: "Solr",
        createContentElement: (props) => {
          return <div>Solr</div>;
        }
      },
      "es": {
        label: "Elastic",
        createContentElement: (props) => {
          return <div>ES</div>;
        }
      },
      "file": {
        label: "Excel / XML / JSON",
        createContentElement: (props) => {
          return <div>File</div>;
        }
      }
    }
  }
];

export const WorkbenchStart = () => {
  return (
      <div className="WorkbenchStartScreen">
        <Views views={views} activeView="web" />
      </div>
  );
};