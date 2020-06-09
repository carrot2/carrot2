import { FormGroup, InputGroup, NumericInput, Switch } from "@blueprintjs/core";

import React from 'react';

import { view } from "react-easy-state";

import { pubmed } from "../../../../service/sources/pubmed.js";
import { persistentStore } from "../../../../util/persistent-store.js";
import { Optional } from "../../Optional.js";
import "./PubMed.css";
import { TitleAndRank, Url } from "./result-components.js";

const pubmedConfigStore = persistentStore("pubmedResultConfig",
  {
    showJournal: true,
    showKeywords: true
  }
);

const pubmedSourceConfigStore = persistentStore("pubmedSourceResultConfig",
  {
    maxResults: 100,
    apiKey: ""
  }
);

export const pubmedSource = (query) => {
  return pubmed(query, {
    maxResults: pubmedSourceConfigStore.maxResults,
    apiKey: pubmedSourceConfigStore.apiKey
  });
};

/**
 * Renders a single search result from PubMed.
 */
export const PubMedResult = view((props) => {
  const result = props.document;
  const commonConfig = props.commonConfigStore;
  const config = pubmedConfigStore;

  let rank = null;
  if (commonConfig.showRank) {
    rank = <span>{props.rank}</span>;
  }

  const maxContentChars = commonConfig.maxCharsPerResult;
  return (
    <>
      <TitleAndRank title={result.title} rank={rank} showRank={commonConfig.showRank} />
      <Optional visible={config.showJournal} content={() => (
        <div>
          {result.journal}, {result.year}
        </div>
      )} />
      <div>
      {
        (result.paragraphs || []).map((() => {
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
              text = p.text.substring(0, maxContentChars - contentCharsOutput) + "\u2026";
            }
            contentCharsOutput += text.length;

            return (
              <p key={index}>
                <Optional visible={!!p.label} content={() => <span>{p.label}</span>} />
                {text}
              </p>
            );
          }
        })())
      }
      </div>
      <Optional visible={config.showKeywords && result.keywords && result.keywords.length > 0} content={() => (
        <div className="keywords">
          <span>Keywords</span>
          {
            result.keywords.join(", ")
          }
        </div>
      )} />
      <Url url={result.url} />
    </>
  )
});

export const PubMedResultConfig = view(() => {
  const store = pubmedConfigStore;
  return (
    <>
      <Switch label="Show journal" checked={store.showJournal}
              onChange={e => store.showJournal = e.target.checked } />
      <Switch label="Show keywords" checked={store.showKeywords}
              onChange={e => store.showKeywords = e.target.checked } />
    </>
  );
});


export const PubMedSourceConfig = view((props) => {
  const store = pubmedSourceConfigStore;

  return (
    <div className="PubMedSourceConfig">
      <FormGroup inline={true} label="Max results" labelFor="pubmed-max-results">
        <NumericInput id="pubmed-max-results" min={50} max={1000} value={store.maxResults}
                      onValueChange={v => { store.maxResults = v; props.onChange(); } }
                      majorStepSize={100} stepSize={50} minorStepSize={10} clampValueOnBlur={true} />
      </FormGroup>
      <FormGroup inline={true} label="API key" labelFor="pubmed-api-key">
        <InputGroup id="pubmed-api-key" value={store.apiKey}
                    onChange={e => { store.apiKey = e.target.value.trim(); props.onChange(); } } />
      </FormGroup>
    </div>
  );
});
