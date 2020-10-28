import React from "react";

import "./Loading.css";
import "three-dots/dist/three-dots.css";

import { view } from "@risingstack/react-easy-state";
import classnames from "classnames";

export const Loading = view(({ store }) => {
  return (
      <div className={classnames("Loading", { "visible" : store.loading })}>
        <div className="dot-bricks" />
      </div>
  );
});
