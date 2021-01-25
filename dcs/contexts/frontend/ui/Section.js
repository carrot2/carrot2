import React from "react";

import "./Section.css";

import { view } from "@risingstack/react-easy-state";
import classnames from "classnames";
import { displayNoneIf } from "./Optional.js";

export const SectionDivider = ({ label, folded, onHeaderClick = () => {} }) => {
  if (!label) {
    return null;
  }

  return (
    <h4 className="SectionDivider">
      <span onClick={onHeaderClick}>
        {label}
        <span className={classnames("Caret", { CaretRight: folded })} />
      </span>
    </h4>
  );
};

export const Section = view(
  ({ label, children, className, style, folded, onHeaderClick }) => {
    const isFolded = folded ? folded() : false;
    return (
      <section className={classnames("Section", className)} style={style}>
        <SectionDivider
          label={label}
          folded={isFolded}
          onHeaderClick={onHeaderClick}
        />
        <div style={displayNoneIf(isFolded)}>{children}</div>
      </section>
    );
  }
);
