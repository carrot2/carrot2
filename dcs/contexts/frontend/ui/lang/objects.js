export const firstField = obj => {
  const keys = Object.keys(obj);
  return keys.length > 0 ? obj[keys[0]] : undefined;
};

export const forEachOwnProp = (obj, cb) => {
  for (const prop in obj) {
    if (obj.hasOwnProperty(prop)) {
      cb(obj[prop], prop);
    }
  }
};

export const incrementInMap = (map, key, diff) => {
  if (map.has(key)) {
    map.set(key, map.get(key) + diff);
  } else {
    map.set(key, diff);
  }
};

export const isEmpty = obj => {
  return (
    obj === undefined || obj === null || (typeof obj === "string" && obj === "")
  );
};
