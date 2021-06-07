// Hardcoded for now, this will be generated during build ultimately.
const availableLanguages = {
  algorithms: {
    "Bisecting K-Means": [
      "Arabic",
      "Armenian",
      "Brazilian",
      "Bulgarian",
      "Croatian",
      "Czech",
      "Danish",
      "Dutch",
      "English",
      "Estonian",
      "Finnish",
      "French",
      "Galician",
      "German",
      "Greek",
      "Hindi",
      "Hungarian",
      "Indonesian",
      "Irish",
      "Italian",
      "Latvian",
      "Lithuanian",
      "Norwegian",
      "Polish",
      "Portuguese",
      "Romanian",
      "Russian",
      "Spanish",
      "Swedish",
      "Thai",
      "Turkish"
    ],
    Lingo: [
      "Arabic",
      "Armenian",
      "Brazilian",
      "Bulgarian",
      "Croatian",
      "Czech",
      "Danish",
      "Dutch",
      "English",
      "Estonian",
      "Finnish",
      "French",
      "Galician",
      "German",
      "Greek",
      "Hindi",
      "Hungarian",
      "Indonesian",
      "Irish",
      "Italian",
      "Latvian",
      "Lithuanian",
      "Norwegian",
      "Polish",
      "Portuguese",
      "Romanian",
      "Russian",
      "Spanish",
      "Swedish",
      "Thai",
      "Turkish"
    ],
    STC: [
      "Arabic",
      "Armenian",
      "Brazilian",
      "Bulgarian",
      "Croatian",
      "Czech",
      "Danish",
      "Dutch",
      "English",
      "Estonian",
      "Finnish",
      "French",
      "Galician",
      "German",
      "Greek",
      "Hindi",
      "Hungarian",
      "Indonesian",
      "Irish",
      "Italian",
      "Latvian",
      "Lithuanian",
      "Norwegian",
      "Polish",
      "Portuguese",
      "Romanian",
      "Russian",
      "Spanish",
      "Swedish",
      "Thai",
      "Turkish"
    ]
  }
};

export const createLanguageSetting = (id, algorithm, overrides, langs) => {
  const languages = langs || availableLanguages.algorithms[algorithm];

  return {
    id: `${id}:language`,
    type: "enum",
    ui: "select",
    label: "Language",
    description: `
<p>
  The language in which to perform clustering. Set it
  to the language in which the majority of input documents is written.
</p>`,
    options: languages.map(l => ({ value: l })),
    ...overrides
  };
};
