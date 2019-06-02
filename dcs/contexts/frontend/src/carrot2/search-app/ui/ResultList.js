import './ResultList.css';

import React, { useEffect, useRef } from 'react';
import PropTypes from "prop-types";

import { view } from "react-easy-state";
import { observe, unobserve } from "@nx-js/observer-util";
import { ClusterSelectionSummary } from "./ClusterSelectionSummary.js";

import { Loading } from "./Loading";

import { sources } from "../../config-sources.js";

function Document(props) {
  const document = props.document;
  const config = props.commonConfigStore;

  return (
    <a href={document.url} target={config.openInNewTab ? "_blank" : "_self"} rel="noopener noreferrer"
       style={{display: props.visibilityStore.isVisible(document) ? "block" : "none"}}>
      {sources[props.source].createResult(props)}
    </a>
  );
}

const DocumentView = view(Document);
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

  return (
    <div className="ResultList" ref={container}>
      <div>
        <Loading loading={props.store.loading}>
          <ClusterSelectionSummaryView clusterSelectionStore={props.clusterSelectionStore}
                                       documentVisibilityStore={props.visibilityStore}
                                       searchResultStore={props.store} />
          {
            props.store.searchResult.documents.map((document, index) =>
              <DocumentView source={props.source} document={document} rank={index + 1} key={document.id}
                            visibilityStore={props.visibilityStore}
                            commonConfigStore={props.commonConfigStore} />)
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