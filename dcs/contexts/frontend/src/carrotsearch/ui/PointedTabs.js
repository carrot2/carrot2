import React from 'react';

import { Tabs } from "@blueprintjs/core";

import "./PointedTabs.css";

export const PointedTabs = ({ children, ...props }) => {
  return (
      <Tabs className="PointedTabs" {...props}>{children}</Tabs>
  );
};