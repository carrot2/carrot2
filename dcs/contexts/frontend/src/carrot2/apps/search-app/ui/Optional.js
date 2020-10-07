import React from "react";

export const Optional = (props) => {
  return props.visible ? <>{props.content()}</> : null;
};

export const ShowHide = props => {
  return (
    <div style={props.visible ? {} : {display: "none"}} className={props.className}>{props.children}</div>
  )
};