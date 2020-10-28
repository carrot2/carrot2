import React from "react";

import "./WorkbenchSide.css";

import { sources } from "../../../config-sources.js";
import { algorithms } from "../../../config-algorithms.js";
import { searchResultStore } from "../../../store/services.js";
import { queryStore } from "../store/query-store.js";

import {
  WorkbenchSourceAlgorithm,
  workbenchSourceAlgorithmStore
} from "./WorkbenchSourceAlgorithm.js";
import { Button } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFlask } from "@fortawesome/pro-regular-svg-icons";
import { Settings } from "../../../../carrotsearch/ui/settings/Settings.js";

const WorkbenchLogo = () => {
  return (
      <div className="WorkbenchLogo">
        <span><span>clustering</span><span className="initial">W</span>orkbench</span>
      </div>
  );
};

const settings = {
  id: "root",
  settings: [
    { components: sources, type: "source" },
    { components: algorithms, type: "algorithm" }
  ].map(t => {
    return Object.keys(t.components).map(k => {
      const settings = t.components[k].getSettings();
      settings.forEach(s => {
        s.visible = s => {
          switch (t.type) {
            case "source":
              return k === workbenchSourceAlgorithmStore.source;

            case "algorithm":
              return k === workbenchSourceAlgorithmStore.algorithm;

            default:
              return false;
          }
        };
      });
      return settings;
    })
  }).flat(2)
};

const runSearch = () => {
  searchResultStore.load(workbenchSourceAlgorithmStore.source, queryStore.query);
};

export const WorkbenchSide = (() => {
  return (
      <div className="WorkbenchSide">
        <div className="WorkbenchSideHeader">
          <WorkbenchLogo />
          <Button className="ClusterButton" intent="primary" large={true}
                  icon={<FontAwesomeIcon icon={faFlask} />}
                  onClick={runSearch}>
            Cluster
          </Button>
        </div>

        <WorkbenchSourceAlgorithm />

        <Settings settings={settings} />
      </div>
  );
});
