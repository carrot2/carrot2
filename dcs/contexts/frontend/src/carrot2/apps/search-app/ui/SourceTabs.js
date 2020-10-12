import { Tab } from "@blueprintjs/core";
import React from "react";
import { sources } from "../../../config-sources.js";
import { PointedTabs } from "../../../../carrotsearch/ui/PointedTabs.js";

export function SourceTabs(props) {
  return <PointedTabs id="sources" selectedTabId={props.active} onChange={props.onChange} className="sources">
    {
      Object.keys(sources).map(s => (
        <Tab key={s} id={s} title={sources[s].label} />
      ))
    }
  </PointedTabs>;
}