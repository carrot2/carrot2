import React from "react";

import "./LogEntries.css";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faExclamationTriangle,
  faInfoSquare
} from "@fortawesome/pro-regular-svg-icons";
import { view } from "@risingstack/react-easy-state";

export const ArrayLogger = function () {
  const entries = [];

  this.log = message => entries.push({ level: "info", message: message });
  this.warn = message => entries.push({ level: "warning", message: message });
  this.error = message => entries.push({ level: "error", message: message });
  this.getEntries = () => entries.slice(0);
};

const LEVEL_ICONS = {
  error: faExclamationTriangle,
  warning: faExclamationTriangle,
  info: faInfoSquare
};

export const LogEntry = ({ entry }) => {
  const { level, message } = entry;
  return (
    <div className={`LogEntry LogEntry-${level}`}>
      <FontAwesomeIcon icon={LEVEL_ICONS[level]} /> {message}
    </div>
  );
};

export const LogEntries = view(({ entries }) => {
  return (
    <>
      {entries.map((e, i) => (
        <LogEntry entry={e} key={i} />
      ))}
    </>
  );
});
