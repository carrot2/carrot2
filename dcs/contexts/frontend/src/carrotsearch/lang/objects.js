export const firstField = obj => {
  const keys = Object.keys(obj);
  return keys.length > 0 ? obj[keys[0]] : undefined;
};