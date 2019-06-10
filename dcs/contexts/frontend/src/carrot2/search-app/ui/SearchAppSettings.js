import "./SearchAppSettings.css";

import React from "react";

import { view, store } from "react-easy-state";

import { Button, Radio, RadioGroup, Tag, Classes } from "@blueprintjs/core";
import { Optional } from "./Optional.js";

import { sources } from "../../config-sources.js";
import { algorithms } from "../../config-algorithms.js";

import { persistentStore } from "../../util/persistent-store.js";

const algorithmStore = persistentStore("clusteringAlgorithm",{
  clusteringAlgorithm: algorithms[Object.keys(algorithms)[0]]
});

const settingsChangedStore = store({
  changed: false
});

export const SearchAppSettings = view((props) => {
  const store = algorithmStore;
  const source = sources[props.source];

  // Normally, we'd use React state to manage this bit, but the settings
  // are embedded in a Popover, which unmounts its contents when the popover
  // is hidden, which discards local state.
  const changedStore = settingsChangedStore;

  return (
    <div className={"SearchAppSettings"}>
      <div>
        <div>
          <h4>{source.label} search settings</h4>
          {
            source.createSourceConfig({ onChange: () => changedStore.changed = true })
          }
        </div>
        <div>
          <h4>Clustering algorithm</h4>
          <RadioGroup selectedValue={store.clusteringAlgorithm}
                      onChange={e => {
                        changedStore.changed = true;
                        return store.clusteringAlgorithm = e.currentTarget.value;
                      }}>
            {
              Object.keys(algorithms).map(a => {
                const algorithm = algorithms[a];
                const label = (
                  <>
                    <strong>{algorithm.label}</strong>
                    <Optional visible={!!algorithm.tag} content={() => <Tag minimal={true}>{algorithm.tag}</Tag>} />
                    <small>{algorithm.description}</small>
                  </>
                );
                return <Radio key={a} label={label} value={a} />;
              })
            }
          </RadioGroup>
        </div>
      </div>
      <Button onClick={() => { props.onApply(); changedStore.changed = false; }}
              intent="primary" disabled={!changedStore.changed}
              className={Classes.POPOVER_DISMISS}>Apply and re-run search</Button>
    </div>
  );
});