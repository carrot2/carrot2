import React from 'react';

import './ClusterSelectionSummary.css';

import { view } from "@risingstack/react-easy-state";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFolder, faLightbulb } from "@fortawesome/pro-regular-svg-icons";

import { searchResultStore } from "../../../store/services.js";
import { documentVisibilityStore, clusterSelectionStore } from "../../../store/selection.js";
import { pluralize } from "../../../../carrotsearch/lang/humanize.js";

export const ClusterInSummary = props => {
  const cluster = props.cluster;
  const subclusters = cluster.clusters || [];
  const hasSubclusters = subclusters.length > 0;
  const labels = cluster.labels.join(", ");

  return (
    <span className="ClusterInSummary" onClick={(e) => { e.preventDefault(); props.onClick && props.onClick() }}>
      <FontAwesomeIcon icon={hasSubclusters ? faLightbulb : faFolder } className="icon" />{" "}
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
    content = <>All retrieved results ({searchResultStore.searchResult.documents.length})</>;
  } else if (clusterSelection.size === 1) {
    const it = clusterSelection.values();
    const cluster = it.next().value;
    content = <>{cluster.size} results in <ClusterInSummary cluster={cluster} /></>;
  } else if (clusterSelection.size > 1) {
    content = <>{pluralize(selectedDocs.size, "result")} in {clusterSelection.size} clusters</>;
  } else {
    content = <>{pluralize(selectedDocs.size, "result")}</>;
  }

  return (
    <div className="ClusterSelectionSummary">
      {content}
    </div>
  );
});