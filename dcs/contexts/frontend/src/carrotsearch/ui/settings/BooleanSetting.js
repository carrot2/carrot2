import React from 'react';
import { Checkbox, FormGroup } from "@blueprintjs/core";
import { view } from "@risingstack/react-easy-state";

export const BooleanSetting = view(({ setting, get, set }) => {
  return (
      <FormGroup className="BooleanSetting Setting" label=" " inline={true}>
        <Checkbox checked={get(setting)} label={setting.label} onChange={e => set(setting, e.target.checked)} />
      </FormGroup>
  );
});