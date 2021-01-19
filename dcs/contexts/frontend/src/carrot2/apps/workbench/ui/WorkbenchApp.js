import React from "react";

import "./WorkbenchApp.css";
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
import { DottedStraightArrow } from "../../../../carrotsearch/ui/arrows/DottedStraightArrow.js";
import { DottedAngledArrow } from "../../../../carrotsearch/ui/arrows/DottedAngledArrow.js";
import { DottedArrowCurly } from "../../../../carrotsearch/ui/arrows/DottedArrowCurly.js";
import { ExportResults } from "./ExportResults.js";
import { workbenchViewStore } from "../store/view-store.js";

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

const SourceConfigurationStep = view(() => {
  const source = sources[workbenchSourceStore.source];
  const help = source.createIntroHelp?.();
  return (
    <li className="SourceConfiguration">
      <DottedAngledArrow />
      <h3>
        Configure <strong>{source.label}</strong> data source
      </h3>
      {help}
    </li>
  );
});

const WorkbenchIntroSteps = () => {
  return (
    <div className="WorkbenchIntroSteps">
      <ol>
        <li className="SourceAlgorithmChoice">
          <DottedStraightArrow />
          <h3>Choose data source and clustering algorithm</h3>
        </li>
        <SourceConfigurationStep />
        <li className="ButtonPress">
          <DottedArrowCurly />
          <h3>
            Press the <strong>Cluster</strong> button
          </h3>
        </li>
      </ol>
    </div>
  );
};

export const WorkbenchIntro = () => {
  return (
    <div className="WorkbenchMain WorkbenchIntro">
      <div className="WorkbenchIntroWelcome">
        <h2>Welcome to Clustering Workbench</h2>
        <h3>
          the expert-level Carrot<sup>2</sup> application
        </h3>
      </div>
      <WorkbenchIntroSteps />
    </div>
  );
};

const WorkbenchMain = view(() => {
  if (searchResultStore.initial) {
    return <WorkbenchIntro />;
  }

  return (
    <div className="WorkbenchMain">
      <div className="stats">
        <ResultStats />
        <ExportResults />
      </div>

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
                searchResultStore.loading || clusterStore.loading || view.isLoading?.()
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
