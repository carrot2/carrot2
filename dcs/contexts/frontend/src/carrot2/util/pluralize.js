export function pluralize(count, what, includeCount = true) {
  return (includeCount ? count + " " : " ") + what + (count !== 1 ? "s" : "");
}