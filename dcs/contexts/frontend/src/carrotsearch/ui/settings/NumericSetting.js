import React, { useState } from 'react';
import PropTypes from 'prop-types';

import "./NumericSetting.css";

import { view } from "@risingstack/react-easy-state";

import { FormGroup, NumericInput, Slider } from "@blueprintjs/core";
import { Setting } from "./Settings.js";

import { ceil125, decimalPlaces } from "../../lang/math.js";

const clampWhenInteger = (v, integer) => integer ? Math.max(1, v) : v;

/**
 * A setting for spinner- and slider-based editing of a single numeric value.
 * This component applies a number of heuristics to make the experience smooth,
 * including automatic value step computation.
 */
export const NumericSetting = view(
    ({ label, description, value, onChange, min, max, integer, step }) => {
      const range = max - min;

      // Compute the value step to be a multiple of 1, 2 or 5.
      const inputStep = clampWhenInteger(ceil125(range / 100), integer);
      const sliderStep = clampWhenInteger(ceil125(range / 20), integer);

      // Compute the number of decimal places we need for display.
      const inputPrecision = decimalPlaces(inputStep);
      const boundsLabelPrecision = Math.max(decimalPlaces(min), decimalPlaces(max));
      const valueLabelPrecision = decimalPlaces(sliderStep);

      // Render bounds without decimal places, if possible.
      const sliderLabelRenderer = v => (v * 1.0).toFixed(
          v === min || v === max ? boundsLabelPrecision : valueLabelPrecision);

      // Render value in the input box with fewest decimal places possible.
      const formatInputValue = v => (v * 1.0).toFixed(Math.min(decimalPlaces(v), inputPrecision));

      // Internal storage of the string entered in the input box.
      // We cannot convert immediately to a number because it wouldn't be possible
      // to enter a decimal point or a minus sign.
      const [ stringValue, setStringValue ] = useState(formatInputValue(value));

      // Parse string into a float when the focus leaves the input box.
      const commitStringValue = () => onSliderValueChange(parseFloat(stringValue), 1);

      // Triggers the change for the parent component to react on.
      const onNumberValueChange = (v, step) => {
        // The slider component sometimes delivers values along the lines of 0.499999999.
        // We'll round this up to the required precision, removing the long tail.
        const m = Math.pow(10, inputPrecision);
        const vRounded = Math.round(v * m) / m;

        // For integer inputs, we align the successive slider values to me multiples of the
        // step value. For example, if the setting has a 2...100 range and step of 5, make the slider
        // produce values of 2, 5, 10, 15, ... rather than 2, 7, 12, 17.
        const toSet = integer ? Math.max(min, vRounded - (vRounded % step)) : vRounded;
        setStringValue(formatInputValue(toSet));
        onChange(toSet);
      };

      const onSpinnerValueChange = v => onNumberValueChange(v, inputStep);
      const onSliderValueChange = v => onNumberValueChange(v, sliderStep);

      const v = Math.max(Math.min(value, max), min);

      return (
          <Setting className="NumericSetting" label={label} description={description}>
            <FormGroup inline={true} fill={true} className="NumericSettingControls">
              <NumericInput onBlur={commitStringValue} onButtonClick={onSpinnerValueChange}
                            value={stringValue} onValueChange={(v, vs) => setStringValue(vs)} fill={false}
                            min={min} max={max} stepSize={inputStep} minorStepSize={inputStep}
                            clampValueOnBlur={true} />
              <Slider value={v} onChange={onSliderValueChange} fill={false}
                      min={min} max={max} stepSize={sliderStep} labelStepSize={range}
                      labelRenderer={sliderLabelRenderer} />
            </FormGroup>
          </Setting>
      );
    });

NumericSetting.propTypes = {
  label: PropTypes.string.isRequired,
  value: PropTypes.number.isRequired,
  onChange: PropTypes.func.isRequired
};