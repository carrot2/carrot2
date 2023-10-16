/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2023, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language.extras;

import java.util.Arrays;
import org.carrot2.language.Stemmer;
import org.carrot2.util.MutableCharArray;

final class LuceneStemmerAdapter implements Stemmer {
  public interface StemmingFunction {
    int apply(char[] buffer, int length);
  }

  private final int extraBufferPadding;
  private final StemmingFunction stemmer;
  private char[] buffer = new char[128];

  LuceneStemmerAdapter(StemmingFunction stemmer, int extraBufferPadding) {
    this.stemmer = stemmer;
    this.extraBufferPadding = extraBufferPadding;
  }

  LuceneStemmerAdapter(StemmingFunction stemmer) {
    this(stemmer, 0);
  }

  @Override
  public CharSequence stem(CharSequence word) {
    if (word.length() + extraBufferPadding > buffer.length) {
      buffer = new char[word.length() + extraBufferPadding];
    }

    for (int i = 0, max = word.length(); i < max; i++) {
      buffer[i] = word.charAt(i);
    }

    int newLen = stemmer.apply(buffer, word.length());

    if (newLen != word.length() || !equals(buffer, newLen, word)) {
      return new MutableCharArray(Arrays.copyOf(buffer, newLen));
    } else {
      return null;
    }
  }

  private boolean equals(char[] buffer, int len, CharSequence word) {
    assert len == word.length();

    for (int i = 0; i < len; i++) {
      if (buffer[i] != word.charAt(i)) {
        return false;
      }
    }

    return true;
  }
}
