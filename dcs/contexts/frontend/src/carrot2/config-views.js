import { IconNames } from "@blueprintjs/icons";
import React from "react";

import { view } from "react-easy-state";
import { persistentStore } from "./util/persistent-store.js";

import { ResultListConfig } from "./search-app/ui/ResultListConfig.js";
import { ClusterList } from "./search-app/ui/view/clusters/ClusterList.js";
import { PieChart } from "./search-app/ui/view/clusters/PieChart.js";
import { Treemap } from "./search-app/ui/view/clusters/Treemap.js";
import { TreemapConfig } from "./search-app/ui/view/clusters/TreemapConfig.js";

import { sources } from "./config-sources.js";

const ClusterListView = view(ClusterList);
const TreemapView = view(Treemap);
const TreemapConfigView = view(TreemapConfig);
const PieChartView = view(PieChart);
const ResultListConfigView = view(ResultListConfig);

const treemapConfigStore = persistentStore("treemapConfig",
  {
    layout: "relaxed",
    stacking: "hierarchical"
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
        id: "config",
        icon: IconNames.COG,
        createContentElement: (props) => {
          return <TreemapConfigView store={treemapConfigStore} />;
        }
      }
    ]
  },

  "pie-chart": {
    label: "Pie-chart",
    createContentElement: (props) => {
      return <PieChartView {...props} />;
    },
    tools: []
  }
};

export const resultListConfigStore = persistentStore("resultListConfig",
  {
    openInNewTab: true
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