import React from 'react';
import PropTypes from 'prop-types';
import { Checkbox, FormGroup } from "@blueprintjs/core";
import { view } from "@risingstack/react-easy-state";

export const BooleanSetting = view(({ label, checked, onChange }) => {
  return (
      <FormGroup className="BooleanSetting Setting" label=" " inline={true}>
        <Checkbox checked={checked} label={label} onChange={e => onChange(e.target.checked)} />
      </FormGroup>
  );
});

BooleanSetting.propTypes = {
  label: PropTypes.string.isRequired,
  checked: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired
};