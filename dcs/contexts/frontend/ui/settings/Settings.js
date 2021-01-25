import React from "react";
import PropTypes from "prop-types";

import { DeferredGroups, Group } from "./Group.js";
import { isEmpty } from "../lang/objects.js";
import { persistentStore } from "../store/persistent-store.js";

export { addFactory } from "./Group.js";

export const Settings = ({ settings, get, set, defer = false, timeout }) =>
  defer ? (
    <DeferredGroups
      className="Settings"
      setting={settings}
      set={set}
      get={get}
      timeout={timeout}
    />
  ) : (
    <Group className="Settings" setting={settings} set={set} get={get} />
  );

Settings.propTypes = {
  settings: PropTypes.object.isRequired
};

export const forEachSetting = (settings, cb) => {
  settings.forEach(s => {
    cb(s);
    if (s.type === "group") {
      forEachSetting(s.settings, cb);
    }
  });
};

/**
 * Redefines the "visible" property of the provided {@param settings} to check if the
 * setting has an "advanced" property and hide the setting if {@param isAdvancedVisible}
 * return true. If a setting already had the "visible" property defined, it will be
 * taken in conjunction with the advanced visibility flag.
 */
export const addAdvancedSettingsVisibility = (settings, isAdvancedVisible) => {
  forEachSetting(settings, s => {
    if (s.advanced) {
      const visibilityFn = s.visible;
      s.visible = () => {
        return (
          (s.advanced || false) === isAdvancedVisible() &&
          (!visibilityFn || visibilityFn())
        );
      };
    }
  });
};

export const addGroupFolding = (settings, key) => {
  const foldingStore = persistentStore(key, {});
  const isGroupFolded = s => foldingStore[s.id];
  const setGroupFolded = (s, folded) => (foldingStore[s.id] = folded);

  const foldableGroups = [];
  forEachSetting(settings, s => {
    if (s.type === "group" && !isEmpty(s.label)) {
      foldableGroups.push(s);
      s.folded = () => {
        return isGroupFolded(s);
      };
      s.onHeaderClick = () => {
        setGroupFolded(s, !isGroupFolded(s));
      };
    }
  });

  const isAllFolded = () => foldableGroups.every(g => foldingStore[g.id]);
  const setFolding = folded => {
    foldableGroups.forEach(g => {
      foldingStore[g.id] = folded;
    });
  };
  const foldAll = () => setFolding(true);
  const expandAll = () => setFolding(false);

  return { isAllFolded, foldAll, expandAll };
};
