import React from "react";

import { VscListTree, VscSettings, VscQuestion } from "react-icons/vsc";

import { autoEffect, store, view } from "@risingstack/react-easy-state";
import { Lazy } from "@carrotsearch/ui/Lazy.js";
import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";

import { PieChartHints } from "./ui/clusters/PieChartHints.js";
import { TreemapHints } from "./ui/clusters/TreemapHints.js";
import { VisualizationExport } from "./ui/clusters/VisualizationExport.js";

import { ResultListConfig } from "./ui/results/ResultListConfig.js";
import { ClusterList } from "./ui/clusters/ClusterList.js";
import { PieChartConfig } from "./ui/clusters/PieChartConfig.js";
import { TreemapConfig } from "./ui/clusters/TreemapConfig.js";

import { ResultList } from "./ui/results/ResultList.js";
import { clusterStore, searchResultStore } from "./store/services.js";
import {
  clusterSelectionStore,
  documentVisibilityStore
} from "./store/selection.js";
import { VisualizationWithList } from "@carrot2/app/ui/clusters/VisualizationWithList.js";
import { Button } from "@blueprintjs/core";

const ClusterListView = view(ClusterList);

const treemapConfigStore = persistentStore("treemapConfig", {
  layout: "relaxed",
  stacking: "hierarchical",
  includeResults: true,
  showClusterList: false
});

const pieChartConfigStore = persistentStore("pieChartConfig", {
  includeResults: true,
  showClusterList: false
});

// A mechanism for capturing the "loading" state of visualizations, which should include
// time spent on initializing the view for new data (which may take a while in case of FoamTree).
const createImplRef = () => {
  // A reactive store for the visualization loading state.
  const loading = store({ loading: false });

  // Called when the visualization is initialized.
  const setRef = newRef => {
    ref.current = newRef;

    // Clear loading state when rollout starts.
    newRef.set("onRolloutStart", () => (loading.loading = false));
  };

  const ref = {
    current: undefined,
    setCurrent: setRef,

    // This reactive method will be called by the "Loading" overlay to see
    // if the overlay should show.
    isLoading: () => loading.loading
  };

  // Set loading state when there is a non-empty list of clusters to display.
  autoEffect(() => {
    if (clusterStore.clusters.length > 0) {
      loading.loading = true;
    }
  });
  return ref;
};

const treemapImplRef = createImplRef();
const piechartImplRef = createImplRef();

const treemapLoader = () => {
  return import(
    /* webpackChunkName: "treemap" */
    /* webpackPrefetch: true */
    "./ui/clusters/Treemap.js"
  ).then(module => view(module.Treemap));
};

const piechartLoader = () => {
  return import(
    /* webpackChunkName: "piechart" */
    /* webpackPrefetch: true */
    "./ui/clusters/PieChart.js"
  ).then(module => view(module.PieChart));
};

// TODO: convert to a series of some internal API calls?
export const clusterViews = [
  {
    label: "Clusters",
    views: {
      folders: {
        label: "list",
        createContentElement: props => {
          return <ClusterListView {...props} />;
        },
        tools: []
      },

      treemap: {
        label: "treemap",
        isLoading: treemapImplRef.isLoading,
        createContentElement: visible => {
          const treemapProps = {
            visible: visible,
            configStore: treemapConfigStore,
            implRef: treemapImplRef
          };
          return (
            <VisualizationWithList
              isClusterListVisible={() => treemapConfigStore.showClusterList}
            >
              <Lazy loader={treemapLoader} props={treemapProps} />
            </VisualizationWithList>
          );
        },
        tools: [
          {
            id: "interaction",
            icon: <VscQuestion />,
            createContentElement: props => {
              return <TreemapHints />;
            },
            title: "Treemap interaction help"
          },
          {
            id: "export-image",
            createContentElement: props => {
              return (
                <VisualizationExport
                  implRef={treemapImplRef}
                  fileNameSuffix="treemap"
                />
              );
            },
            title: "Export treemap as JPEG"
          },
          {
            id: "showClusterList",
            createContentElement: () => {
              return (
                <ShowClusterList
                  isEnabled={() => treemapConfigStore.showClusterList}
                  setEnabled={v => (treemapConfigStore.showClusterList = v)}
                />
              );
            },
            title: "Show cluster list"
          },
          {
            id: "config",
            icon: <VscSettings />,
            createContentElement: () => {
              return <TreemapConfig store={treemapConfigStore} />;
            },
            title: "Treemap settings"
          }
        ]
      },

      "pie-chart": {
        label: "pie-chart",
        isLoading: piechartImplRef.isLoading,
        createContentElement: visible => {
          const piechartProps = {
            visible: visible,
            configStore: pieChartConfigStore,
            implRef: piechartImplRef
          };
          return (
            <VisualizationWithList
              isClusterListVisible={() => pieChartConfigStore.showClusterList}
            >
              <Lazy loader={piechartLoader} props={piechartProps} />
            </VisualizationWithList>
          );
        },
        tools: [
          {
            id: "interaction",
            icon: <VscQuestion />,
            createContentElement: props => {
              return <PieChartHints />;
            },
            title: "Pie-chart interaction help"
          },
          {
            id: "export-image",
            createContentElement: props => {
              return (
                <VisualizationExport
                  implRef={piechartImplRef}
                  fileNameSuffix="pie-chart"
                />
              );
            },
            title: "Export pie-chart as JPEG"
          },
          {
            id: "showClusterList",
            createContentElement: () => {
              return (
                <ShowClusterList
                  isEnabled={() => pieChartConfigStore.showClusterList}
                  setEnabled={v => (pieChartConfigStore.showClusterList = v)}
                />
              );
            },
            title: "Show cluster list"
          },
          {
            id: "config",
            icon: <VscSettings />,
            createContentElement: props => {
              return <PieChartConfig store={pieChartConfigStore} />;
            },
            title: "Pie-chart settings"
          }
        ]
      }
    }
  }
];

const SourceConfig = view(() => {
  return (
    <ResultListConfig>
      {searchResultStore.searchResult.source?.createConfig()}
    </ResultListConfig>
  );
});

export const resultsViews = [
  {
    label: "Results",
    views: {
      list: {
        label: "list",
        createContentElement: visible => {
          return (
            <ResultList
              store={searchResultStore}
              visibilityStore={documentVisibilityStore}
              clusterSelectionStore={clusterSelectionStore}
            />
          );
        },
        tools: [
          {
            id: "config",
            icon: <VscSettings />,
            title: "Result list settings",
            createContentElement: () => {
              return <SourceConfig />;
            }
          }
        ]
      }
    }
  }
];

export const ShowClusterList = view(({ isEnabled, setEnabled }) => {
  return (
    <Button
      icon={<VscListTree />}
      minimal={true}
      active={isEnabled()}
      onClick={() => setEnabled(!isEnabled())}
    />
  );
});
