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
