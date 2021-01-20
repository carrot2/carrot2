import React from "react";
import "./BooleanSetting.css";

import { Checkbox, FormGroup } from "@blueprintjs/core";
import { view } from "@risingstack/react-easy-state";
import { DescriptionPopover } from "./Setting.js";

export const BooleanSetting = view(({ setting, get, set }) => {
  const { description } = setting;
  return (
    <FormGroup className="BooleanSetting Setting">
      <Checkbox
        checked={get(setting)}
        label={setting.label}
        inline={true}
        onChange={e => set(setting, e.target.checked)}
      />
      <DescriptionPopover description={description} />
    </FormGroup>
  );
});
