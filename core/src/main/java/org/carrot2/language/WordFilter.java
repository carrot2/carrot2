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
 * A stop word filter.
 *
 * @see Dictionaries
 * @since 4.1.0
 */
@FunctionalInterface
public interface WordFilter {
  /** @return Return true if the provided term should be ignored in processing. */
  boolean ignoreWord(CharSequence word);
}
