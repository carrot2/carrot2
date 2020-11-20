import React from "react";

import { view } from "@risingstack/react-easy-state";

import { Button } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUndoAlt, faBookSpells } from "@fortawesome/pro-regular-svg-icons";

import { algorithmStore } from "../../../store/services.js";
import { persistentStore } from "../../../../carrotsearch/store/persistent-store.js";
import * as PropTypes from "prop-types";

const resetAlgorithmSettings = () => {
  algorithmStore.getAlgorithmInstance().resetToDefaults();
};

export const settingsStateStore = persistentStore("workbench:settings:state", {
  showAdvancedSettings: false
});

const AdvancedSettingsButton = view(() => {
  return (
      <Button icon={<FontAwesomeIcon icon={faBookSpells} />}
              title="Show advanced settings"
              small={true}
              active={settingsStateStore.showAdvancedSettings}
              onClick={() => settingsStateStore.showAdvancedSettings = !settingsStateStore.showAdvancedSettings} />
  );
});

AdvancedSettingsButton.propTypes = { onClick: PropTypes.func };
export const SettingsTools = () => {
  return (
      <div className="SettingsTools">
        <Button icon={<FontAwesomeIcon icon={faUndoAlt} />} title="Reset all settings to defaults"
                small={true} onClick={resetAlgorithmSettings} />
        <AdvancedSettingsButton />
      </div>
  );
};