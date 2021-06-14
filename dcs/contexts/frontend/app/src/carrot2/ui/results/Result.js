import React from "react";

import xss from "xss";

import { Optional } from "@carrotsearch/ui/Optional.js";

export const TitleAndRank = props => {
  return (
    <strong>
      <Optional
        visible={props.showRank}
        content={() => <span>{props.rank}</span>}
      />
      <SanitizedHtml text={props.title} />
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

const xssOptions = {
  whiteList: {
    b: ["class"]
  }
};

export const SanitizedHtml = ({ text }) => {
  if (!text) {
    return null;
  }
  return (
    <span
      dangerouslySetInnerHTML={{
        __html: xss(text, xssOptions)
      }}
    />
  );
};
