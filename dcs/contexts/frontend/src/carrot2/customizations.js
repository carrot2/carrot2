import mitt from "mitt";

const emitter = mitt();

export const triggerClusteringRequested = (sourceId, docCount) => {
  emitter.emit("clusteringRequested");
};

import ( /* webpackIgnore: true */ "/customizer.js").then(module => {
  module.default({
    on: emitter.on,
    off: emitter.off
  });
});
