import React from "react";

import "./WorkbenchSourceAlgorithm.css";

import { view } from "@risingstack/react-easy-state";
import { FormGroup, HTMLSelect } from "@blueprintjs/core";

import { algorithmStore } from "../../../store/services.js";

import { sources } from "../../../sources.js";
import { algorithms } from "@carrot2/config/algorithms.js";
import { workbenchSourceStore } from "../store/source-store.js";

const ComponentSelect = view(({ label, id, components, get, set }) => {
  const htmlId = `workbench-${id}`;
  return (
    <FormGroup label={label} labelFor={htmlId} inline={true}>
      <HTMLSelect
        value={get()}
        onChange={e => {
          set(e.target.value);
        }}
        id={htmlId}
        fill={true}
      >
        {Object.keys(components).map(k => {
          const component = components[k];
          return (
            <option key={k} value={k}>
              {component.label}
            </option>
          );
        })}
      </HTMLSelect>
    </FormGroup>
  );
});

export const WorkbenchSourceAlgorithm = () => {
  return (
    <div className="WorkbenchSourceAlgorithm">
      <ComponentSelect
        label="Data source"
        id="source"
        components={sources}
        get={() => workbenchSourceStore.source}
        set={val => (workbenchSourceStore.source = val)}
      />
      <ComponentSelect
        label="Clustering algorithm"
        id="algorithm"
        components={algorithms}
        get={() => algorithmStore.clusteringAlgorithm}
        set={val => (algorithmStore.clusteringAlgorithm = val)}
      />
    </div>
  );
};
