import React from "react";
import "./ButtonLink.css";
import classnames from "classnames";

export const ButtonLink = ({
  className = "",
  children,
  enabled = true,
  ...rest
}) => {
  return (
    <button
      {...rest}
      disabled={!enabled}
      className={classnames("link", className)}
    >
      {children}
    </button>
  );
};
