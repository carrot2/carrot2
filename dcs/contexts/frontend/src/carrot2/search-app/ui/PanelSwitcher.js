import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";

export function PanelSwitcher(props) {
  const panels = props.panels;
  return <React.Fragment>
    {
      panels.map(p => {
        return <Switch key={p.id} visible={p.id === props.visible}
                       createElement={p.createElement} />
      })
    }
  </React.Fragment>;
}

PanelSwitcher.propTypes = {
  panels: PropTypes.array.isRequired,
  visible: PropTypes.string.isRequired
};

function Switch(props) {
  const [ initialized, setInitialized ] = useState(false);
  useEffect(() => {
    setInitialized(true);
  }, []);

  if (!initialized) {
    return null;
  }

  return <div style={{display: props.visible ? "block" : "none"}}>
    { props.createElement() }
  </div>;
}

Switch.propTypes = {
  visible: PropTypes.bool.isRequired,
  createElement: PropTypes.func.isRequired
};