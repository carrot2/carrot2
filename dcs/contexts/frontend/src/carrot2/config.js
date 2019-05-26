import React from "react";
import { view } from "react-easy-state";
import { ClusterList } from "./search-app/ui/views/ClusterList.js";
import { Treemap } from "./search-app/ui/views/Treemap.js";
import { PieChart } from "./search-app/ui/views/PieChart.js";

import { IconNames } from "@blueprintjs/icons";
import { TreemapConfig } from "./search-app/ui/views/TreemapConfig.js";
import { ResultListConfig } from "./search-app/ui/ResultListConfig.js";

import { persistentStore } from "./util/persistent-store.js";
import { resultListConfigStore } from "./search-app/store/ui-config.js";

import { etools } from "./service/sources/etools.js";
import { pubmed } from "./service/sources/pubmed.js";


export const dcsConfig = {
  dcsServiceUrl: process.env.REACT_APP_DCS_SERVICE_URL || "http://localhost:8080/service/cluster?template=frontend-default"
};

export const sources = {
  "web": {
    label: "Web",
    source: etools
  },
  "pubmed": {
    label: "PubMed",
    source: pubmed
  }
};

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

export const resultsViews = {
  "list": {
    label: "Results",
    tools: [
      {
        id: "config",
        icon: IconNames.COG,
        createContentElement: (props) => {
          return <ResultListConfigView store={resultListConfigStore} />;
        }
      }
    ]
  }
};