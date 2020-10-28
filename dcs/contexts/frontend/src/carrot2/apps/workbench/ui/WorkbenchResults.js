import React from 'react';

import "./WorkbenchResults.css";

import { view } from "@risingstack/react-easy-state";

import { WorkbenchSide } from "./WorkbenchSide.js";
import { Views } from "../../../../carrotsearch/ui/Views.js";
import { clusterViews, resultsViews } from "../../../config-views.js";
import { clusterStore, searchResultStore } from "../../../store/services.js";
import { Loading } from "../../../../carrotsearch/ui/Loading.js";
import { persistentStore } from "../../../util/persistent-store.js";

import { workbenchSourceAlgorithmStore } from "./WorkbenchSourceAlgorithm.js";

const uiStore = persistentStore("workbench:ui", {
  clusterView: "folders"
})

export const WorkbenchMain = view(() => {
  return (
      <div className="WorkbenchMain">
        <div className="clusters">
          <Views activeView={uiStore.clusterView} views={clusterViews} onViewChange={newView => uiStore.clusterView = newView} >
            <Loading store={clusterStore} />
          </Views>
        </div>
        <div className="docs">
          <Views views={resultsViews} activeView="list" onViewChange={() => {}} source={workbenchSourceAlgorithmStore.source}>
            <Loading store={searchResultStore} />
          </Views>
        </div>
      </div>
  );
});

export const WorkbenchResults = () => {
  return (
      <div className="WorkbenchResults">
        <WorkbenchSide />
        <WorkbenchMain />
      </div>
  );
};