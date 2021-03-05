import "./ClusteringEngineSettings.css";

import React from "react";

import { view } from "@risingstack/react-easy-state";

import { Radio, RadioGroup, Tag } from "@blueprintjs/core";
import { Optional } from "@carrotsearch/ui/Optional.js";

import { algorithms } from "@carrot2/config/algorithms.js";

import { algorithmStore } from "../../../store/services.js";
import { Link } from "react-router-dom";

export const ClusteringEngineSettings = view(() => {
  const store = algorithmStore;

  const differentTags =
    Object.keys(algorithms).reduce((set, alg) => {
      set.add(alg.tag);
      return set;
    }, new Set()).size > 1;

  return (
    <div className={"ClusteringEngineSettings"}>
      <h4>Clustering algorithm</h4>
      <RadioGroup
        selectedValue={store.clusteringAlgorithm}
        onChange={e => (store.clusteringAlgorithm = e.currentTarget.value)}
      >
        {Object.keys(algorithms).map(a => {
          const algorithm = algorithms[a];
          const commercial = algorithm.tag === "commercial";
          const label = (
            <>
              <strong>{algorithm.label}</strong>
              <Optional
                visible={differentTags && !!algorithm.tag}
                content={() => (
                  <Tag
                    intent={commercial ? "warning" : "success"}
                    icon={commercial ? "dollar" : "git-branch"}
                    minimal={true}
                  >
                    {algorithm.tag}
                  </Tag>
                )}
              />
              <small>{algorithm.description}</small>
            </>
          );
          return <Radio key={a} label={label} value={a} />;
        })}
      </RadioGroup>
      <Link to="/about">more information</Link>
    </div>
  );
});
