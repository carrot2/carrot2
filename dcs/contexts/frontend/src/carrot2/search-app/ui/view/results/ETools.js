import React from 'react';
import { Switch } from "@blueprintjs/core";
import { TitleAndRank, Url } from "./result-components.js";

import { persistentStore } from "../../../../util/persistent-store.js";

export const etoolsConfigStore = persistentStore("etoolsConfig",
  {
    showSiteIcons: true,
    showRank: true
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
      <TitleAndRank title={document.title} rank={props.rank} showRank={config.showRank} />
      <div>{document.snippet}</div>
      {urlWithIcon}
      <div className="sources">
        {
          document.sources.map((source, index) => <span key={index}>{source}</span>)
        }
      </div>
    </>
  );
};

export const EToolsResultConfig = (props) => {
  const store = props.configStore;
  return (
    <>
      <Switch label="Show site icons" checked={store.showSiteIcons}
        onChange={e => store.showSiteIcons = e.target.checked } />
      <Switch label="Show search rank" checked={store.showRank}
        onChange={e => store.showRank = e.target.checked } />
    </>
  );
};