import { Tab } from "@blueprintjs/core";
import React from "react";
import { searchAppSources } from "../../../config-sources.js";
import { PointedTabs } from "../../../../carrotsearch/ui/PointedTabs.js";

export function SourceTabs(props) {
  return <PointedTabs id="sources" selectedTabId={props.active} onChange={props.onChange} className="sources">
    {
      Object.keys(searchAppSources).map(s => (
        <Tab key={s} id={s} title={searchAppSources[s].label} />
      ))
    }
  </PointedTabs>;
}