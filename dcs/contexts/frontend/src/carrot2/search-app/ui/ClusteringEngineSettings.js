import "./ClusteringEngineSettings.css";

import React from "react";

import { view, store } from "react-easy-state";

import { Button, Radio, RadioGroup, Tag, Classes } from "@blueprintjs/core";
import { Optional } from "./Optional.js";

import { sources } from "../../config-sources.js";
import { algorithms } from "../../config-algorithms.js";

import { algorithmStore } from "../store/services.js";

export const ClusteringEngineSettings = view(() => {
  const store = algorithmStore;

  return (
    <div className={"ClusteringEngineSettings"}>
      <h4>Clustering algorithm</h4>
      <RadioGroup selectedValue={store.clusteringAlgorithm}
                  onChange={e => store.clusteringAlgorithm = e.currentTarget.value}>
        {
          Object.keys(algorithms).map(a => {
            const algorithm = algorithms[a];
            const commercial = algorithm.tag === "commercial";
            const label = (
              <>
                <strong>{algorithm.label}</strong>
                <Optional visible={!!algorithm.tag}
                          content={() => <Tag intent={commercial ? "warning" : "success"}
                                              icon={commercial ? "dollar" : "git-branch"}
                                              minimal={true}>{algorithm.tag}</Tag>} />
                <small>{algorithm.description}</small>
              </>
            );
            return <Radio key={a} label={label} value={a} />;
          })
        }
      </RadioGroup>
    </div>
  );
});