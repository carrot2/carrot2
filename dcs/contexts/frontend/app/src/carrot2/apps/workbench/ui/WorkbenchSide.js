import React, { useEffect } from "react";

import "./WorkbenchSide.css";

import { view } from "@risingstack/react-easy-state";

import { clusterStore, searchResultStore } from "../../../store/services.js";

import { WorkbenchSourceAlgorithm } from "./WorkbenchSourceAlgorithm.js";

import { Button } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLightbulbOn } from "@fortawesome/pro-regular-svg-icons";
import { Settings } from "../../../../carrotsearch/ui/settings/Settings.js";
import {
  parametersStateStore,
  runSearch,
  settings,
  SettingsTools
} from "./WorkbenchSettings.js";

const WorkbenchLogo = () => {
  return (
    <div className="WorkbenchLogo">
      <span>
        <span>clustering</span>
        <span className="initial">W</span>orkbench
      </span>
    </div>
  );
};

const ClusterButton = view(() => {
  useEffect(() => {
    const listener = e => {
      if ((e.keyCode === 13 || e.keyCode === 10) && e.ctrlKey) {
        runSearch();
      }
    };

    window.addEventListener("keypress", listener);
    return () => window.removeEventListener("keypress", listener);
  }, []);

  return (
    <Button
      className="ClusterButton"
      intent={
        parametersStateStore.sourceDirty || parametersStateStore.algorithmDirty
          ? "primary"
          : "none"
      }
      large={true}
      icon={<FontAwesomeIcon icon={faLightbulbOn} />}
      title="Press Ctrl+Enter to perform clustering"
      onClick={runSearch}
      loading={searchResultStore.loading || clusterStore.loading}
    >
      Cluster
    </Button>
  );
});

export const WorkbenchSide = () => {
  return (
    <div className="WorkbenchSide">
      <div className="WorkbenchSideFixed">
        <div className="WorkbenchSideHeader">
          <WorkbenchLogo />
          <ClusterButton />
        </div>

        <WorkbenchSourceAlgorithm />
        <SettingsTools />
      </div>

      <Settings settings={settings} defer={true} />
    </div>
  );
};
