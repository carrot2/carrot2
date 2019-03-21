import { store } from 'react-easy-state';
import { observe } from '@nx-js/observer-util';
import storage from "store2";


export function persistentStore(key, defaults, methods) {
  const backingStore = store(Object.assign(defaults, storage.get(key), methods));

  observe(function () {
    storage.set(key, backingStore);
  });

  return backingStore;
}