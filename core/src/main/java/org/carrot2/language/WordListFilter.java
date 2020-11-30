/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2020, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrStringArray;
import org.carrot2.util.ResourceLookup;

public class WordListFilter extends AttrComposite implements WordFilterAttr {
  public AttrStringArray entries =
      attributes.register(
          "entries",
          AttrStringArray.builder()
              .label("Words to filter out from the input")
              .defaultValue(new String[] {}));

  public WordListFilter() {}

  public WordListFilter(String... entries) {
    this.entries.set(entries);
  }

  @Override
  public WordFilter get() {
    HashSet<String> entries = new HashSet<>(Arrays.asList(this.entries.get()));
    return (word) -> entries.contains(word.toString());
  }

  static HashSet<String> loadFromPlainText(ResourceLookup loader, String stopwordsResource)
      throws IOException {
    HashSet<String> stopwords = new HashSet<>();
    try (InputStream is = loader.open(stopwordsResource);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      RegExpLabelFilter.readLines(reader)
          .forEach(word -> stopwords.add(word.toLowerCase(Locale.ROOT)));
    }
    return stopwords;
  }
}
