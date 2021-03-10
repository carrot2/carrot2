import React from "react";

import "./PubMed.css";

import { FormGroup, InputGroup, NumericInput, Switch } from "@blueprintjs/core";

import { view } from "@risingstack/react-easy-state";
import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";
import { Optional } from "@carrotsearch/ui/Optional.js";

import { pubmed } from "../../service/sources/pubmed.js";
import { TitleAndRank, Url } from "../results/Result.js";
import { queryStore } from "../../apps/workbench/store/query-store.js";

import { resultListConfigStore } from "../results/ResultListConfig.js";
import { GenericSearchEngineErrorMessage } from "../../apps/search-app/ui/ErrorMessage.js";

const pubmedConfigStore = persistentStore("pubmedResultConfig", {
  showJournal: true,
  showKeywords: true
});

const pubmedSourceConfigStore = persistentStore("pubmedSourceResultConfig", {
  maxResults: 100,
  apiKey: ""
});

export const pubmedSource = query => {
  return pubmed(query, {
    maxResults: pubmedSourceConfigStore.maxResults,
    apiKey: pubmedSourceConfigStore.apiKey
  });
};

export const pubmedSettings = [
  {
    id: "pubmed",
    type: "group",
    label: "PubMed",
    settings: [
      {
        id: "pubmed:query",
        get: () => queryStore.query,
        set: (prop, val) => (queryStore.query = val),
        type: "string",
        label: "Query",
        description: `<p>The search query to pass to PubMed.</p>`
      },
      {
        id: "pubmed:maxResults",
        get: () => pubmedSourceConfigStore.maxResults,
        set: (prop, val) => (pubmedSourceConfigStore.maxResults = val),
        type: "number",
        label: "Max results",
        min: 0,
        max: 500,
        step: 10,
        description: `<p>The number of search results to fetch.</p>`
      },
      {
        id: "pubmed:apiKey",
        get: () => pubmedSourceConfigStore.apiKey,
        set: (prop, val) => (pubmedSourceConfigStore.apiKey = val),
        type: "string",
        label: "API key",
        description: `<p><a href="https://ncbiinsights.ncbi.nlm.nih.gov/2017/11/02/new-api-keys-for-the-e-utilities/" target=_blank>PubMed API key</a>, optional.</p>`
      }
    ]
  }
];

/**
 * Renders a single search result from PubMed.
 */
export const PubMedResult = view(props => {
  const result = props.document;
  const commonConfig = resultListConfigStore;
  const config = pubmedConfigStore;

  let rank = null;
  if (commonConfig.showRank) {
    rank = <span>{props.rank}</span>;
  }

  const maxContentChars = commonConfig.maxCharsPerResult;
  return (
    <>
      <TitleAndRank
        title={result.title}
        rank={rank}
        showRank={commonConfig.showRank}
      />
      <Optional
        visible={config.showJournal}
        content={() => (
          <div>
            {result.journal}, {result.year}
          </div>
        )}
      />
      <div>
        {(result.paragraphs || []).map(
          (() => {
            let contentCharsOutput = 0;
            return (p, index) => {
              if (maxContentChars === 0) {
                return null;
              }

              let text;

              // Allow some reasonable number of characters for a new paragraph, hence the +80.
              if (contentCharsOutput + 80 >= maxContentChars) {
                return null;
              }

              if (contentCharsOutput + p.text.length < maxContentChars) {
                text = p.text;
              } else {
                text =
                  p.text.substring(0, maxContentChars - contentCharsOutput) +
                  "\u2026";
              }
              contentCharsOutput += text.length;

              return (
                <p key={index}>
                  <Optional
                    visible={!!p.label}
                    content={() => <span>{p.label}</span>}
                  />
                  {text}
                </p>
              );
            };
          })()
        )}
      </div>
      <Optional
        visible={
          config.showKeywords && result.keywords && result.keywords.length > 0
        }
        content={() => (
          <div className="keywords">
            <span>Keywords</span>
            {result.keywords.join(", ")}
          </div>
        )}
      />
      <Url url={result.url} />
    </>
  );
});

export const PubMedResultConfig = view(() => {
  const store = pubmedConfigStore;
  return (
    <>
      <Switch
        label="Show journal"
        checked={store.showJournal}
        onChange={e => (store.showJournal = e.target.checked)}
      />
      <Switch
        label="Show keywords"
        checked={store.showKeywords}
        onChange={e => (store.showKeywords = e.target.checked)}
      />
    </>
  );
});

export const PubMedSourceConfig = view(props => {
  const store = pubmedSourceConfigStore;

  return (
    <div className="PubMedSourceConfig">
      <FormGroup
        inline={true}
        label="Max results"
        labelFor="pubmed-max-results"
      >
        <NumericInput
          id="pubmed-max-results"
          min={50}
          max={1000}
          value={store.maxResults}
          onValueChange={v => {
            store.maxResults = v;
            props.onChange();
          }}
          majorStepSize={100}
          stepSize={50}
          minorStepSize={10}
          clampValueOnBlur={true}
        />
      </FormGroup>
      <FormGroup inline={true} label="API key" labelFor="pubmed-api-key">
        <InputGroup
          id="pubmed-api-key"
          value={store.apiKey}
          onChange={e => {
            store.apiKey = e.target.value.trim();
            props.onChange();
          }}
        />
      </FormGroup>
    </div>
  );
});

export const PubMedIntro = () => {
  return (
    <>
      <p>
        Type your PubMed query in the <strong>Query</strong> box.
      </p>

      <p>
        To request larger numbers of PubMed results, you may need to get the{" "}
        <a href="https://ncbiinsights.ncbi.nlm.nih.gov/2017/11/02/new-api-keys-for-the-e-utilities/">
          NCBI API key
        </a>{" "}
        and provide it in the <strong>API key</strong> field.
      </p>
    </>
  );
};

export const pubmedSourceDescriptor = {
  label: "PubMed",
  descriptionHtml:
    "abstracts of medical papers from the PubMed database provided by NCBI.",
  contentSummary: "PubMed abstracts",
  source: pubmedSource,
  createResult: props => <PubMedResult {...props} />,
  createError: props => <GenericSearchEngineErrorMessage {...props} />,
  createConfig: () => <PubMedResultConfig />,
  createSourceConfig: props => <PubMedSourceConfig {...props} />,
  getSettings: () => pubmedSettings,
  getFieldsToCluster: () => ["title", "snippet"],
  createIntroHelp: () => <PubMedIntro />
};
