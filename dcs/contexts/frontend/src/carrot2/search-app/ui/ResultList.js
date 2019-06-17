import './ResultList.css';

import React, { useEffect, useRef } from 'react';
import PropTypes from "prop-types";

import { view } from "react-easy-state";
import { observe, unobserve } from "@nx-js/observer-util";
import { ClusterSelectionSummary, ClusterInSummary } from "./ClusterSelectionSummary.js";
import { Optional } from "./Optional.js";
import { Loading } from "./Loading";

import { sources } from "../../config-sources.js";
import { clusterSelectionStore } from "../store/selection.js";

const ResultClusters = view(props => {
  const selectionStore = clusterSelectionStore;
  return (
    <span className="ResultClusters">
      {
        (props.result.clusters || []).map(c =>
          <ClusterInSummary cluster={c} key={c.id} onClick={() => selectionStore.toggleSelection(c)} />)
      }
    </span>
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

export const Error = (props) => {
  const source = sources[props.source];
  return source.createError(props);
};

const ClusterSelectionSummaryView = view(ClusterSelectionSummary);

export function ResultList(props) {
  const container = useRef(undefined);

  // Reset document list scroll on cluster selection changes.
  useEffect(() => {
    const resetScroll = () => {
      const selected = props.clusterSelectionStore.selected;

      // A dummy always-true condition to prevent removal of this code.
      // We need to read some property of the selected cluster set to
      // get notifications of changes.
      if (selected.size >= 0) {
        container.current.scrollTop = 0;
      }
    };
    observe(resetScroll);
    return () => unobserve(resetScroll);
  }, [ props.clusterSelectionStore.selected ]);

  const store = props.store;
  return (
    <div className="ResultList" ref={container}>
      <div>
        <Loading loading={store.loading}>
          {
            store.error !== undefined ?
              <Error source={props.source} error={store.error} runSearch={props.runSearch} />
              :
              <>
                <ClusterSelectionSummaryView clusterSelectionStore={props.clusterSelectionStore}
                                             documentVisibilityStore={props.visibilityStore}
                                             searchResultStore={store} />
                {
                  store.searchResult.documents.map((document, index) =>
                  <Result source={props.source} document={document} rank={index + 1} key={document.id}
                          visibilityStore={props.visibilityStore}
                          commonConfigStore={props.commonConfigStore} />)
                }
              </>
          }
        </Loading>
      </div>
    </div>
  );
}

ResultList.propTypes = {
  store: PropTypes.object.isRequired,
  commonConfigStore: PropTypes.object.isRequired,
  visibilityStore: PropTypes.object.isRequired
};