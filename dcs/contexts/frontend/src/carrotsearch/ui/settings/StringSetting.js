import React from 'react';

import { view } from "@risingstack/react-easy-state";

import { InputGroup } from "@blueprintjs/core";
import { Setting } from "./Settings.js";

export const StringSetting = view(({ setting, get, set }) => {
  const { label, description } = setting;

  return (
      <Setting className="StringSetting" label={label} description={description}>
        <InputGroup value={get(setting)} onChange={e => set(setting, e.target.value)} />
      </Setting>
  );
});
