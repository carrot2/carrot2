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

/**
 * A cluster label candidate filter.
 *
 * @since 4.1.0
 */
@FunctionalInterface
public interface LabelFilter {
  /** @return Return true if the label candidate should be ignored in processing. */
  boolean ignoreLabel(CharSequence labelCandidate);
}
