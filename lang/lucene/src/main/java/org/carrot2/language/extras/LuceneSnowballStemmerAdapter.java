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
import org.tartarus.snowball.SnowballStemmer;

/** An adapter converting Snowball programs into {@link Stemmer} interface. */
public final class LuceneSnowballStemmerAdapter implements Stemmer {
  private final SnowballStemmer s;

  public LuceneSnowballStemmerAdapter(SnowballStemmer s) {
    this.s = s;
  }

  public CharSequence stem(CharSequence word) {
    final int len = word.length();
    char[] buffer = s.getCurrentBuffer();
    if (buffer.length < len) buffer = new char[len];

    for (int i = word.length(); --i >= 0; ) buffer[i] = word.charAt(i);
    s.setCurrent(buffer, len);

    if (s.stem()) {
      return new MutableCharArray(Arrays.copyOf(s.getCurrentBuffer(), s.getCurrentBufferLength()));
    } else {
      return null;
    }
  }
}
