import React from "react";

import {
  faCog,
  faQuestionCircle,
  faSave
} from "@fortawesome/pro-regular-svg-icons";

import { view } from "@risingstack/react-easy-state";
import { PieChartHints } from "./ui/clusters/PieChartHints.js";
import { TreemapHints } from "./ui/clusters/TreemapHints.js";
import { VisualizationExport } from "./ui/clusters/VisualizationExport.js";
import { Lazy } from "../carrotsearch/ui/Lazy.js";
import { persistentStore } from "../carrotsearch/store/persistent-store.js";

import { ResultListConfig } from "./ui/results/ResultListConfig.js";
import { ClusterList } from "./ui/clusters/ClusterList.js";
import { PieChartConfig } from "./ui/clusters/PieChartConfig.js";
import { TreemapConfig } from "./ui/clusters/TreemapConfig.js";

import { ResultList } from "./ui/results/ResultList.js";
import { searchResultStore } from "./store/services.js";
import {
  clusterSelectionStore,
  documentVisibilityStore
} from "./store/selection.js";

const ClusterListView = view(ClusterList);

const treemapConfigStore = persistentStore("treemapConfig", {
  layout: "relaxed",
  stacking: "hierarchical",
  includeResults: true
});

const pieChartConfigStore = persistentStore("pieChartConfig", {
  includeResults: true
});

const treemapImplRef = { current: undefined };
const piechartImplRef = { current: undefined };

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
        createContentElement: visible => {
          const treemapProps = {
            visible: visible,
            configStore: treemapConfigStore,
            implRef: treemapImplRef
          };
          return <Lazy loader={treemapLoader} props={treemapProps} />;
        },
        tools: [
          {
            id: "interaction",
            icon: faQuestionCircle,
            createContentElement: props => {
              return <TreemapHints />;
            },
            title: "Treemap interaction help"
          },
          {
            id: "export-image",
            icon: faSave,
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
            id: "config",
            icon: faCog,
            createContentElement: props => {
              return <TreemapConfig store={treemapConfigStore} />;
            },
            title: "Treemap settings"
          }
        ]
      },

      "pie-chart": {
        label: "pie-chart",
        createContentElement: visible => {
          const piechartProps = {
            visible: visible,
            configStore: pieChartConfigStore,
            implRef: piechartImplRef
          };
          return <Lazy loader={piechartLoader} props={piechartProps} />;
        },
        tools: [
          {
            id: "interaction",
            icon: faQuestionCircle,
            createContentElement: props => {
              return <PieChartHints />;
            }
          },
          {
            id: "export-image",
            icon: faSave,
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
            id: "config",
            icon: faCog,
            createContentElement: props => {
              return <PieChartConfig store={pieChartConfigStore} />;
            }
          }
        ]
      }
    }
  }
];

export const resultsViews = [
  {
    label: "Results",
    views: {
      list: {
        label: "list",
        createContentElement: props => {
          return (
            <ResultList
              {...props}
              store={searchResultStore}
              visibilityStore={documentVisibilityStore}
              clusterSelectionStore={clusterSelectionStore}
            />
          );
        },
        tools: [
          {
            id: "config",
            icon: faCog,
            createContentElement: props => {
              return (
                <ResultListConfig>
                  {props.source.createConfig()}
                </ResultListConfig>
              );
            }
          }
        ]
      }
    }
  }
];
