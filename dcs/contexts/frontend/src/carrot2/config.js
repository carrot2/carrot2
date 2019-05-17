import React from "react";
import { view } from "react-easy-state";
import { ClusterList } from "./search-app/ui/views/ClusterList.js";
import { Treemap } from "./search-app/ui/views/Treemap.js";
import { PieChart } from "./search-app/ui/views/PieChart.js";

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
const PieChartView = view(PieChart);

export const views = {
  "folders": {
    label: "Folders",
    createContentElement: (props) => {
      return <ClusterListView {...props} />
    }
  },

  "treemap": {
    label: "Treemap",
    createContentElement: (props) => {
      return <TreemapView {...props} />;
    }
  },

  "pie-chart": {
    label: "Pie-chart",
    createContentElement: (props) => {
      return <PieChartView {...props} />;
    }
  }
};