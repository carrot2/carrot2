import { persistentStore } from "../../../../carrotsearch/store/persistent-store.js";

export const queryStore = persistentStore("workbench:query", {
  query: ""
});