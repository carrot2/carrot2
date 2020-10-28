import { persistentStore } from "../../../util/persistent-store.js";

export const queryStore = persistentStore("workbench:query", {
  query: ""
});