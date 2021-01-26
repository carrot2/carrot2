import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";

// The initial value of the source is hardcoded. We could import all sources and take
// the first defined key, but this would create a circular dependency and cause
// some imports to be null. I don't know how to solve this for now.
export const workbenchSourceStore = persistentStore("workbench:source", {
  source: "web"
});
