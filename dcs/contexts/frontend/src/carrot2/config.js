import React from "react";
import { view } from "react-easy-state";
import { ClusterList } from "./search-app/ui/ClusterList.js";

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
      return <div>Treemap place holder.</div>;
    }
  },

  "pie-chart": {
    label: "Pie-chart",
    createContentElement: (props) => {
      return <div>Pie-chart place holder.</div>;
    }
  }
};