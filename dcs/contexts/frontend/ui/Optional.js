import React from "react";

export const Optional = props => {
  return props.visible ? <>{props.content()}</> : null;
};

export const displayNoneIf = (condition, extra) => {
  const style = {};
  if (condition) {
    style.display = "none";
  }
  return Object.assign(style, extra);
};
