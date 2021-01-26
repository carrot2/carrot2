import React from "react";

import { Optional } from "@carrotsearch/ui/Optional.js";

export const TitleAndRank = props => {
  return (
    <strong>
      <Optional
        visible={props.showRank}
        content={() => <span>{props.rank}</span>}
      />
      {props.title}
    </strong>
  );
};

export const Url = props => {
  return (
    <span className="url">
      <span>{props.url}</span>
    </span>
  );
};
