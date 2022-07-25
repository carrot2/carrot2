import React, { useEffect, useRef } from "react";

import debounce from "lodash.debounce";

import { autoEffect, batch, store, view } from "@risingstack/react-easy-state";

import { Button, InputGroup } from "@blueprintjs/core";

import { VscWand, VscFold, VscDiscard, VscFilter } from "react-icons/vsc";

import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";
import {
  addAdvancedSettingsVisibility,
  addGroupFolding,
  addSettingsSearch
} from "@carrotsearch/ui/settings/Settings.js";

import { sources } from "../../../sources.js";
import { algorithms } from "@carrot2/config/algorithms.js";
import { workbenchSourceStore } from "../store/source-store.js";
import {
  algorithmStore,
  reloadClusters,
  searchResultStore
} from "../../../store/services.js";

import { queryStore } from "../store/query-store.js";
import { ExportParameters } from "./ExportParameters.js";
import { displayNoneIf } from "@carrotsearch/ui/Optional.js";

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
export const settingsStateStore = persistentStore("workbench:settings:state", {
  showAdvancedSettings: false,
  showFilters: false,
  search: ""
});

export const debouncedSettingSearchStore = store({
  search: settingsStateStore.search,
  setSearch: debounce(
    search => (debouncedSettingSearchStore.search = search),
    250
  ),
  getSearch: () => {
    return settingsStateStore.showFilters
      ? debouncedSettingSearchStore.search
      : "";
  }
});
autoEffect(() => {
  debouncedSettingSearchStore.setSearch(settingsStateStore.search);
});

const AdvancedSettingsButton = view(() => {
  return (
    <Button
      icon={<VscWand />}
      title="Show advanced settings"
      small={true}
      minimal={true}
      active={settingsStateStore.showAdvancedSettings}
      onClick={() =>
        (settingsStateStore.showAdvancedSettings =
          !settingsStateStore.showAdvancedSettings)
      }
    />
  );
});
const FoldSettingsButton = view(() => {
  const allFolded = isAllFolded();
  return (
    <Button
      icon={allFolded ? <VscFold /> : <VscFold />}
      title={`${allFolded ? "Expand" : "Fold"} all setting groups`}
      small={true}
      minimal={true}
      onClick={() => (allFolded ? expandAll() : foldAll())}
    />
  );
});
const SettingFiltersButton = view(() => {
  return (
    <Button
      className="SettingFiltersButton"
      icon={<VscFilter />}
      small={true}
      minimal={true}
      active={settingsStateStore.showFilters}
      onClick={() =>
        (settingsStateStore.showFilters = !settingsStateStore.showFilters)
      }
    >
      Filters
    </Button>
  );
});

const SettingFilters = view(() => {
  const input = useRef();
  useEffect(() => {
    if (settingsStateStore.showFilters) {
      input.current.focus();
    }
  });

  return (
    <div
      className="SettingFilters"
      style={displayNoneIf(!settingsStateStore.showFilters)}
    >
      <InputGroup
        value={settingsStateStore.search}
        onChange={e => (settingsStateStore.search = e.target.value)}
        placeholder="parameter name"
        inputRef={input}
      />
    </div>
  );
});

export const SettingsTools = () => {
  return (
    <div className="SettingsTools">
      <div className="SettingsToolsButtons">
        <Button
          icon={<VscDiscard />}
          title="Reset all settings to defaults"
          small={true}
          minimal={true}
          onClick={algorithmStore.getAlgorithmInstance().resetToDefaults}
        />
        <AdvancedSettingsButton />
        <FoldSettingsButton />
        <ExportParameters />
        <SettingFiltersButton />
      </div>

      <SettingFilters />
    </div>
  );
};
addAdvancedSettingsVisibility(
  settings.settings,
  () => settingsStateStore.showAdvancedSettings
);
addSettingsSearch(settings.settings, () =>
  debouncedSettingSearchStore.getSearch()
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
