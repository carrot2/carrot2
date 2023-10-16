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
package org.carrot2.language;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A stop word filter.
 *
 * @see EphemeralDictionaries
 * @since 4.1.0
 */
@FunctionalInterface
// fragment-start{word-filter}
public interface StopwordFilter extends Predicate<CharSequence> {
  /**
   * @param word The word to test. Input words are guaranteed to be in lower case (consistent with
   *     {@link Character#toLowerCase(int)}.
   * @return Return {@code false} if the provided term should be ignored in processing.
   */
  boolean test(CharSequence word);

  // fragment-end{word-filter}

  @Override
  default StopwordFilter and(Predicate<? super CharSequence> other) {
    Objects.requireNonNull(other);
    return (t) -> test(t) && other.test(t);
  }
}
