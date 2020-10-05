import { store, autoEffect } from "@risingstack/react-easy-state";
import storage from "store2";


export const persistentStore = (key, defaults, methods) => {
  const backingStore = store(Object.assign(defaults, storage.get(key), methods));

  autoEffect(function () {
    storage.set(key, backingStore);
  });

  return backingStore;
};