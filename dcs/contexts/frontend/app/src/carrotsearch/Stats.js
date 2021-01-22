import React from "react";

import "./Stats.css";

export const Stat = ({ value, label }) => {
  return (
    <div className="Stat">
      <strong>{value}</strong>
      <small>{label}</small>
    </div>
  );
};

export const Stats = ({ stats }) => {
  return (
    <div className="Stats">
      {stats
        .filter(s => s.value !== undefined)
        .map(s => (
          <Stat key={s.id} {...s} />
        ))}
    </div>
  );
};
