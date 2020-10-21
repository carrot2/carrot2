import React from 'react';
import PropTypes from 'prop-types';
import { FormGroup, Radio, RadioGroup } from "@blueprintjs/core";
import { view } from "@risingstack/react-easy-state";

export const RadioSetting = view(({ label, options, selected, onChange }) => {
  return (
      <FormGroup label={label} inline={true}>
        <RadioGroup onChange={e => onChange(e.target.value)}
                    selectedValue={selected}>
          {
            options.map(o => <Radio label={o.label || o.value} value={o.value} key={o.value}/>)
          }
        </RadioGroup>
      </FormGroup>
  );
});

RadioSetting.propTypes = {
  label: PropTypes.string.isRequired,
  options: PropTypes.array.isRequired,
  selected: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired
};