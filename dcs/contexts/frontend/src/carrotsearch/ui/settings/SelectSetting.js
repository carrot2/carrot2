import React from "react";

import "./SelectSetting.css";

import { HTMLSelect } from "@blueprintjs/core";
import { view } from "@risingstack/react-easy-state";
import { Setting } from "./Setting.js";

export const SelectSetting = view(({ setting, get, set }) => {
  const {
    label,
    options,
    description,
    noOptionsMessage = "No options to choose from."
  } = setting;

  const opts = typeof options === "function" ? options() : options;
  const content =
    opts && opts.length > 0 ? (
      <HTMLSelect
        onChange={e => set(setting, e.target.value)}
        value={get(setting)}
        fill={true}
      >
        {opts.map(o => (
          <option value={o.value} key={o.value} title={o.description}>
            {o.label || o.value}
          </option>
        ))}
      </HTMLSelect>
    ) : (
      <small>{noOptionsMessage}</small>
    );

  return (
    <Setting className="SelectSetting" label={label} description={description}>
      {content}
    </Setting>
  );
});
