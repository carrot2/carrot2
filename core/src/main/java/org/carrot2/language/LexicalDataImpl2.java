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

import java.util.List;

public class LexicalDataImpl2 implements LexicalData {
  private final List<WordFilter> wordFilters;
  private final List<LabelFilter> labelFilters;

  public LexicalDataImpl2(List<WordFilter> wordFilters, List<LabelFilter> labelFilters) {
    this.wordFilters = wordFilters;
    this.labelFilters = labelFilters;
  }

  @Override
  public boolean ignoreWord(CharSequence word) {
    for (WordFilter filter : wordFilters) {
      if (filter.ignoreWord(word)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean ignoreLabel(CharSequence label) {
    for (LabelFilter filter : labelFilters) {
      if (filter.ignoreLabel(label)) {
        return true;
      }
    }
    return false;
  }
}
