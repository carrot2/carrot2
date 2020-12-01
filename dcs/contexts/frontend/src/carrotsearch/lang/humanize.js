function maxDigits(number, digits) {
  const fixed = number.toFixed(digits - 1);
  return fixed
    .substring(0, Math.max(fixed.indexOf("."), digits + 1))
    .replace(/\.(\d*?)(0)+$/, ".$1") // trim trailing zeros
    .replace(/\.$/, ""); // trim trailing decimal point
}

export const humanizeDuration = function (milliseconds) {
  if (milliseconds === null || milliseconds === undefined) {
    return milliseconds;
  }

  if (isNaN(milliseconds) || !isFinite(milliseconds)) {
    return milliseconds;
  }

  // Below one second
  if (milliseconds < 1000) {
    return milliseconds + "ms";
  }

  // Below one minute
  if (milliseconds < 60 * 1000) {
    return maxDigits(milliseconds / 1000, 3) + "s";
  }

  // One minute or more
  const minutes = Math.floor(milliseconds / (60 * 1000));
  const seconds = Math.round((milliseconds - minutes * 60 * 1000) / 1000);
  return minutes + "m" + (seconds > 0 ? " " + seconds + "s" : "");
};

export function pluralize(count, what, includeCount = true) {
  return (includeCount ? count + " " : " ") + what + (count !== 1 ? "s" : "");
}

export function finishingPeriod(string) {
  return string + (string.endsWith(".") ? "" : ".");
}
