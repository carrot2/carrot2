import React from "react";
import PropTypes from "prop-types";

import { Group } from "./Group.js";

export { addFactory } from "./Group.js";

export const Settings = ({ settings, get, set }) => (
  <Group className="Settings" setting={settings} set={set} get={get} />
);

Settings.propTypes = {
  settings: PropTypes.object.isRequired
};

const forEachSetting = (settings, cb) => {
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
