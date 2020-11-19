import React from "react";

import "./WorkbenchSide.css";

import { autoEffect, batch, store, view } from "@risingstack/react-easy-state";

import { sources } from "../../../config-sources.js";
import { algorithms } from "../../../config-algorithms.js";
import { algorithmStore, clusterStore, searchResultStore } from "../../../store/services.js";
import { queryStore } from "../store/query-store.js";

import { WorkbenchSourceAlgorithm} from "./WorkbenchSourceAlgorithm.js";

import { Button } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLightbulbOn } from "@fortawesome/pro-regular-svg-icons";
import { Settings } from "../../../../carrotsearch/ui/settings/Settings.js";
import { SettingsTools } from "./SettingsTools.js";
import { workbenchSourceStore } from "../store/source-store.js";

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

const parametersStateStore = store({
  sourceDirty: false,
  algorithmDirty: false
});

function collectSettings(components) {
  Object.keys(components).forEach(s => {
    const component = components[s];

    // A dummy read just to have this auto effect run on every parameter change.
    let getter;
    component.getSettings().reduce(function collect(acc, sett) {
      if (sett.type === "group") {
        if (!getter) {
          getter = sett.get;
        }
        sett.settings.reduce(collect, []);
      } else {
        if (sett.get || getter) {
          const val = (sett.get || getter)(sett);

          // Read from iterable types too, so that we pick up changes to the
          // contents of the collection and not just an update of the collection reference.
          if (typeof val?.forEach === "function") {
            let cnt = 0;
            val.forEach(v => cnt++);
          }
        }
      }
      return acc;
    }, []);
  });
}

autoEffect(() => {
  batch(() => {
    collectSettings(sources);

    // A dummy read to make this effect run also on source change
    const dummy = !!workbenchSourceStore.source;
    parametersStateStore.sourceDirty = dummy || true;
  });
});
autoEffect(() => {
  batch(() => {
    collectSettings(algorithms);

    // A dummy read to make this effect run also on source change
    const dummy = !!algorithmStore.clusteringAlgorithm;
    parametersStateStore.algorithmDirty = dummy || true;
  });
});

const runSearch = () => {
  if (parametersStateStore.sourceDirty) {
    searchResultStore.load(sources[workbenchSourceStore.source], queryStore.query);
  } else {
    clusterStore.reload();
  }
  parametersStateStore.algorithmDirty = false;
  parametersStateStore.sourceDirty = false;
};

const ClusterButton = view(() => {
  return (
      <Button className="ClusterButton"
              intent={parametersStateStore.sourceDirty || parametersStateStore.algorithmDirty ? "primary" : "none"}
              large={true}
              icon={<FontAwesomeIcon icon={faLightbulbOn} />}
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
          <SettingsTools />
        </div>

        <Settings settings={settings} />
      </div>
  );
});
