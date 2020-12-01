import React from "react";

import { sources } from "../../../config-sources.js";
import { algorithms } from "../../../config-algorithms.js";
import { workbenchSourceStore } from "../store/source-store.js";
import {
  algorithmStore,
  reloadClusters,
  searchResultStore
} from "../../../store/services.js";
import { persistentStore } from "../../../../carrotsearch/store/persistent-store.js";
import { autoEffect, batch, store, view } from "@risingstack/react-easy-state";
import {
  addAdvancedSettingsVisibility,
  addGroupFolding
} from "../../../../carrotsearch/ui/settings/Settings.js";
import { Button } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faBookSpells,
  faCompressAlt,
  faExpandAlt,
  faUndoAlt
} from "@fortawesome/pro-regular-svg-icons";
import { queryStore } from "../store/query-store.js";
import { ExportParameters } from "./ExportParameters.js";

// Settings of all sources and algorithms, combined. We'll show and hide
// the right settings based on the source and algorithm selection.
export const settings = {
  id: "root",
  settings: [
    { components: sources, type: "source" },
    { components: algorithms, type: "algorithm" }
  ]
    .map(t => {
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
      });
    })
    .flat(2)
};

// Folding state of setting groups.
const { isAllFolded, foldAll, expandAll } = addGroupFolding(
  settings.settings,
  "workbench:settings:folding"
);

// We'll track which parameters have been modified since the last clustering
// and not request documents from the source if only clustering parameters
// have changed.
export const parametersStateStore = store({
  sourceDirty: false,
  algorithmDirty: false
});

const collectSettings = components => {
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
};

// Monitor setting changes and set the dirty flags.
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

// Setting tools buttons
const settingsStateStore = persistentStore("workbench:settings:state", {
  showAdvancedSettings: false
});
const AdvancedSettingsButton = view(() => {
  return (
    <Button
      icon={<FontAwesomeIcon icon={faBookSpells} />}
      title="Show advanced settings"
      small={true}
      active={settingsStateStore.showAdvancedSettings}
      onClick={() =>
        (settingsStateStore.showAdvancedSettings = !settingsStateStore.showAdvancedSettings)
      }
    />
  );
});
const FoldSettingsButton = view(() => {
  const allFolded = isAllFolded();
  return (
    <Button
      icon={<FontAwesomeIcon icon={allFolded ? faExpandAlt : faCompressAlt} />}
      title={`${allFolded ? "Expand" : "Fold"} all setting groups`}
      small={true}
      onClick={() => (allFolded ? expandAll() : foldAll())}
    />
  );
});
export const SettingsTools = () => {
  return (
    <div className="SettingsTools">
      <Button
        icon={<FontAwesomeIcon icon={faUndoAlt} />}
        title="Reset all settings to defaults"
        small={true}
        onClick={algorithmStore.getAlgorithmInstance().resetToDefaults}
      />
      <AdvancedSettingsButton />
      <FoldSettingsButton />
      <ExportParameters />
    </div>
  );
};
addAdvancedSettingsVisibility(
  settings.settings,
  () => settingsStateStore.showAdvancedSettings
);

export const runSearch = () => {
  if (parametersStateStore.sourceDirty) {
    searchResultStore.load(
      workbenchSourceStore.source,
      sources[workbenchSourceStore.source],
      queryStore.query
    );
  } else {
    reloadClusters();
  }

  parametersStateStore.algorithmDirty = false;
  parametersStateStore.sourceDirty = false;
};
