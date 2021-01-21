import React from "react";
import { Visualization } from "./Visualization.js";
import { Circles as CarrotSearchCircles } from "@carrotsearch/circles";
import PropTypes from "prop-types";

Circles.propTypes = {
  options: PropTypes.object,
  dataObject: PropTypes.object.isRequired,
  selection: PropTypes.array
};

const impl = {
  embed: options => new CarrotSearchCircles(options),
  set: (instance, ...rest) => {
    instance.set.apply(instance, rest);
  },
  select: (instance, selection) => {
    if (!selection) {
      return;
    }
    if (!selection.keepPrevious) {
      instance.set("selection", { all: true, selected: false });
    }
    instance.set("selection", {
      groups: selection.groups.map(g => g.id),
      selected: true
    });
  },
  resize: instance => instance.resize(),
  dispose: () => {} // no-op, Circles does not support disposing
};

export function Circles(props) {
  return <Visualization impl={impl} {...props} />;
}
