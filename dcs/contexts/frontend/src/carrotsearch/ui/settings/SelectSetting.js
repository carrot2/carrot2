import React from 'react';
import { HTMLSelect } from "@blueprintjs/core";
import { view } from "@risingstack/react-easy-state";
import { Setting } from "./Setting.js";

export const SelectSetting = view(({ setting, get, set }) => {
  const { label, options, description } = setting;

  const opts = typeof options === "function" ? options() : options;
  return (
      <Setting className="SelectSetting" label={label} description={description}>
        <HTMLSelect onChange={e => set(setting, e.target.value)}
                    value={get(setting)} fill={true}>
          {
            opts.map(o => <option value={o.value} key={o.value} title={o.description}>{o.label || o.value}</option>)
          }
        </HTMLSelect>
      </Setting>
  );
});