import React from 'react';
import PropTypes from 'prop-types';
import { InputGroup } from "@blueprintjs/core";
import { view } from "@risingstack/react-easy-state";
import { Setting } from "./Settings.js";

export const StringSetting = view(({ label, description, value, onChange }) => {
  return (
      <Setting className="StringSetting" label={label} description={description}>
        <InputGroup value={value} onChange={e => onChange(e.target.value)} />
      </Setting>
  );
});

StringSetting.propTypes = {
  label: PropTypes.string.isRequired,
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired
};