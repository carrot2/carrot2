import React from "react";

import "./VisualizationWithList.css";

import classnames from "classnames";

import { view } from "@risingstack/react-easy-state";
import { ClusterList } from "@carrot2/app/ui/clusters/ClusterList.js";

const ClusterListView = view(ClusterList);

export const VisualizationWithList = view(
  ({ isClusterListVisible, children }) => {
    const listVisible = isClusterListVisible();

    const clusterList = listVisible ? (
      <div>
        <ClusterListView />
      </div>
    ) : null;

    return (
      <div
        className={classnames("VisualizationWithList", {
          ListVisible: listVisible
        })}
      >
        <div>{children}</div>
        {clusterList}
      </div>
    );
  }
);
