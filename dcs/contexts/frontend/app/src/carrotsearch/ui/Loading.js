import React from "react";

import "./Loading.css";
import "three-dots/dist/three-dots.css";

import { view } from "@risingstack/react-easy-state";
import classnames from "classnames";

export const Loading = view(({ isLoading }) => {
  return (
    <div className={classnames("Loading", { visible: isLoading() })}>
      <div className="dot-bricks" />
    </div>
  );
});
