import './DocumentList.css';
import PropTypes from "prop-types";

import React from 'react';

import { view } from "react-easy-state";

import { Loading } from "./Loading";

function Document(props) {
  const document = props.document;
  const slashIndex = document.url.indexOf("/", 8);
  const domain = slashIndex > 0 ? document.url.substring(0, slashIndex) : document.url;

  return (
    <a href={document.url} target="_blank" rel="noopener noreferrer"
       style={{display: props.visibilityStore.isVisible(document) ? "block" : "none"}}>
      <strong>{document.title}</strong>
      <div>{document.snippet}</div>
      <span style={{ backgroundImage: `url(${domain + "/favicon.ico"})` }}>
        <span>{document.url}</span>
      </span>
    </a>
  );
}

const DocumentView = view(Document);

export function DocumentList(props) {
  return (
    <div className="DocumentList">
      <Loading loading={props.store.loading}>
        {
          props.store.searchResult.documents.map(document =>
            <DocumentView document={document} key={document.id} visibilityStore={props.visibilityStore} />)
        }
      </Loading>
    </div>
  );
}

DocumentList.propTypes = {
  store: PropTypes.object.isRequired,
  visibilityStore: PropTypes.object.isRequired
};