import { autoEffect, store } from "@risingstack/react-easy-state";
import storage from "store2";
import LRU from "lru-cache";

export const persistentStore = (key, defaults, methods) => {
  const backingStore = store(Object.assign(defaults, storage.get(key), methods));

  autoEffect(function () {
    storage.set(key, backingStore);
  });

  return backingStore;
};

export const persistentLruStore = (storeKey, itemKey, itemValue, maxItems = 1024) => {
  const items = new LRU({ max: maxItems });
  items.load(storage.get(storeKey) || []);

  autoEffect(() => {
    const value = itemValue();
    if (value) {
      items.set(itemKey(value), value);
      storage.set(storeKey, items.dump());
    }
  });
  return items;
};