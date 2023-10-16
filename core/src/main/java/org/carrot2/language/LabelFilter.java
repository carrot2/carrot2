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
 * A cluster label candidate filter.
 *
 * @since 4.1.0
 */
@FunctionalInterface
// fragment-start{label-filter}
public interface LabelFilter extends Predicate<CharSequence> {
  /**
   * @param label The label to test. Input labels may have mixed case, depending on the algorithm
   *     and their surface forms collected from input documents.
   * @return Return {@code false} if the label candidate should be ignored in processing.
   */
  boolean test(CharSequence label);

  // fragment-end{label-filter}

  @Override
  default LabelFilter and(Predicate<? super CharSequence> other) {
    Objects.requireNonNull(other);
    return (t) -> test(t) && other.test(t);
  }
}
