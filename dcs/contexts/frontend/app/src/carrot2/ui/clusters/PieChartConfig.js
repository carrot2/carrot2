import React from "react";

import { view } from "@risingstack/react-easy-state";
import { Switch } from "@blueprintjs/core";

export const PieChartConfig = view(props => {
  const store = props.store;
  return (
    <div className="pie-chart config">
      <h4>Pie-chart appearance</h4>
      <Switch
        label="Include results as leaf nodes"
        checked={store.includeResults}
        onChange={e => (store.includeResults = e.target.checked)}
      />
    </div>
  );
});
