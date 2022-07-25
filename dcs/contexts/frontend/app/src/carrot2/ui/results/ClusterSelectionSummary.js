import React from "react";

import "./ClusterSelectionSummary.css";

import { view } from "@risingstack/react-easy-state";

import { VscFolder, VscLightbulb } from "react-icons/vsc";

import { pluralize } from "@carrotsearch/ui/lang/humanize.js";

import { searchResultStore } from "../../store/services.js";
import {
  clusterSelectionStore,
  documentVisibilityStore
} from "../../store/selection.js";

export const ClusterInSummary = props => {
  const cluster = props.cluster;
  const subclusters = cluster.clusters || [];
  const hasSubclusters = subclusters.length > 0;
  const labels = cluster.labels.join(", ");

  return (
    <span
      className="ClusterInSummary"
      onClick={e => {
        e.preventDefault();
        props.onClick && props.onClick();
      }}
    >
      {hasSubclusters ? <VscLightbulb /> : <VscFolder />}{" "}
      <span className="labels">{labels}</span>
    </span>
  );
};

/**
 * @return {null}
 */
export const ClusterSelectionSummary = view(() => {
  const clusterSelection = clusterSelectionStore.selected;
  const selectedDocs = documentVisibilityStore.visibleDocumentIds;
  let content;
  if (searchResultStore.error) {
    content = <>Search results could not be retrieved due to an error.</>;
  } else if (selectedDocs.size === 0) {
    const totalDocCount = searchResultStore.searchResult.documents.length;
    content =
      totalDocCount > 0 ? (
        <>All retrieved results ({totalDocCount})</>
      ) : (
        <>No results to show</>
      );
  } else if (clusterSelection.size === 1) {
    const it = clusterSelection.values();
    const cluster = it.next().value;
    content = (
      <>
        {cluster.size} results in <ClusterInSummary cluster={cluster} />
      </>
    );
  } else if (clusterSelection.size > 1) {
    content = (
      <>
        {pluralize(selectedDocs.size, "result")} in {clusterSelection.size}{" "}
        clusters
      </>
    );
  } else {
    content = <>{pluralize(selectedDocs.size, "result")}</>;
  }

  return <div className="ClusterSelectionSummary">{content}</div>;
});
