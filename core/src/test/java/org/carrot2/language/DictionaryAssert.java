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

import java.util.function.Predicate;
import org.assertj.core.api.Assertions;

class DictionaryAssert<T extends Predicate<CharSequence>> {
  private final T dictionary;

  private DictionaryAssert(T dictionary) {
    this.dictionary = dictionary;
  }

  public static <T extends Predicate<CharSequence>> DictionaryAssert<T> assertThat(T dictionary) {
    return new DictionaryAssert<>(dictionary);
  }

  public DictionaryAssert<T> matchesAll(String... entries) {
    for (String entry : entries) {
      Assertions.assertThat(dictionary.test(entry))
          .as("Dictionary matches '" + entry + "'")
          .isTrue();
    }
    return this;
  }

  public DictionaryAssert<T> doesNotMatchAny(String... entries) {
    for (String entry : entries) {
      Assertions.assertThat(dictionary.test(entry))
          .as("Dictionary does not match '" + entry + "'")
          .isFalse();
    }
    return this;
  }

  public T getDictionary() {
    return dictionary;
  }
}
