import React, { useEffect, useRef } from 'react';
import PropTypes from 'prop-types';

Visualization.propTypes = {
  impl: PropTypes.object.isRequired,
  implRef: PropTypes.object,
  options: PropTypes.object,
  dataObject: PropTypes.object.isRequired,
  selection: PropTypes.array
};

/**
 * Implementation base for Circles and FoamTree.
 */
export function Visualization(props) {
  const element = useRef(null);
  const instance = useRef(null);

  // Dispose on unmount
  useEffect(() => {
    if (instance.current) {
      props.impl.dispose(instance.current);
      instance.current = undefined;
    }
  }, [ props.impl ]);

  useEffect(() => {
    if (instance.current) {
      props.impl.set(instance.current, props.options);
    } else {
      instance.current = props.impl.embed({
        element: element.current,
        pixelRatio: window.devicePixelRatio || 1,
        ...props.options
      });
      if (props.implRef) {
        props.implRef.current = instance.current;
      }
    }
  }, [ props.options, props.impl, props.implRef ]);

  useEffect(() => {
    if (instance.current) {
      props.impl.set(instance.current, "dataObject", props.dataObject);
    }
  }, [ props.dataObject, props.impl ]);

  useEffect(() => {
    if (instance.current) {
      props.impl.select(instance.current, { groups: props.selection, keepPrevious: false });
    }
  }, [ props.selection, props.impl ]);

  useEffect(() => {
    let timeout;
    const onResize = () => {
      window.clearTimeout(timeout);
      timeout = window.setTimeout(() => {
        props.impl.resize(instance.current);
      }, 300);
    };

    window.addEventListener("resize", onResize);
    return () => window.removeEventListener("resize", onResize);
  }, [ props.impl ]);

  return (
    <div ref={element} style={{ position: "absolute", top: 0, bottom: 0, left: 0, right: 0 }}/>
  );
}
