import { IconNames } from "@blueprintjs/icons";
import React from "react";

import { view } from "react-easy-state";
import { PieChartHints } from "./search-app/ui/view/clusters/PieChartHints.js";
import { TreemapHints } from "./search-app/ui/view/clusters/TreemapHints.js";
import { VisualizationExport } from "./search-app/ui/view/clusters/VisualizationExport.js";
import { persistentStore } from "./util/persistent-store.js";

import { ResultListConfig } from "./search-app/ui/ResultListConfig.js";
import { ClusterList } from "./search-app/ui/view/clusters/ClusterList.js";
import { PieChart } from "./search-app/ui/view/clusters/PieChart.js";
import { Treemap } from "./search-app/ui/view/clusters/Treemap.js";
import { PieChartConfig } from "./search-app/ui/view/clusters/PieChartConfig.js";
import { TreemapConfig } from "./search-app/ui/view/clusters/TreemapConfig.js";

import { sources } from "./config-sources.js";

const ClusterListView = view(ClusterList);
const TreemapView = view(Treemap);
const PieChartView = view(PieChart);
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

// TODO: convert to a series of some internal API calls?
export const clusterViews = {
  "folders": {
    label: "Folders",
    createContentElement: (props) => {
      return <ClusterListView {...props} />
    },
    tools: []
  },

  "treemap": {
    label: "Treemap",
    createContentElement: (props) => {
      return <TreemapView {...props} configStore={treemapConfigStore} implRef={treemapImplRef} />;
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
    label: "Pie-chart",
    createContentElement: (props) => {
      return <PieChartView {...props} configStore={pieChartConfigStore} implRef={piechartImplRef} />;
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
};

export const resultListConfigStore = persistentStore("resultListConfig",
  {
    showRank: true,
    openInNewTab: true,
    showClusters: true,
    maxCharsPerResult: 400
  }
);

export const resultsViews = {
  "list": {
    label: "Results",
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
};