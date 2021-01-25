export const mapUpToMaxLength = (
  array,
  maxChars,
  toResult,
  toText = a => a
) => {
  if (maxChars === 0) {
    return;
  }

  let contentCharsOutput = 0;
  return array
    .map((element, index) => {
      let text;

      // Allow some reasonable number of characters for a new paragraph, hence the +80.
      if (contentCharsOutput + 80 >= maxChars) {
        return null;
      }

      const elementText = toText(element);
      if (!elementText) {
        console.log("null", element);
        return null;
      }
      if (contentCharsOutput + elementText.length < maxChars) {
        text = elementText;
      } else {
        text =
          elementText.substring(0, maxChars - contentCharsOutput) + "\u2026";
      }
      contentCharsOutput += text.length;

      return toResult(text, element, index);
    })
    .filter(e => e !== null);
};

export const wrapIfNotArray = element => {
  return Array.isArray(element) ? element : [element];
};

export const equals = (a1, a2) => {
  return (
    Array.isArray(a1) &&
    Array.isArray(a2) &&
    a1.length === a2.length &&
    a1.every((v, i) => v === a2[i])
  );
};
