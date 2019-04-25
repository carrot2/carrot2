import { Tab, Tabs } from "@blueprintjs/core";
import React from "react";
import { persistentStore } from "../../util/persistent-store";

export const sourceTabStore = persistentStore("source",
  {
    active: "web"
  },
  {
    getActive: () => sourceTabStore.active,
    setActive: (active) => sourceTabStore.active = active
  });

export function SourceTabs(props) {
  return <Tabs id="sources" selectedTabId={props.active} onChange={props.onChange} className="sources">
    <Tab id="web" title="Web" />
    <Tab id="pubmed" title="PubMed" />
  </Tabs>;
}