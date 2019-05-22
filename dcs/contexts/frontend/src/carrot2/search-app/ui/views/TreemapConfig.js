import "./TreemapConfig.css";

import React from "react";
import { RadioGroup, Radio, FormGroup } from "@blueprintjs/core";

export const TreemapConfig = (props) => {
  const store = props.store;
  return (
    <div className="treemap config">
      <h4>Treemap appearance</h4>
      <FormGroup label="Layout" inline={true}>
        <RadioGroup onChange={e => store.layout = e.target.value}
                    selectedValue={store.layout}>
          <Radio label="Polygonal" value="relaxed" />
          <Radio label="Rectangular" value="squarified" />
        </RadioGroup>
      </FormGroup>
      <FormGroup label="Stacking" inline={true}>
        <RadioGroup onChange={e => store.stacking = e.target.value}
                    selectedValue={store.stacking}>
          <Radio label="Hierarchical" value="hierarchical" />
          <Radio label="Flattened" value="flattened" />
        </RadioGroup>
      </FormGroup>
    </div>
  );
};