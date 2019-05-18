import React from 'react';
import { Visualization } from "../Visualization.js";
import { CarrotSearchFoamTree } from "./foamtree-impl.js";
import PropTypes from 'prop-types';

FoamTree.propTypes = {
  options: PropTypes.object,
  dataObject: PropTypes.object.isRequired,
  selection: PropTypes.array
};

const impl = {
  embed: (options) => new CarrotSearchFoamTree(options),
  set: (instance, ...rest) => {
    instance.set.apply(instance, rest);
    if (!rest.dataObject && rest[0] !== "dataObject") {
      instance.redraw();
    }
  },
  select: (instance, selection) => {
    if (!selection) {
      return;
    }
    instance.select({ groups: selection, keepPrevious: false });
  },
  resize: (instance) => instance.resize(),
  dispose: (instance) => instance.dispose()
};

export function FoamTree(props) {
  return (
    <Visualization impl={impl} {...props} />
  );
}
