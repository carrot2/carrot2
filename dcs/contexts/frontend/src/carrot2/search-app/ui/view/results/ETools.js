import "./ETools.css";

import React from 'react';

import { view } from "react-easy-state";

import { FormGroup, HTMLSelect, Radio, RadioGroup, Switch } from "@blueprintjs/core";
import { Optional } from "../../Optional.js";
import { TitleAndRank, Url } from "./result-components.js";

import { etools } from "../../../../service/sources/etools.js";
import { persistentStore } from "../../../../util/persistent-store.js";

const etoolsResultsConfigStore = persistentStore("etoolsResultConfig",
  {
    showSiteIcons: true,
    showSources: true
  }
);

const etoolsSourceConfigStore = persistentStore("etoolsSourceConfig",
  {
    safeSearch: true,
    dataSources: "all",
    language: "all",
    country: "web"
  }
);

export const etoolsSource = (query) => {
  const store = etoolsSourceConfigStore;
  return etools(query, {
    safeSearch: store.safeSearch,
    dataSources: store.dataSources,
    language: store.language,
    country: store.country
  });
};

/**
 * Renders a single web search result from eTools.
 */
export const EToolsResult = view((props) => {
  const document = props.document;
  const slashIndex = document.url.indexOf("/", 8);
  const domain = slashIndex > 0 ? document.url.substring(0, slashIndex) : document.url;
  const config = etoolsResultsConfigStore;
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

  const maxContentChars = commonConfig.maxCharsPerResult;
  let snippet;
  if (maxContentChars === 0) {
    snippet = null;
  } else if (document.snippet && document.snippet.length > maxContentChars) {
    snippet = document.snippet.substring(0, maxContentChars) + "\u2026";
  } else {
    snippet = document.snippet;
  }

  return (
    <>
      <TitleAndRank title={document.title} rank={props.rank} showRank={commonConfig.showRank} />
      <div>{snippet}</div>
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
});

export const EToolsResultConfig = view(() => {
  const store = etoolsResultsConfigStore;
  return (
    <>
      <Switch label="Show site icons" checked={store.showSiteIcons}
              onChange={e => store.showSiteIcons = e.target.checked } />
      <Switch label="Show sources" checked={store.showSources}
              onChange={e => store.showSources = e.target.checked } />
    </>
  );
});

export const EToolsSourceConfig = view((props) => {
  const store = etoolsSourceConfigStore;
  return (
    <div className="EToolsSourceConfig">
      <FormGroup label="Language" inline={true}>
        <HTMLSelect onChange={e => { store.language = e.currentTarget.value; props.onChange(); }}
                    value={store.language}>
          <option value="all">All</option>
          <option value="en">English</option>
          <option value="fr">French</option>
          <option value="de">German</option>
          <option value="it">Italian</option>
          <option value="es">Spanish</option>
        </HTMLSelect>
      </FormGroup>
      <FormGroup label="Country" inline={true}>
        <HTMLSelect onChange={e => { store.country = e.currentTarget.value; props.onChange(); }}
                    value={store.country}>
          <option value="web">All</option>
          <option value="AT">Austria</option>
          <option value="FR">France</option>
          <option value="DE">Germany</option>
          <option value="GB">Great Britain</option>
          <option value="IT">Italy</option>
          <option value="LI">Lichtenstein</option>
          <option value="ES">Spain</option>
          <option value="CH">Switzerland</option>
        </HTMLSelect>
      </FormGroup>
      <FormGroup inline={true} label="Sources">
        <RadioGroup onChange={e => { store.dataSources = e.currentTarget.value; props.onChange(); }}
                    selectedValue={store.dataSources}
                    inline={true}>
          <Radio label="All" value="all" />
          <Radio label="Fastest" value="fastest" />
        </RadioGroup>
      </FormGroup>
      <FormGroup inline={true} label=" ">
        <Switch label="Safe search" checked={store.safeSearch}
                onChange={e => { store.safeSearch = e.target.checked; props.onChange(); } } />
      </FormGroup>
    </div>
  );
});