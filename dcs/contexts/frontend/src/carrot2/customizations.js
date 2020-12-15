import mitt from "mitt";
import { routes } from "./routes.js";
import { workbenchViewStore } from "./apps/workbench/store/view-store.js";

const emitter = mitt();

export const triggerClusteringRequested = (sourceId, docCount) => {
  const event = {
    source: sourceId,
    docs: docCount
  };

  // Extract the current active application and view.
  const hash = window.location.hash.substring(1);
  let match;
  if ((match = routes.workbench.match(hash))) {
    event.app = "workbench";
    event.view = workbenchViewStore.clusterView;
  } else if ((match = routes.searchResults.match(hash))) {
    event.app = "search";
    event.view = match.params.view;
  }

  emitter.emit("clusteringRequested", event);
};

// Load customizations at startup from a known URL.
import(
  /* webpackIgnore: true */ new URL("customizer.js", window.location).toString()
).then(module => {
  // Execute the customization layer.
  module.default({
    on: emitter.on,
    off: emitter.off
  });
});
