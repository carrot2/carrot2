import React from 'react';
import { FormGroup, Radio, RadioGroup } from "@blueprintjs/core";
import { view } from "@risingstack/react-easy-state";

export const RadioSetting = view(({ setting, get, set }) => {
  const { label, options } = setting;

  return (
      <FormGroup label={label} inline={true}>
        <RadioGroup onChange={e => set(setting, e.target.value)}
                    selectedValue={get(setting)}>
          {
            options.map(o => <Radio label={o.label || o.value} value={o.value} key={o.value}/>)
          }
        </RadioGroup>
      </FormGroup>
  );
});