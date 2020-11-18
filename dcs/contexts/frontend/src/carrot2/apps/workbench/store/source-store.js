import { persistentStore } from "../../../../carrotsearch/store/persistent-store.js";

export const workbenchSourceStore = persistentStore("workbench:source", {
  source: undefined
});