import { useState, useEffect, createElement } from "react";
import PropTypes from "prop-types";

export const Lazy = props => {
  const [component, setComponent] = useState(null);

  const loader = props.loader;
  useEffect(() => {
    loader().then(c => setComponent(c));
  }, [loader]);

  return component ? createElement(component, props.props) : null;
};

Lazy.propTypes = {
  loader: PropTypes.func.isRequired
};
