import PropTypes from "prop-types";
import React from "react";

import "./LightDarkSwitch.css";

export function LightDarkSwitch(props) {
  return (
    <div className={`LightDarkSwitch ${props.className}`}>
      <input
        className="tgl tgl-ios"
        id="theme"
        type="checkbox"
        tabIndex="0"
        checked={props.dark}
        onChange={props.onChange}
      />
      <label
        className="tgl-btn"
        htmlFor="theme"
        title={`Switch to ${props.dark ? "light" : "dark"} theme`}
      />
    </div>
  );
}

LightDarkSwitch.propTypes = {
  dark: PropTypes.bool.isRequired,
  onChange: PropTypes.func.isRequired
};
