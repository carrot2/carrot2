import React from "react";

import { Tabs } from "@blueprintjs/core";
import classnames from "classnames";

import "./PointedTabs.css";

export const PointedTabs = ({ children, ...props }) => {
  return (
    <Tabs {...props} className={classnames("PointedTabs", props.className)}>
      {children}
    </Tabs>
  );
};
