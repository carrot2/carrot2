import React from "react";
import { Switch, NumericInput, FormGroup } from "@blueprintjs/core";

export const ResultListConfig = (props) => {
  const store = props.store;
  return (
    <>
      <h4>Result list appearance</h4>
      {props.children}
      <hr />
      <Switch label="Show search rank" checked={store.showRank}
              onChange={e => store.showRank = e.target.checked } />
      <Switch label="Open links in a new tab" checked={store.openInNewTab}
              onChange={e => store.openInNewTab = e.target.checked } />
      <FormGroup inline={true} label="Max chars per result">
        <NumericInput min={0} value={store.maxCharsPerResult}
                      onValueChange={v => store.maxCharsPerResult = v}
                      majorStepSize={200} stepSize={50} minorStepSize={10} />
      </FormGroup>
    </>
  )
};