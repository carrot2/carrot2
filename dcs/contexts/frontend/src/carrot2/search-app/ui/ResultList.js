import './ResultList.css';

import React from 'react';
import PropTypes from "prop-types";

import { view } from "react-easy-state";
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
  return (
    <div className="DocumentList">
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
  );
}

ResultList.propTypes = {
  store: PropTypes.object.isRequired,
  commonConfigStore: PropTypes.object.isRequired,
  visibilityStore: PropTypes.object.isRequired
};