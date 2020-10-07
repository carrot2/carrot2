import { Tab, Tabs } from "@blueprintjs/core";
import React from "react";
import { sources } from "../../../config-sources.js";

export function SourceTabs(props) {
  return <Tabs id="sources" selectedTabId={props.active} onChange={props.onChange} className="sources">
    {
      Object.keys(sources).map(s => (
        <Tab key={s} id={s} title={sources[s].label} />
      ))
    }
  </Tabs>;
}