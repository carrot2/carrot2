import React from "react";
import { view } from "react-easy-state";
import { ClusterList } from "./search-app/ui/views/ClusterList.js";
import { Treemap } from "./search-app/ui/views/Treemap.js";
import { PieChart } from "./search-app/ui/views/PieChart.js";

import { IconNames } from "@blueprintjs/icons";
import { TreemapConfig } from "./search-app/ui/views/TreemapConfig.js";
import { persistentStore } from "./util/persistent-store.js";

export const config = {
  dcsServiceUrl: process.env.REACT_APP_DCS_SERVICE_URL || "http://localhost:8080/service/cluster?template=frontend-default"
};

export const sources = {
  "web": {
    label: "Web"
  },
  "pubmed": {
    label: "PubMed"
  }
};

const ClusterListView = view(ClusterList);
const TreemapView = view(Treemap);
const TreemapConfigView = view(TreemapConfig);
const PieChartView = view(PieChart);

const treemapConfigStore = persistentStore("treemapConfig",
  {
    layout: "relaxed",
    stacking: "hierarchical"
  }
);

export const views = {
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