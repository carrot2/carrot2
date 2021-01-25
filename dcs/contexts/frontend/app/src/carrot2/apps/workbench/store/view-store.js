import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";

export const workbenchViewStore = persistentStore("workbench:ui", {
  clusterView: "folders"
});
