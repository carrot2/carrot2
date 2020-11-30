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

import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;
import org.carrot2.util.ResourceLookup;

/**
 * {@link LexicalData} implemented on top of a hash set (stopwords) and a regular expression pattern
 * (stoplabels).
 */
public final class LexicalDataImpl implements LexicalData {
  private final Set<String> stopwords;
  private final Pattern stoplabelPattern;

  public LexicalDataImpl(Set<String> stopwords, Pattern stoplabelPattern) {
    this.stopwords = stopwords;
    this.stoplabelPattern = stoplabelPattern;
  }

  public LexicalDataImpl(ResourceLookup loader, String stopwordsResource, String stoplabelsResource)
      throws IOException {
    this(
        WordListFilter.loadFromPlainText(loader, stopwordsResource),
        RegExpLabelFilter.loadFromPlainText(loader, stoplabelsResource));
  }

  @Override
  public boolean ignoreWord(CharSequence word) {
    return stopwords.contains(word.toString());
  }

  @Override
  public boolean ignoreLabel(CharSequence label) {
    if (this.stoplabelPattern == null) {
      return false;
    } else {
      return stoplabelPattern.matcher(label).matches();
    }
  }
}
