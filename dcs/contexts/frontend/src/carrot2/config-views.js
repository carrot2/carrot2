import React from "react";

import { IconNames } from "@blueprintjs/icons";

import { view } from "@risingstack/react-easy-state";
import { PieChartHints } from "./apps/search-app/ui/view/clusters/PieChartHints.js";
import { TreemapHints } from "./apps/search-app/ui/view/clusters/TreemapHints.js";
import { VisualizationExport } from "./apps/search-app/ui/view/clusters/VisualizationExport.js";
import { Lazy } from "./ui/Lazy.js";
import { persistentStore } from "./util/persistent-store.js";

import { ResultListConfig } from "./apps/search-app/ui/ResultListConfig.js";
import { ClusterList } from "./apps/search-app/ui/view/clusters/ClusterList.js";
import { PieChartConfig } from "./apps/search-app/ui/view/clusters/PieChartConfig.js";
import { TreemapConfig } from "./apps/search-app/ui/view/clusters/TreemapConfig.js";

import { sources } from "./config-sources.js";
import { ResultList } from "./apps/search-app/ui/ResultList.js";
import { searchResultStore } from "./apps/search-app/store/services.js";
import {
  clusterSelectionStore,
  documentVisibilityStore
} from "./apps/search-app/store/selection.js";

const ClusterListView = view(ClusterList);
const ResultListConfigView = view(ResultListConfig);

const treemapConfigStore = persistentStore("treemapConfig",
    {
      layout: "relaxed",
      stacking: "hierarchical",
      includeResults: true
    }
);

const pieChartConfigStore = persistentStore("pieChartConfig",
    {
      includeResults: true
    }
);

const treemapImplRef = { current: undefined };
const piechartImplRef = { current: undefined };

const treemapLoader = () => {
  return import(
      /* webpackChunkName: "treemap" */
      /* webpackPrefetch: true */
      "./apps/search-app/ui/view/clusters/Treemap.js")
      .then(module => view(module.Treemap));
};

const piechartLoader = () => {
  return import(
      /* webpackChunkName: "piechart" */
      /* webpackPrefetch: true */
      "./apps/search-app/ui/view/clusters/PieChart.js")
      .then(module => view(module.PieChart));
};

// TODO: convert to a series of some internal API calls?
export const clusterViews = [
  {
    label: "Clusters",
    views: {
      "folders": {
        label: "list",
        createContentElement: (props) => {
          return <ClusterListView {...props} />
        },
        tools: []
      },

      "treemap": {
        label: "treemap",
        createContentElement: (visible) => {
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
            icon: IconNames.HELP,
            createContentElement: (props) => {
              return <TreemapHints />;
            },
            title: "Treemap interaction help"
          },
          {
            id: "export-image",
            createContentElement: (props) => {
              return <VisualizationExport implRef={treemapImplRef} fileNameSuffix="treemap" />;
            },
            title: "Export treemap as JPEG"
          },
          {
            id: "config",
            icon: IconNames.COG,
            createContentElement: (props) => {
              return <TreemapConfig store={treemapConfigStore} />;
            },
            title: "Treemap settings"
          }
        ]
      },

      "pie-chart": {
        label: "pie-chart",
        createContentElement: (visible) => {
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
            icon: IconNames.HELP,
            createContentElement: (props) => {
              return <PieChartHints />;
            }
          },
          {
            id: "export-image",
            createContentElement: (props) => {
              return <VisualizationExport implRef={piechartImplRef} fileNameSuffix="pie-chart" />;
            },
            title: "Export pie-chart as JPEG"
          },
          {
            id: "config",
            icon: IconNames.COG,
            createContentElement: (props) => {
              return <PieChartConfig store={pieChartConfigStore} />;
            }
          }
        ]
      }
    }
  }
];

export const resultListConfigStore = persistentStore("resultListConfig",
    {
      showRank: true,
      openInNewTab: true,
      showClusters: true,
      maxCharsPerResult: 400
    }
);

export const resultsViews = [
  {
    label: "Results",
    views: {
      "list": {
        label: "list",
        createContentElement: props => {
          return (
              <ResultList {...props} store={searchResultStore}
                          visibilityStore={documentVisibilityStore}
                          clusterSelectionStore={clusterSelectionStore}
                          commonConfigStore={resultListConfigStore} />
          );
        },
        tools: [
          {
            id: "config",
            icon: IconNames.COG,
            createContentElement: (props) => {
              return (
                  <ResultListConfigView store={resultListConfigStore}>
                    {sources[props.source].createConfig()}
                  </ResultListConfigView>
              );
            }
          }
        ]
      }
    }
  }
];