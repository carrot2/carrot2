import React from 'react';
import "./ButtonLink.css";
import classnames from "classnames";

export const ButtonLink = ({ className = "", children, ...rest }) => {
  return <button {...rest} className={classnames("link", className)}>{children}</button>;
};