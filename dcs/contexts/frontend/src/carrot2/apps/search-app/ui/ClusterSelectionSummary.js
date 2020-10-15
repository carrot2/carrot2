import './ClusterSelectionSummary.css';
import { Icon } from "@blueprintjs/core";
import PropTypes from "prop-types";

import React from 'react';
import { pluralize } from "../../../util/humanize.js";

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
  if (searchResultStore.loading) {
    return null;
  }

  const clusterSelection = props.clusterSelectionStore.selected;
  const selectedDocs = props.documentVisibilityStore.visibleDocumentIds;
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
}

ClusterSelectionSummary.propTypes = {
  searchResultStore: PropTypes.object.isRequired,
  clusterSelectionStore: PropTypes.object.isRequired,
  documentVisibilityStore: PropTypes.object.isRequired
};