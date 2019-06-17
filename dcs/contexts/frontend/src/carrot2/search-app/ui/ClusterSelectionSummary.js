import './ClusterSelectionSummary.css';
import { Icon } from "@blueprintjs/core";
import PropTypes from "prop-types";

import React from 'react';
import { pluralize } from "../../util/pluralize.js";

export const ClusterInSummary = props => {
  const cluster = props.cluster;
  const subclusters = cluster.clusters || [];
  const hasSubclusters = subclusters.length > 0;
  const labels = cluster.labels.join(", ");

  return (
    <span className="ClusterInSummary" onClick={(e) => { e.preventDefault(); props.onClick && props.onClick() }}>
      <Icon icon={hasSubclusters ? "lightbulb" : "folder-close"} className="icon" iconSize="1em" intent="warning" />{" "}
      <span className="labels">{labels}</span>
    </span>
  );
};

/**
 * @return {null}
 */
export function ClusterSelectionSummary (props) {
  const searchResultStore = props.searchResultStore;
  if (searchResultStore.loading || searchResultStore.error) {
    return null;
  }

  const clusterSelection = props.clusterSelectionStore.selected;
  const selectedDocs = props.documentVisibilityStore.visibleDocumentIds;
  if (selectedDocs.size === 0) {
    return (
      <div className="ClusterSelectionSummary">
        All retrieved results ({searchResultStore.searchResult.documents.length})
      </div>
    );
  } else if (clusterSelection.size === 1) {
    const it = clusterSelection.values();
    const cluster = it.next().value;
    return (
      <div className="ClusterSelectionSummary">
        {cluster.size} results in <ClusterInSummary cluster={cluster} />
      </div>
    )
  } else if (clusterSelection.size > 1) {
    return (
      <div className="ClusterSelectionSummary">
        {pluralize(selectedDocs.size, "result")} in {clusterSelection.size} clusters
      </div>
    );
  }

  return (
    <div className="ClusterSelectionSummary">
      {pluralize(selectedDocs.size, "result")}
    </div>
  );
}

ClusterSelectionSummary.propTypes = {
  searchResultStore: PropTypes.object.isRequired,
  clusterSelectionStore: PropTypes.object.isRequired,
  documentVisibilityStore: PropTypes.object.isRequired
};