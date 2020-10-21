import React from 'react';
import PropTypes from 'prop-types';
import { FormGroup, NumericInput, Slider } from "@blueprintjs/core";
import { view } from "@risingstack/react-easy-state";

import "./NumericSetting.css";
import { Setting } from "./Settings.js";

export const NumericSetting = view(({ label, description, value, onChange, min, max, step }) => {
  const labelStepSize = max - min;
  const v = Math.max(Math.min(value, max), min);
  return (
      <Setting className="NumericSetting" label={label} description={description}>
        <FormGroup inline={true} fill={true} className="NumericSettingControls">
          <NumericInput value={value} onValueChange={v => onChange(v)} fill={false}
                        min={min} max={max} stepSize={step} clampValueOnBlur={true} />
          <Slider value={v} onChange={v => onChange(v)} fill={false}
                  min={min} max={max} stepSize={step} labelStepSize={labelStepSize} />
        </FormGroup>
      </Setting>
  );
});

NumericSetting.propTypes = {
  label: PropTypes.string.isRequired,
  value: PropTypes.number.isRequired,
  onChange: PropTypes.func.isRequired
};