/**
 * The nearest multiple 1, 2, 5, 10,... largest than v.
 */
const multiples125 = [1, 2, 5, 10];
export const ceil125 = v => {
  if (v === 0) {
    return multiples125[0];
  }
  const mag = Math.floor(Math.log10(v));
  const vb = v / Math.pow(10, mag);
  for (let i = 0; i < multiples125.length; i++) {
    if (vb <= multiples125[i]) {
      return multiples125[i] * Math.pow(10, mag);
    }
  }
};

export const decimalPlaces = (v, max = 3) => {
  for (let i = 0; i < max; i++) {
    const mult = v * Math.pow(10, i);
    if (Math.floor(mult) === mult) {
      return i;
    }
  }
  return max;
};
