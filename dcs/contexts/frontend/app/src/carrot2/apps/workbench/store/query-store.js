import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";

export const queryStore = persistentStore("workbench:query", {
  query: ""
});
