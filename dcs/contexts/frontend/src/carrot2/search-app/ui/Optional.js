import React from "react";

export const Optional = (props) => {
  return props.visible ? <>{props.content()}</> : null;
};