import React from 'react';
import { Switch } from "@blueprintjs/core";
import { Optional } from "../../Optional.js";
import { TitleAndRank, Url } from "./result-components.js";

import { persistentStore } from "../../../../util/persistent-store.js";

export const etoolsConfigStore = persistentStore("etoolsConfig",
  {
    showSiteIcons: true,
    showSources: true
  }
);

/**
 * Renders a single web search result from eTools.
 */
export const EToolsResult = (props) => {
  const document = props.document;
  const slashIndex = document.url.indexOf("/", 8);
  const domain = slashIndex > 0 ? document.url.substring(0, slashIndex) : document.url;
  const config = props.configStore;
  const commonConfig = props.commonConfigStore;

  let urlWithIcon = null;
  if (config.showSiteIcons) {
    urlWithIcon = (
      <span className="url with-site-icon" style={{ backgroundImage: `url(${domain + "/favicon.ico"})` }}>
        <span>{document.url}</span>
      </span>
    );
  } else {
    urlWithIcon = <Url url={document.url} />;
  }

  return (
    <>
      <TitleAndRank title={document.title} rank={props.rank} showRank={commonConfig.showRank} />
      <div>{document.snippet}</div>
      {urlWithIcon}
      <Optional visible={config.showSources} content={() => (
        <div className="sources">
          {
            document.sources.map((source, index) => <span key={index}>{source}</span>)
          }
        </div>
      )}/>
    </>
  );
};

export const EToolsResultConfig = (props) => {
  const store = props.configStore;
  return (
    <>
      <Switch label="Show site icons" checked={store.showSiteIcons}
              onChange={e => store.showSiteIcons = e.target.checked } />
      <Switch label="Show sources" checked={store.showSources}
              onChange={e => store.showSources = e.target.checked } />
    </>
  );
};