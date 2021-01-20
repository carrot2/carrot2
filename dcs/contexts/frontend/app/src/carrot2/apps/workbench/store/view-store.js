import { persistentStore } from "../../../../carrotsearch/store/persistent-store.js";

export const workbenchViewStore = persistentStore("workbench:ui", {
  clusterView: "folders"
});
