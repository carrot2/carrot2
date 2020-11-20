import React from 'react';

import "./Section.css";

import { view } from "@risingstack/react-easy-state";
import classnames from "classnames";
import { displayNoneIf } from "../../carrot2/apps/search-app/ui/Optional.js";

export const SectionDivider = ({ label, folded, onCaretClick = () => {} }) => {
  if (!label) {
    return null;
  }

  return (
      <h4 className="SectionDivider">
        <span onClick={onCaretClick}>{label}<span className={classnames("Caret", { "CaretRight": folded })} /></span>
      </h4>
  );
};

export const Section = view(({ label, children, className, style, isFolded, onCaretClick }) => {
  const folded = isFolded ? isFolded() : false;
  return (
      <section className={classnames("Section", className)} style={style}>
        <SectionDivider label={label} folded={folded}
                        onCaretClick={onCaretClick} />
        <div style={displayNoneIf(folded)}>
          {children}
        </div>
      </section>
  );
});