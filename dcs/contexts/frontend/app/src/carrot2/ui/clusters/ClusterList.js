import React, { useEffect, useRef } from "react";

import "./ClusterList.css";

import classNames from "classnames";

import { VscFolder, VscLightbulb } from "react-icons/vsc";

import { view } from "@risingstack/react-easy-state";

import { clusterStore } from "../../store/services.js";
import { clusterSelectionStore } from "../../store/selection.js";
import { useScrollReset } from "@carrotsearch/ui/hooks/scroll-reset.js";

const isInView = (container, element) => {
  let cTop = container.scrollTop;
  let cBottom = cTop + container.clientHeight;
  let eTop = element.offsetTop - container.offsetTop;
  let eBottom = eTop + element.clientHeight;

  return (
    (eTop >= cTop && eBottom <= cBottom) ||
    (eTop < cTop && eBottom > cTop) ||
    (eBottom > cBottom && eTop < cBottom)
  );
};

const closestWithClass = (element, className) => {
  let result = element;
  while (
    result &&
    (!result.className || result.className.indexOf(className) < 0)
  ) {
    result = result.parentElement;
  }

  return result;
};

const useScrollIntoView = (isSelected, elementToScroll = e => e) => {
  const element = useRef();
  useEffect(() => {
    if (isSelected) {
      if (element.current) {
        const container = closestWithClass(element.current, "ClusterList");
        if (!isInView(container, element.current)) {
          elementToScroll(element.current).scrollIntoView();
        }
      }
    }
  }, [isSelected, elementToScroll]);

  return [element];
};

const TopCluster = props => {
  const cluster = props.cluster;
  const subclusters = cluster.clusters || [];
  const hasSubclusters = subclusters.length > 0;

  const meta =
    `(${cluster.size} docs` +
    (hasSubclusters ? `, ${subclusters.length} subclusters)` : ")");
  const labels = cluster.labels.join(", ");

  const clusterSelectionStore = props.clusterSelectionStore;
  const isSelected = clusterSelectionStore.isSelected(cluster);
  const className = classNames("TopCluster", {
    "with-subclusters": hasSubclusters,
    selected: isSelected
  });

  const [element] = useScrollIntoView(isSelected);

  return (
    <div
      ref={element}
      className={className}
      onClick={e => clusterSelectionStore.toggleSelection(cluster, e.ctrlKey)}
    >
      <VscLightbulb className="icon" />
      <span className="labels">{labels}</span>{" "}
      <span className="meta" dir="ltr">
        {meta}
      </span>
      <div className="subclusters">
        {subclusters.map(subcluster => (
          <SubClusterView
            key={subcluster.id}
            cluster={subcluster}
            clusterSelectionStore={clusterSelectionStore}
          />
        ))}
      </div>
    </div>
  );
};

const TopClusterView = view(TopCluster);

const SubCluster = props => {
  const cluster = props.cluster;
  const labels = cluster.labels.join(", ");
  const meta = `(${cluster.size})`;
  const metaTitle = `(${cluster.size} docs)`;
  const clusterSelectionStore = props.clusterSelectionStore;

  const isSelected = clusterSelectionStore.isSelected(cluster);
  const className = classNames("SubCluster", {
    selected: isSelected
  });

  const [element] = useScrollIntoView(isSelected, c =>
    closestWithClass(c, "TopCluster")
  );

  return (
    <span
      ref={element}
      className={className}
      onClick={e => {
        e.stopPropagation();
        clusterSelectionStore.toggleSelection(cluster, e.ctrlKey);
      }}
    >
      <span className="icon">
        <VscFolder className="icon" />
        {"\u00a0"}
      </span>
      <span className="labels">{labels}</span>
      {"\u00a0"}
      <span className="meta" title={metaTitle} dir="ltr">
        {meta}
      </span>{" "}
    </span>
  );
};

const SubClusterView = view(SubCluster);

export const ClusterList = () => {
  const clusters = clusterStore.clusters;
  const flatClusters = clusters.reduce((flat, c) => {
    return flat && (!c.clusters || c.clusters.length === 0);
  }, true);

  const { container, scrollReset } = useScrollReset();

  // Reset scroll on new cluster list.
  useEffect(() => {
    scrollReset();
  }, [clusters, scrollReset]);

  return (
    <div
      className={"ClusterList" + (flatClusters ? " flat" : "")}
      ref={container}
    >
      <div>
        {clusters.length > 0 ? (
          clusters.map(cluster => (
            <TopClusterView
              cluster={cluster}
              key={cluster.id}
              clusterSelectionStore={clusterSelectionStore}
            />
          ))
        ) : (
          <div>No clusters to show</div>
        )}
      </div>
    </div>
  );
};
