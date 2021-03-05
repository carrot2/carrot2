import React from "react";

import { Tab } from "@blueprintjs/core";
import { PointedTabs } from "@carrotsearch/ui/PointedTabs.js";

import { searchAppSources } from "../../../sources.js";

export function SourceTabs(props) {
  return (
    <PointedTabs
      id="sources"
      selectedTabId={props.active}
      onChange={props.onChange}
      className="sources"
    >
      {Object.keys(searchAppSources).map(s => (
        <Tab key={s} id={s} title={searchAppSources[s].label} />
      ))}
    </PointedTabs>
  );
}
