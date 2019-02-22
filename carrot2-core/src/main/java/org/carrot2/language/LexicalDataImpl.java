
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.language;

import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * {@link LexicalData} implemented on top of a hash set (stopwords) and a regular
 * expression pattern (stoplabels).
 */
public final class LexicalDataImpl implements LexicalData {
  private final HashSet<String> stopwords;
  private final Pattern stoplabelPattern;
  private final boolean usesSpaceDelimiters;

  public LexicalDataImpl(HashSet<String> stopwords,
                         Pattern stoplabelPattern,
                         boolean usesSpaceDelimiters) {
    this.stopwords = stopwords;
    this.stoplabelPattern = stoplabelPattern;
    this.usesSpaceDelimiters = usesSpaceDelimiters;
  }

  /*
   *
   */
  @Override
  public boolean ignoreWord(CharSequence word) {
    return stopwords.contains(word.toString());
  }

  /*
   *
   */
  @Override
  public boolean ignoreLabel(CharSequence label) {
    if (this.stoplabelPattern == null)
      return false;

    return stoplabelPattern.matcher(label).matches();
  }

  @Override
  public boolean usesSpaceDelimiters() {
    return usesSpaceDelimiters;
  }
}
