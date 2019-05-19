import React from "react";
import { Switch } from "@blueprintjs/core";

export const ResultListConfig = (props) => {
  const store = props.store;
  return (
    <>
      <h4>Result list appearance</h4>
      <Switch label="Show site icons" checked={store.showSiteIcons}
              onChange={e => store.showSiteIcons = e.target.checked } />
      <Switch label="Show search rank" checked={store.showRank}
              onChange={e => store.showRank = e.target.checked } />
      <hr />
      <Switch label="Open links in a new tab" checked={store.openInNewTab}
              onChange={e => store.openInNewTab = e.target.checked } />
    </>
  )
};