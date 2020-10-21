import React from 'react';
import "./WorkbenchSourceAlgorithm.css";

import { view } from "@risingstack/react-easy-state";
import { FormGroup, HTMLSelect } from "@blueprintjs/core";

import { persistentStore } from "../../../util/persistent-store.js";

import { sources } from "../../../config-sources.js";
import { algorithms } from "../../../config-algorithms.js";

export const workbenchSourceAlgorithmStore = persistentStore("workbench:sourceAlgorithm", {
  source: Object.keys(sources)[0],
  algorithm: Object.keys(algorithms)[0]
});

const ComponentSelect = view(({ label, id, components, storeKey }) => {
  const htmlId = `workbench-${id}`;
  return (
      <FormGroup label={label} labelFor={htmlId} inline={true} >
        <HTMLSelect value={workbenchSourceAlgorithmStore[storeKey]}
                    onChange={e => { workbenchSourceAlgorithmStore[storeKey] = e.target.value; }}
                    id={htmlId} fill={true}>
          {
            Object.keys(components).map(k => {
              const component = components[k];
              return <option key={k} value={k}>{component.label}</option>;
            })
          }
        </HTMLSelect>
      </FormGroup>
  );
});

export const WorkbenchSourceAlgorithm = () => {
  return (
      <div className="WorkbenchSourceAlgorithm">
        <ComponentSelect label="Data source" id="source" components={sources} storeKey="source" />
        <ComponentSelect label="Clustering algorithm" id="algorithm" components={algorithms} storeKey="algorithm" />
      </div>
  );
};