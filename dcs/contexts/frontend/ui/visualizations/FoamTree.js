import React from "react";
import { Visualization } from "./Visualization.js";
import { FoamTree as CarrotSearchFoamTree } from "@carrotsearch/foamtree";
import PropTypes from "prop-types";

FoamTree.propTypes = {
  options: PropTypes.object,
  dataObject: PropTypes.object.isRequired,
  selection: PropTypes.array
};

const impl = {
  embed: options => new CarrotSearchFoamTree(options),
  set: (instance, ...rest) => {
    const reloadNeeded = willChange("layout") || willChange("stacking");

    instance.set.apply(instance, rest);
    if (!rest.dataObject && rest[0] !== "dataObject") {
      if (reloadNeeded) {
        const selection = instance.get("selection");
        const fadeDuration = instance.get("fadeDuration");
        instance.set({
          dataObject: instance.get("dataObject"),
          fadeDuration: 0
        });
        instance.select(selection);
        instance.set("fadeDuration", fadeDuration);
      } else {
        instance.redraw();
      }
    }

    function willChange(option) {
      return instance.get(option) !== rest[0][option];
    }
  },
  select: (instance, selection) => {
    if (!selection) {
      return;
    }
    instance.select({ groups: selection, keepPrevious: false });
  },
  resize: instance => instance.resize(),
  dispose: instance => instance.dispose()
};

export function FoamTree(props) {
  return <Visualization impl={impl} {...props} />;
}
