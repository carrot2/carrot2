
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

import org.carrot2.util.MutableCharArray;

/**
 * Provides additional miscellaneous lexical information for a given language.
 */
public interface LexicalData {
  /**
   * @return Return true if the provided term is common (meaningless).
   * Such words are referred to as "stop words" and are usually
   * ignored in information retrieval tasks.
   */
  default boolean ignoreWord(CharSequence word) {
    return false;
  }

  /**
   * @return Return {@code true} if the label candidate should be ignored in processing.
   */
  default boolean ignoreLabel(CharSequence labelCandidate) {
    return false;
  }

  default boolean usesSpaceDelimiters() {
    return true;
  }
}
