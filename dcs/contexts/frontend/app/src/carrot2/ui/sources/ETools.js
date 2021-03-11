import React from "react";
import "./ETools.css";

import {
  Button,
  FormGroup,
  HTMLSelect,
  InputGroup,
  Radio,
  RadioGroup,
  Switch
} from "@blueprintjs/core";

import { store, view } from "@risingstack/react-easy-state";

import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";
import { ButtonLink } from "@carrotsearch/ui/ButtonLink.js";
import { storeAccessors } from "@carrotsearch/ui/settings/Setting.js";
import { Optional } from "@carrotsearch/ui/Optional.js";

import { etools } from "../../service/sources/etools.js";
import { TitleAndRank, Url } from "../results/Result.js";

import { queryStore } from "../../apps/workbench/store/query-store.js";

import { resultListConfigStore } from "../results/ResultListConfig.js";
import { GenericSearchEngineErrorMessage } from "../../apps/search-app/ui/ErrorMessage.js";

const etoolsResultsConfigStore = persistentStore("etoolsResultConfig", {
  showSiteIcons: false,
  showSources: true
});

const etoolsSourceConfigStore = persistentStore("etoolsSourceConfig", {
  safeSearch: true,
  dataSources: "all",
  language: "en",
  country: "web",
  partner: "Carrot2Json",
  customerId: ""
});

const etoolsDataSourcesOptions = [
  { label: "All", value: "all" },
  { label: "Fastest", value: "fastest" }
];

const etoolsCountryOptions = [
  { value: "web", label: "All" },
  { value: "AT", label: "Austria" },
  { value: "FR", label: "France" },
  { value: "DE", label: "Germany" },
  { value: "GB", label: "Great Britain" },
  { value: "IT", label: "Italy" },
  { value: "LI", label: "Lichtenstein" },
  { value: "ES", label: "Spain" },
  { value: "CH", label: "Switzerland" }
];

const etoolsLanguageOptions = [
  { value: "all", label: "All" },
  { value: "en", label: "English" },
  { value: "fr", label: "French" },
  { value: "de", label: "German" },
  { value: "it", label: "Italian" },
  { value: "es", label: "Spanish" }
];

export const etoolsSettings = [
  {
    id: "web",
    type: "group",
    label: "Web",
    settings: [
      {
        id: "web:query",
        ...storeAccessors(queryStore, "query"),
        type: "string",
        label: "Query",
        description: `
<p>
  The search query to pass to eTools. Use the common web search engine syntax: double quotes
  for phrase search, <code>-</code> to exclude words or phrases etc.
</p>`
      },
      {
        id: "web:language",
        ...storeAccessors(etoolsSourceConfigStore, "language"),
        type: "enum",
        ui: "select",
        label: "Language",
        options: etoolsLanguageOptions,
        description: `
<p>
  Restricts the search results to a specific language.
</p>`
      },
      {
        id: "web:country",
        ...storeAccessors(etoolsSourceConfigStore, "country"),
        type: "enum",
        ui: "select",
        label: "Country",
        options: etoolsCountryOptions,
        description: `
<p>
  Restricts the search results to websites from a specific country.
</p>`
      },
      {
        id: "web:safeSearch",
        ...storeAccessors(etoolsSourceConfigStore, "safeSearch"),
        type: "boolean",
        label: "Safe search",
        description: `
<p>
  Controls filtering of offensive search results.
</p>`
      },
      {
        id: "web:dataSources",
        ...storeAccessors(etoolsSourceConfigStore, "dataSources"),
        type: "enum",
        ui: "radio",
        inline: true,
        label: "Data sources",
        advanced: true,
        options: etoolsDataSourcesOptions,
        description: `
<p>
  Determines the set of search engines from which to aggregate the results.
</p>`
      },
      {
        id: "web:partner",
        ...storeAccessors(etoolsSourceConfigStore, "partner"),
        type: "string",
        label: "Partner",
        advanced: true,
        description: `
<p>
  If you have a custom service agreement with eTools, provide your partner ID here.  
</p>`
      },
      {
        id: "web:customerId",
        ...storeAccessors(etoolsSourceConfigStore, "customerId"),
        type: "string",
        label: "Customer ID",
        advanced: true,
        description: `
<p>
  Customer ID, optional. If you have a custom service agreement with eTools, provide your customer ID here.  
</p>`
      }
    ]
  }
];

export const etoolsSource = query => {
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
export const EToolsResult = view(props => {
  const document = props.document;
  const appProtocol = new URL(window.location).protocol;
  const docHostname = new URL(
    document.url.startsWith("//") ? appProtocol + document.url : document.url
  ).hostname;
  const config = etoolsResultsConfigStore;
  const commonConfig = resultListConfigStore;

  let urlWithIcon = null;
  if (config.showSiteIcons) {
    urlWithIcon = (
      <span
        className="url with-site-icon"
        style={{
          backgroundImage: `url(${appProtocol}//${
            docHostname + "/favicon.ico"
          })`
        }}
      >
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
      <TitleAndRank
        title={document.title}
        rank={props.rank}
        showRank={commonConfig.showRank}
      />
      <div>{snippet}</div>
      {urlWithIcon}
      <Optional
        visible={config.showSources}
        content={() => (
          <div className="sources">
            {document.sources.map((source, index) => (
              <span key={index}>{source}</span>
            ))}
          </div>
        )}
      />
    </>
  );
});

export const EToolsResultConfig = view(() => {
  const store = etoolsResultsConfigStore;
  return (
    <>
      <Switch
        label="Show site icons"
        checked={store.showSiteIcons}
        onChange={e => (store.showSiteIcons = e.target.checked)}
      />
      <Switch
        label="Show sources"
        checked={store.showSources}
        onChange={e => (store.showSources = e.target.checked)}
      />
    </>
  );
});

const detailsVisibleStore = store({
  detailsVisible: false
});

const EToolsTokensForm = view(props => {
  const store = etoolsSourceConfigStore;
  const onChange = () => {
    props.onChange && props.onChange();
  };

  return (
    <div className="EToolsAccessDetails">
      <h4>eTools access tokens</h4>
      <FormGroup label="Partner ID" labelFor="partner-id" inline={true}>
        <InputGroup
          id="partner-id"
          value={store.partner}
          onChange={e => {
            onChange();
            return (store.partner = e.target.value);
          }}
        />
      </FormGroup>
      <FormGroup
        label="Customer ID"
        labelInfo="(optional)"
        labelFor="customer-id"
        inline={true}
      >
        <InputGroup
          id="customer-id"
          value={store.customerId}
          onChange={e => {
            onChange();
            return (store.customerId = e.target.value);
          }}
        />
      </FormGroup>
    </div>
  );
});

const EToolsTokensButton = props => {
  const store = detailsVisibleStore;

  return (
    <ButtonLink
      onClick={e => {
        e.preventDefault();
        store.detailsVisible = !store.detailsVisible;
      }}
    >
      {props.children}
    </ButtonLink>
  );
};

const EToolsTokensFormContainer = view(props => {
  const detailsVisible = detailsVisibleStore.detailsVisible;

  return (
    <div
      style={{ display: detailsVisible ? "block" : "none", marginTop: "2em" }}
    >
      <EToolsTokensForm onChange={props.onChange} />
      {props.children}
    </div>
  );
});

export const EToolsLink = () => {
  return (
    <a href="https://etools.ch" target="_blank" rel="noopener noreferrer">
      eTools
    </a>
  );
};

export const EToolsSourceConfig = view(props => {
  const store = etoolsSourceConfigStore;
  return (
    <div className="EToolsSourceConfig">
      <FormGroup label="Language" labelFor="etools-language" inline={true}>
        <HTMLSelect
          onChange={e => {
            store.language = e.currentTarget.value;
            props.onChange();
          }}
          id="etools-language"
          value={store.language}
        >
          {etoolsLanguageOptions.map(o => (
            <option key={o.value} value={o.value}>
              {o.label}
            </option>
          ))}
        </HTMLSelect>
      </FormGroup>
      <FormGroup label="Country" labelFor="etools-country" inline={true}>
        <HTMLSelect
          onChange={e => {
            store.country = e.currentTarget.value;
            props.onChange();
          }}
          id="etools-country"
          value={store.country}
        >
          {etoolsCountryOptions.map(o => (
            <option key={o.value} value={o.value}>
              {o.label}
            </option>
          ))}
        </HTMLSelect>
      </FormGroup>
      <FormGroup inline={true} label="Sources" labelFor="etools-sources">
        <RadioGroup
          onChange={e => {
            store.dataSources = e.currentTarget.value;
            props.onChange();
          }}
          selectedValue={store.dataSources}
          id="etools-sources"
          inline={true}
        >
          {etoolsDataSourcesOptions.map(o => (
            <Radio key={o.value} {...o} />
          ))}
        </RadioGroup>
      </FormGroup>
      <FormGroup inline={true} label=" ">
        <Switch
          label="Safe search"
          checked={store.safeSearch}
          onChange={e => {
            store.safeSearch = e.target.checked;
            props.onChange();
          }}
        />
      </FormGroup>

      <p>
        <small>
          Web search feed is kindly provided to us by <EToolsLink />. If you
          have custom eTools access tokens,{" "}
          <EToolsTokensButton>provide them here</EToolsTokensButton>.
        </small>
      </p>

      <EToolsTokensFormContainer onChange={props.onChange} />
    </div>
  );
});

export const EToolsIpBannedError = view(() => {
  return (
    <div className="Error">
      <h3>Search limit exceeded</h3>

      <p>
        <EToolsLink />, our web search results provider, blocked access to the
        service due automated querying or excessive number of searches issued
        from your IP address.
      </p>

      <p>
        The block may be lifted after some time, but if you keep seeing this
        message, you may need to{" "}
        <a
          href="mailto:sschmid@comcepta.com"
          target="_blank"
          rel="noopener noreferrer"
        >
          contact eTools
        </a>{" "}
        to arrange for an unlimited search service.
      </p>

      <p>
        Once you get your eTools access tokens,{" "}
        <EToolsTokensButton>provide them here</EToolsTokensButton>.
      </p>

      <EToolsTokensFormContainer>
        <Button onClick={() => document.location.reload()} intent="primary">
          Apply and re-run search
        </Button>
      </EToolsTokensFormContainer>
    </div>
  );
});

export const IntroHelp = () => {
  return (
    <>
      Type your query in the <strong>Query</strong> box. Use the common web
      search engine syntax such as double quotes for{" "}
      <code>"phrase search"</code>,<code>-</code> to exclude words or phrases
      etc.
    </>
  );
};

export const etoolsSourceDescriptor = {
  label: "Web",
  descriptionHtml:
    "web search results provided by <a href='https://etools.ch' target='_blank'>etools.ch</a>. Extensive use may require special arrangements with the <a href='mailto:sschmid@comcepta.com' target='_blank'>owner</a> of the etools.ch service.",
  contentSummary: "Web search results",
  source: etoolsSource,
  createResult: props => <EToolsResult {...props} />,
  createError: error => {
    if (error && error.status === 403) {
      return <EToolsIpBannedError />;
    }
    return <GenericSearchEngineErrorMessage />;
  },
  createConfig: () => <EToolsResultConfig />,
  createSourceConfig: props => <EToolsSourceConfig {...props} />,
  getSettings: () => etoolsSettings,
  getFieldsToCluster: () => ["title", "snippet"],
  createIntroHelp: () => <IntroHelp />
};
