import React from "react";

import "./WorkbenchSide.css";

import { autoEffect, batch, view } from "@risingstack/react-easy-state";

import { sources } from "../../../config-sources.js";
import { algorithms } from "../../../config-algorithms.js";
import { clusterStore, searchResultStore } from "../../../store/services.js";
import { queryStore } from "../store/query-store.js";
import { algorithmStore } from "../../../store/services.js";

import {
  WorkbenchSourceAlgorithm,
  workbenchSourceStore
} from "./WorkbenchSourceAlgorithm.js";

import { Button } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFlask } from "@fortawesome/pro-regular-svg-icons";
import { Settings } from "../../../../carrotsearch/ui/settings/Settings.js";

const WorkbenchLogo = () => {
  return (
      <div className="WorkbenchLogo">
        <span><span>clustering</span><span className="initial">W</span>orkbench</span>
      </div>
  );
};

const settings = {
  id: "root",
  settings: [
    { components: sources, type: "source" },
    { components: algorithms, type: "algorithm" }
  ].map(t => {
    return Object.keys(t.components).map(k => {
      const settings = t.components[k].getSettings();
      settings.forEach(s => {
        s.visible = s => {
          switch (t.type) {
            case "source":
              return k === workbenchSourceStore.source;

            case "algorithm":
              return k === algorithmStore.clusteringAlgorithm;

            default:
              return false;
          }
        };
      });
      return settings;
    })
  }).flat(2)
};

let sourceParametersDirty = false;
autoEffect(() => {
  batch(() => {
    Object.keys(sources).forEach(s => {
      const source = sources[s];

      // A dummy read just to have this auto effect run on every parameter change.
      source.getSettings().reduce(function collect(acc, sett) {
        if (sett.type === "group") {
          sett.settings.reduce(collect, []);
        } else {
          if (sett.get) {
            const val = sett.get(sett);

            // Read from iterable types too, so that we pick up changes to the
            // contents of the collection and not just an update of the collection reference.
            if (typeof val?.forEach === "function") {
              let cnt = 0;
              val.forEach(v => cnt++);
            }
          }
        }
        return acc;
      }, [ queryStore.query, workbenchSourceStore.source ]); // also react to query and source changes

    });
    sourceParametersDirty = true;
  });
});

const runSearch = () => {
  if (sourceParametersDirty) {
    sourceParametersDirty = false;
    searchResultStore.load(sources[workbenchSourceStore.source], queryStore.query);
  } else {
    clusterStore.reload();
  }
};

const ClusterButton = view(() => {
  return (
      <Button className="ClusterButton" intent="primary" large={true}
              icon={<FontAwesomeIcon icon={faFlask} />}
              onClick={runSearch}
              loading={searchResultStore.loading || clusterStore.loading}>
        Cluster
      </Button>
  );
});

export const WorkbenchSide = (() => {
  return (
      <div className="WorkbenchSide">
        <div className="WorkbenchSideFixed">
          <div className="WorkbenchSideHeader">
            <WorkbenchLogo />
            <ClusterButton />
          </div>

          <WorkbenchSourceAlgorithm />
        </div>

        <Settings settings={settings} />
      </div>
  );
});
