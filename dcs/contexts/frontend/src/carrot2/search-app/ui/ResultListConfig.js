import React from "react";
import { Switch } from "@blueprintjs/core";

export const ResultListConfig = (props) => {
  const store = props.store;
  return (
    <>
      <h4>Result list appearance</h4>
      {props.children}
      <hr />
      <Switch label="Open links in a new tab" checked={store.openInNewTab}
              onChange={e => store.openInNewTab = e.target.checked } />
    </>
  )
};