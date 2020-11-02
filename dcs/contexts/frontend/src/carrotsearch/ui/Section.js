import React from 'react';

import "./Section.css";

import classnames from "classnames";

export const SectionDivider = ({ label }) => {
  if (!label) {
    return null;
  }

  return (
      <h4 className="SectionDivider">
        <span>{label}</span>
      </h4>
  );
};

export const Section = ({ label, children, className, style }) => {
  return (
      <section className={classnames("Section", className)} style={style}>
        <SectionDivider label={label} />
        <div>
          {children}
        </div>
      </section>
  );
};