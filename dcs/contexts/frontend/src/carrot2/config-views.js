import { IconNames } from "@blueprintjs/icons";
import React from "react";

import { view } from "react-easy-state";
import { PieChartHints } from "./search-app/ui/view/clusters/PieChartHints.js";
import { TreemapHints } from "./search-app/ui/view/clusters/TreemapHints.js";
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
      return <TreemapView {...props} configStore={treemapConfigStore} />;
    },
    tools: [
      {
        id: "interaction",
        icon: IconNames.HELP,
        createContentElement: (props) => {
          return <TreemapHints />;
        }
      },
      {
        id: "config",
        icon: IconNames.COG,
        createContentElement: (props) => {
          return <TreemapConfig store={treemapConfigStore} />;
        }
      }
    ]
  },

  "pie-chart": {
    label: "Pie-chart",
    createContentElement: (props) => {
      return <PieChartView {...props} configStore={pieChartConfigStore} />;
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