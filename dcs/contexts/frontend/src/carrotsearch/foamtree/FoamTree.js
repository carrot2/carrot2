import React, { useEffect, useRef } from 'react';
import { CarrotSearchFoamTree } from "./foamtree-impl.js";
import PropTypes from 'prop-types';

FoamTree.propTypes = {
  options: PropTypes.object,
};

export function FoamTree(props) {
  const element = useRef(null);
  const foamtree = useRef(null);

  // Dispose on unmount
  useEffect(() => {
    if (foamtree.current) {
      foamtree.current.dispose();
      foamtree.current = undefined;
    }
  }, []);

  useEffect(() => {
    if (foamtree.current) {
      foamtree.current.set(props.options);
    } else {
      foamtree.current = new CarrotSearchFoamTree({
        element: element.current,
        pixelRatio: window.devicePixelRatio || 1,
        ...props.options
      });
    }
  }, [ props.options ]); // run once

  return (
    <div ref={element} style={{ position: "absolute", top: 0, bottom: 0, left: 0, right: 0 }}/>
  );
}
