import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";

export function Switcher(props) {
  const panels = props.panels;
  return (
    <>
      {panels.map(p => {
        return (
          <Switch
            key={p.id}
            visible={p.isVisible(props.visible, p)}
            createElement={p.createElement}
          />
        );
      })}
    </>
  );
}

Switcher.propTypes = {
  panels: PropTypes.array.isRequired,
  visible: PropTypes.string.isRequired
};

function Switch(props) {
  const [initialized, setInitialized] = useState(false);
  useEffect(() => {
    if (props.visible) {
      setInitialized(true);
    }
  }, [props.visible]);

  if (!initialized) {
    return null;
  }

  return (
    <div
      style={{
        display: props.visible ? "block" : "none",
        position: "relative"
      }}
    >
      {props.createElement(props.visible)}
    </div>
  );
}

Switch.propTypes = {
  visible: PropTypes.bool.isRequired,
  createElement: PropTypes.func.isRequired
};
