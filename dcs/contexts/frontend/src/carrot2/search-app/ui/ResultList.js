import './ResultList.css';
import PropTypes from "prop-types";

import React from 'react';

import { view } from "react-easy-state";
import { ClusterSelectionSummary } from "./ClusterSelectionSummary.js";

import { Loading } from "./Loading";

function Document(props) {
  const document = props.document;
  const slashIndex = document.url.indexOf("/", 8);
  const domain = slashIndex > 0 ? document.url.substring(0, slashIndex) : document.url;
  const config = props.configStore;

  let siteIcon = null;
  if (config.showSiteIcons) {
    siteIcon = (
      <span className="url with-site-icon" style={{ backgroundImage: `url(${domain + "/favicon.ico"})` }}>
        <span>{document.url}</span>
      </span>
    );
  } else {
    siteIcon = <span className="url"><span>{document.url}</span></span>;
  }

  let rank = null;
  if (config.showRank) {
    rank = <span>{props.rank}</span>;
  }

  return (
    <a href={document.url} target={config.openInNewTab ? "_blank" : "_self"} rel="noopener noreferrer"
       style={{display: props.visibilityStore.isVisible(document) ? "block" : "none"}}>
      <strong>{rank}{document.title}</strong>
      <div>{document.snippet}</div>
      {siteIcon}
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
            <DocumentView document={document} key={document.id} visibilityStore={props.visibilityStore}
                          configStore={props.configStore} rank={index + 1} />)
        }
      </Loading>
    </div>
  );
}

ResultList.propTypes = {
  store: PropTypes.object.isRequired,
  configStore: PropTypes.object.isRequired,
  visibilityStore: PropTypes.object.isRequired
};