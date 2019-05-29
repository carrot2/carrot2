import React from "react";

export const TitleAndRank = (props) => {
  let rank = null;
  if (props.showRank) {
    rank = <span>{props.rank}</span>;
  }
  return <strong>{rank}{props.title}</strong>;
};

export const Url = (props) => {
  return <span className="url"><span>{props.url}</span></span>;
};