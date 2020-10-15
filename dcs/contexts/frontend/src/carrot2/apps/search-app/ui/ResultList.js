import './ResultList.css';

import React, { useEffect, useRef } from 'react';
import PropTypes from "prop-types";

import { view, autoEffect, clearEffect } from "@risingstack/react-easy-state";
import { ClusterSelectionSummary, ClusterInSummary } from "./ClusterSelectionSummary.js";
import { Optional } from "./Optional.js";

import { sources } from "../../../config-sources.js";
import { clusterSelectionStore } from "../store/selection.js";

const ResultClusters = view(props => {
  const selectionStore = clusterSelectionStore;
  return (
    <div className="ResultClusters">
      <span>
      {
        (props.result.clusters || []).map(c =>
          <ClusterInSummary cluster={c} key={c.id} onClick={() => selectionStore.toggleSelection(c)} />)
      }
      </span>
    </div>
  );
});

const Result = view(props => {
  const document = props.document;
  const config = props.commonConfigStore;
  const source = sources[props.source];

  return (
    <a href={document.url} target={config.openInNewTab ? "_blank" : "_self"} rel="noopener noreferrer"
       style={{display: props.visibilityStore.isVisible(document) ? "block" : "none"}}>
      {source.createResult(props)}
      <Optional visible={config.showClusters} content={ () => <ResultClusters result={document} /> } />
    </a>
  );
});

const ClusterSelectionSummaryView = view(ClusterSelectionSummary);

export const ResultList = view(props => {
  const container = useRef(undefined);

  // Reset document list scroll on cluster selection changes.
  useEffect(() => {
    const resetScroll = () => {
      const selected = props.clusterSelectionStore.selected;

      // A dummy always-true condition to prevent removal of this code.
      // We need to read some property of the selected cluster set to
      // get notifications of changes.
      if (selected.size >= 0 && container.current) {
        container.current.scrollTop = 0;
      }
    };
    autoEffect(resetScroll);
    return () => clearEffect(resetScroll);
  }, [ props.clusterSelectionStore.selected ]);


  const store = props.store;
  if (store.searchResult === null) {
    return null;
  }
  return (
    <div className="ResultList" ref={container}>
      <Optional visible={!store.loading} content={() => (
        <div>
          <ClusterSelectionSummaryView clusterSelectionStore={props.clusterSelectionStore}
                                       documentVisibilityStore={props.visibilityStore}
                                       searchResultStore={store} />
          {
            store.searchResult.documents.map((document, index) =>
              <Result source={store.searchResult.source} document={document} rank={index + 1} key={document.id}
                      visibilityStore={props.visibilityStore}
                      commonConfigStore={props.commonConfigStore} />)
          }
        </div>
      )}>
      </Optional>
    </div>
  );
});

ResultList.propTypes = {
  store: PropTypes.object.isRequired,
  commonConfigStore: PropTypes.object.isRequired,
  visibilityStore: PropTypes.object.isRequired
};