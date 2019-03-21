import './ClusterSelectionSummary.css';
import { Icon } from "@blueprintjs/core";
import PropTypes from "prop-types";

import React from 'react';

function ClusterInSummary(props) {
  const cluster = props.cluster;
  const subclusters = cluster.clusters || [];
  const hasSubclusters = subclusters.length > 0;
  const labels = cluster.phrases.join(", ");

  return (
    <span className="ClusterInSummary">
      <Icon icon={hasSubclusters ? "lightbulb" : "folder-close"} className="icon" iconSize="0.9em" intent="warning" />{" "}
      <span className="labels">{labels}</span>
    </span>
  );
}

/**
 * @return {null}
 */
export function ClusterSelectionSummary (props) {
  const searchResultStore = props.searchResultStore;
  if (searchResultStore.loading) {
    return null;
  }

  const selectedCluster = props.clusterSelectionStore.selected;
  const selectedDocs = props.documentVisibilityStore.visibleDocumentIds;
  if (selectedCluster.size === 0) {
    return (
      <div className="ClusterSelectionSummary">
        All retrieved results ({searchResultStore.searchResult.documents.length})
      </div>
    );
  } else if (selectedCluster.size === 1) {
    const it = selectedCluster.values();
    const cluster = it.next().value;
    return (
      <div className="ClusterSelectionSummary">
        {cluster.size} results in <ClusterInSummary cluster={cluster} />
      </div>
    )
  }

  // Multi-selection is not yet supported, so a generic fallback message here.
  return (
    <div className="ClusterSelectionSummary">
      ${selectedDocs.size} in selected clusters
    </div>
  );
}

ClusterSelectionSummary.propTypes = {
  searchResultStore: PropTypes.object.isRequired,
  clusterSelectionStore: PropTypes.object.isRequired,
  documentVisibilityStore: PropTypes.object.isRequired
};