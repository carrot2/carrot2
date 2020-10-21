import React, { useCallback } from 'react';

import "./WorkbenchResults.css";

import { view } from "@risingstack/react-easy-state";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFlask } from "@fortawesome/pro-regular-svg-icons";
import { Button } from "@blueprintjs/core";

import { persistentStore } from "../../../util/persistent-store.js";
import { Settings } from "../../../../carrotsearch/ui/settings/Settings.js";
import {
  WorkbenchSourceAlgorithm,
  workbenchSourceAlgorithmStore
} from "./WorkbenchSourceAlgorithm.js";

import { sources } from "../../../config-sources.js";
import { algorithms } from "../../../config-algorithms.js";

export const settingsStore = persistentStore("workbench:settings", {
  query: "",
  maxResults: 100
}, {});

const storeGet = (setting) => settingsStore[setting.id];
const storeSet = (setting, value) => settingsStore[setting.id] = value;

export const WorkbenchLogo = () => {
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
        s.component = k;
        s.componentType = t.type;
      });
      return settings;
    })
  }).flat(2)
};

const WorkbenchSide = view(() => {
  const source = workbenchSourceAlgorithmStore.source;
  const algorithm = workbenchSourceAlgorithmStore.algorithm;

  const filter = useCallback((setting) => {
    if (setting.type !== "group") {
      return true;
    }
    if (setting.componentType === "source") {
      return setting.component === source;
    }
    if (setting.componentType === "algorithm") {
      return setting.component === algorithm;
    }
    return false;
  }, [ source, algorithm ]);

  return (
      <div className="WorkbenchSide">
        <div className="WorkbenchSideHeader">
          <WorkbenchLogo />
          <Button className="ClusterButton" intent="primary" large={true}
                  icon={<FontAwesomeIcon icon={faFlask} />}>Cluster</Button>
        </div>

        <WorkbenchSourceAlgorithm />

        <Settings settings={settings} get={storeGet} set={storeSet} filter={filter} />
      </div>
  );
});

export const WorkbenchResults = () => {
  return (
      <div className="WorkbenchResults">
        <WorkbenchSide />
        <div className="WorkbenchMain">
        </div>
      </div>
  );
};