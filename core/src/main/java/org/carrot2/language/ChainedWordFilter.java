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

import java.util.ArrayList;
import java.util.List;

/** A composite {@link StopwordFilter}. */
class ChainedWordFilter implements StopwordFilter {
  private final ArrayList<StopwordFilter> filters;

  public ChainedWordFilter(List<StopwordFilter> filters) {
    this.filters = new ArrayList<>(filters);
  }

  @Override
  public boolean ignoreWord(CharSequence word) {
    for (var filter : filters) {
      if (filter.ignoreWord(word)) {
        return true;
      }
    }
    return false;
  }
}
