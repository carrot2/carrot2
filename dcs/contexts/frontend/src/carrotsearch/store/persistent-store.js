import { autoEffect, store } from "@risingstack/react-easy-state";
import storage from "store2";
import LRU from "lru-cache";

export const persistentStore = (key, defaults, methods) => {
  const backingStore = store(
    Object.assign({}, defaults, storage.get(key), methods)
  );

  autoEffect(function () {
    storage.set(key, backingStore);
  });

  backingStore.resetToDefaults = () => {
    // Assume a flat object for now
    Object.keys(defaults).forEach(prop => {
      backingStore[prop] = defaults[prop];
    });
  };
  backingStore.getDefaults = () => defaults;

  return backingStore;
};

export const persistentLruStore = (
  storeKey,
  itemKey,
  itemValue,
  maxItems = 1024
) => {
  const items = new LRU({ max: maxItems });
  items.load(storage.get(storeKey) || []);

  autoEffect(() => {
    const value = itemValue();
    if (value) {
      const computedKey = itemKey(value);
      if (computedKey) {
        items.set(computedKey, value);
        storage.set(storeKey, items.dump());
      }
    }
  });
  return items;
};
