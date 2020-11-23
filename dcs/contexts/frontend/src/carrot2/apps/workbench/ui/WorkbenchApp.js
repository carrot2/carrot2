import React from "react";

import "./WorkbenchApp.css";

import { persistentStore } from "../../../../carrotsearch/store/persistent-store.js";
import { view } from "@risingstack/react-easy-state";
import { clusterStore, searchResultStore } from "../../../store/services.js";
import { humanizeDuration } from "../../../../carrotsearch/lang/humanize.js";
import { Stats } from "../../../../carrotsearch/ui/Stats.js";
import { Views } from "../../../../carrotsearch/ui/Views.js";
import { clusterViews, resultsViews } from "../../../config-views.js";
import { Loading } from "../../../../carrotsearch/ui/Loading.js";
import { sources } from "../../../config-sources.js";
import { workbenchSourceStore } from "../store/source-store.js";
import { WorkbenchSide } from "./WorkbenchSide.js";

const uiStore = persistentStore("workbench:ui", {
  clusterView: "folders"
});

const ResultStats = view(() => {
  const stats = [
    {
      id: "result-count",
      value: searchResultStore.searchResult.documents.length,
      label: "results"
    },
    {
      id: "cluster-count",
      value: clusterStore.clusters.length,
      label: "clusters"
    },
    {
      id: "clustered-docs",
      value: (100.0 * clusterStore.getClusteredDocsRatio()).toFixed(1) + "%",
      label: "clustered docs"
    },
    {
      id: "processing-time",
      value: humanizeDuration(clusterStore.serviceInfo?.clusteringTimeMillis),
      label: "clustering time"
    }
  ];

  return (
      <div className="stats">
        <Stats stats={stats} />
      </div>
  );
});

const WorkbenchMain = view(() => {
  return (
      <div className="WorkbenchMain">
        <ResultStats />
        <div className="clusters">
          <Views
              activeView={uiStore.clusterView}
              views={clusterViews}
              onViewChange={newView => (uiStore.clusterView = newView)}
          >
            <Loading isLoading={() => clusterStore.loading} />
          </Views>
        </div>
        <div className="docs">
          <Views
              views={resultsViews}
              activeView="list"
              onViewChange={() => {}}
              source={sources[workbenchSourceStore.source]}
          >
            <Loading isLoading={() => searchResultStore.loading} />
          </Views>
        </div>
      </div>
  );
});

export const WorkbenchApp = () => {
  return (
      <div className="WorkbenchApp">
        <WorkbenchSide />
        <WorkbenchMain />
      </div>
  );
};
