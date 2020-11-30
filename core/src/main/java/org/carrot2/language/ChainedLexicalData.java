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

import java.util.Arrays;
import java.util.List;

/** @since 4.1.0 */
public class ChainedLexicalData implements LexicalData {
  List<LexicalData> delegates;

  public ChainedLexicalData(LexicalData... delegates) {
    this.delegates = Arrays.asList(delegates);
  }

  @Override
  public boolean ignoreWord(CharSequence word) {
    for (LexicalData ld : delegates) {
      if (ld.ignoreWord(word)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean ignoreLabel(CharSequence labelCandidate) {
    for (LexicalData ld : delegates) {
      if (ld.ignoreLabel(labelCandidate)) {
        return true;
      }
    }
    return false;
  }
}
