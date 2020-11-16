import React from "react";

import { Button } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUndoAlt } from "@fortawesome/pro-regular-svg-icons";

import { algorithmStore } from "../../../store/services.js";

const resetAlgorithmSettings = () => {
  algorithmStore.getAlgorithmInstance().resetToDefaults();
};

export const SettingsTools = () => {
  return (
      <div className="SettingsTools">
        <Button icon={<FontAwesomeIcon icon={faUndoAlt} />} title="Reset all settings to defaults"
                outlined={true} small={true} onClick={resetAlgorithmSettings} />
      </div>
  );
};