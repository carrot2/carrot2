import React from 'react';

import "./RadioSetting.css";

import { Radio, RadioGroup } from "@blueprintjs/core";
import { view } from "@risingstack/react-easy-state";
import { LabelWithHelp, Setting } from "./Settings.js";

export const RadioSetting = view(({ setting, get, set }) => {
  const { label, description, options } = setting;

  return (
      <Setting className="RadioSetting" label={label} description={description}>
        <RadioGroup onChange={e => set(setting, e.target.value)}
                    selectedValue={get(setting)}>
          {
            options.map(o => (
                <Radio value={o.value} key={o.value}>
                  <LabelWithHelp label={o.label || o.value} description={o.description} />
                </Radio>
            ))
          }
        </RadioGroup>
      </Setting>
  );
});