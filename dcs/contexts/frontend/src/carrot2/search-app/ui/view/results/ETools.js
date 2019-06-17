import { Button, FormGroup, HTMLSelect, InputGroup, Radio, RadioGroup, Switch } from "@blueprintjs/core";

import React from 'react';

import { store, view } from "react-easy-state";

import { etools } from "../../../../service/sources/etools.js";
import { persistentStore } from "../../../../util/persistent-store.js";
import { Optional } from "../../Optional.js";
import "./ETools.css";
import { TitleAndRank, Url } from "./result-components.js";

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
    country: "web",
    partner: "Carrot2Json",
    customerId: ""
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

const detailsVisibleStore = store({
  detailsVisible: false
});

const EToolsTokensForm = view(props => {
  const store = etoolsSourceConfigStore;
  const onChange = () => { props.onChange && props.onChange(); };

  return (
    <div className="EToolsAccessDetails">
      <h4>eTools access tokens</h4>
      <FormGroup label="Partner ID" labelFor="partner-id" inline={true}>
        <InputGroup id="partner-id" value={store.partner} onChange={e => { onChange(); return store.partner = e.target.value; } } />
      </FormGroup>
      <FormGroup label="Customer ID" labelFor="customer-id" inline={true}>
        <InputGroup id="customer-id" value={store.customerId} onChange={e => { onChange(); return store.customerId = e.target.value; } } />
      </FormGroup>
    </div>
  );
});

const EToolsTokensButton = props => {
  const store = detailsVisibleStore;

  return (
    <button onClick={(e) => { e.preventDefault(); store.detailsVisible = !store.detailsVisible; } }
            className="link">{props.children}</button>
  );
};

const EToolsTokensFormContainer = view((props) => {
  const detailsVisible = detailsVisibleStore.detailsVisible;

  return (
    <div style={{display: detailsVisible ? "block" : "none", marginTop: "2em"}}>
      <EToolsTokensForm onChange={props.onChange} />
      {props.children}
    </div>
  );
});

export const EToolsLink = () => {
  return (
    <a href="https://etools.ch" target="_blank" rel="noopener noreferrer">eTools</a>
  );
};

export const EToolsSourceConfig = view((props) => {
  const store = etoolsSourceConfigStore;
  return (
    <div className="EToolsSourceConfig">
      <FormGroup label="Language" labelFor="etools-language" inline={true}>
        <HTMLSelect onChange={e => { store.language = e.currentTarget.value; props.onChange(); }}
                    id="etools-language" value={store.language}>
          <option value="all">All</option>
          <option value="en">English</option>
          <option value="fr">French</option>
          <option value="de">German</option>
          <option value="it">Italian</option>
          <option value="es">Spanish</option>
        </HTMLSelect>
      </FormGroup>
      <FormGroup label="Country" labelFor="etools-country" inline={true}>
        <HTMLSelect onChange={e => { store.country = e.currentTarget.value; props.onChange(); }}
                    id="etools-country" value={store.country}>
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
      <FormGroup inline={true} label="Sources" labelFor="etools-sources">
        <RadioGroup onChange={e => { store.dataSources = e.currentTarget.value; props.onChange(); }}
                    selectedValue={store.dataSources}
                    id="etools-sources" inline={true}>
          <Radio label="All" value="all" />
          <Radio label="Fastest" value="fastest" />
        </RadioGroup>
      </FormGroup>
      <FormGroup inline={true} label=" ">
        <Switch label="Safe search" checked={store.safeSearch}
                onChange={e => { store.safeSearch = e.target.checked; props.onChange(); } } />
      </FormGroup>

      <p><small>
        Web search feed is kindly provided to us by <EToolsLink />.
        If you have custom eTools access tokens, <EToolsTokensButton>provide them here</EToolsTokensButton>.
      </small></p>

      <EToolsTokensFormContainer onChange={props.onChange} />
    </div>
  );
});

export const EToolsIpBannedError = view((props) => {
  return (
    <div className="Error">
      <h3>Search limit exceeded</h3>

      <p>
        <EToolsLink/>, our web search results provider,
        blocked your IP address due to the excessive number of searches.
      </p>

      <p>
        The block may be lifted after some time, but if you keep getting this
        message, you may need to <a href="mailto:sschmid@comcepta.com" target="_blank" rel="noopener noreferrer">contact
        eTools</a> to arrange for an unlimited search service.
      </p>

      <p>
        Once you get your eTools access tokens, <EToolsTokensButton>provide them here</EToolsTokensButton>.
      </p>

      <EToolsTokensFormContainer>
        <Button onClick={props.runSearch} intent="primary">Apply and re-run search</Button>
      </EToolsTokensFormContainer>
    </div>
  );
});