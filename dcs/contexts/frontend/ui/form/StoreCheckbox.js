import React from "react";

import { view } from "@risingstack/react-easy-state";
import { Checkbox } from "@blueprintjs/core";

export const StoreCheckbox = view(({ store, property, ...props }) => {
  return (
    <Checkbox
      {...props}
      checked={store[property]}
      onChange={e => (store[property] = e.target.checked)}
    />
  );
});
