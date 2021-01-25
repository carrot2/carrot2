import React from "react";

import { view } from "@risingstack/react-easy-state";

import { Switch, NumericInput, FormGroup } from "@blueprintjs/core";

import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";

export const resultListConfigStore = persistentStore("resultListConfig", {
  showRank: true,
  openInNewTab: true,
  showClusters: true,
  maxCharsPerResult: 400,
  maxResultsPerPage: 50
});

export const ResultListConfig = view(({ children }) => {
  const store = resultListConfigStore;
  return (
    <>
      <h4>Result list appearance</h4>
      {children}
      <hr />
      <Switch
        label="Show search rank"
        checked={store.showRank}
        onChange={e => (store.showRank = e.target.checked)}
      />
      <Switch
        label="Show clusters to which results belong"
        checked={store.showClusters}
        onChange={e => (store.showClusters = e.target.checked)}
      />
      <Switch
        label="Open links in a new tab"
        checked={store.openInNewTab}
        onChange={e => (store.openInNewTab = e.target.checked)}
      />
      <FormGroup
        inline={true}
        label="Max chars per result"
        labelFor="max-chars-per-result"
      >
        <NumericInput
          id="max-chars-per-result"
          min={0}
          value={store.maxCharsPerResult}
          onValueChange={v => (store.maxCharsPerResult = v)}
          majorStepSize={200}
          stepSize={50}
          minorStepSize={10}
        />
      </FormGroup>
    </>
  );
});
