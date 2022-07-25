import React, { useEffect } from "react";

import "./WorkbenchApp.css";

import { view } from "@risingstack/react-easy-state";

import { VscRocket } from "react-icons/vsc";

import { humanizeDuration } from "@carrotsearch/ui/lang/humanize.js";
import { Stats } from "@carrotsearch/ui/Stats.js";
import { Views } from "@carrotsearch/ui/Views.js";
import { Loading } from "@carrotsearch/ui/Loading.js";
import {
  AppMainButton,
  AppWithSidePanel
} from "@carrotsearch/ui/AppWithSidePanel.js";
import { Settings } from "@carrotsearch/ui/settings/Settings.js";

import { clusterStore, searchResultStore } from "../../../store/services.js";
import { clusterViews, resultsViews } from "../../../views.js";
import { sources } from "../../../sources.js";
import { workbenchSourceStore } from "../store/source-store.js";
import { ExportResults } from "./ExportResults.js";
import { workbenchViewStore } from "../store/view-store.js";
import { WorkbenchSourceAlgorithm } from "./WorkbenchSourceAlgorithm.js";
import {
  debouncedSettingSearchStore,
  parametersStateStore,
  runSearch,
  settings,
  SettingsTools
} from "./WorkbenchSettings.js";
import { WorkbenchIntro } from "./WorkbenchWelcome.js";

export const WorkbenchLogo = () => {
  return (
    <div className="WorkbenchLogo">
      <span>
        <span>clustering</span>
        <span className="initial">W</span>orkbench
      </span>
    </div>
  );
};

export const ClusterButton = view(() => {
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
    <AppMainButton
      intent={
        parametersStateStore.sourceDirty || parametersStateStore.algorithmDirty
          ? "primary"
          : "none"
      }
      icon={<VscRocket />}
      title="Press Ctrl+Enter to perform clustering"
      onClick={runSearch}
      loading={searchResultStore.loading || clusterStore.loading}
    >
      Cluster
    </AppMainButton>
  );
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

  return <Stats stats={stats} />;
});

const WorkbenchMain = view(() => {
  return (
    <>
      <div className="clusters">
        <Views
          activeView={workbenchViewStore.clusterView}
          views={clusterViews}
          onViewChange={newView => (workbenchViewStore.clusterView = newView)}
        >
          <Loading
            isLoading={() => {
              // Combine the DCS and view-specific loading state.
              // Visualizations take longer to initialize, so in their case
              // the loading state will take longer.
              const view =
                clusterViews[0].views[workbenchViewStore.clusterView];
              return (
                searchResultStore.loading ||
                clusterStore.loading ||
                view.isLoading?.()
              );
            }}
          />
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
    </>
  );
});

export const WorkbenchApp = view(() => {
  return (
    <AppWithSidePanel
      className="WorkbenchApp"
      isInitial={searchResultStore.initial}
      welcome={<WorkbenchIntro />}
      logo={<WorkbenchLogo />}
      button={<ClusterButton />}
      sideFixed={
        <>
          <WorkbenchSourceAlgorithm />
          <SettingsTools />
        </>
      }
      sideMain={
        <Settings
          settings={settings}
          defer={true}
          search={debouncedSettingSearchStore.getSearch()}
        />
      }
      stats={<ResultStats />}
      globalActions={<ExportResults />}
      main={<WorkbenchMain />}
    />
  );
});
