/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * https://www.carrot2.org/carrot2.LICENSE
 */
package org.carrot2.language;

/** Provides additional word and label filtering information for a given language. */
// fragment-start{lexical-data}
public interface LexicalData {
  /** @return Return true if the provided term should be ignored in processing. */
  boolean ignoreWord(CharSequence word);

  /** @return Return true if the label candidate should be ignored in processing. */
  boolean ignoreLabel(CharSequence labelCandidate);
}
// fragment-end{lexical-data}
