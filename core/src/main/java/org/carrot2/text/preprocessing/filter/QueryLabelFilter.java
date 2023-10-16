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
package org.carrot2.text.preprocessing.filter;

import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.PreprocessingContext;

/** Removes labels that consist only of query words. */
public class QueryLabelFilter extends SingleLabelFilterBase {
  public QueryLabelFilter() {
    super("Query label filter enabled");
  }

  @Override
  public boolean acceptPhrase(PreprocessingContext context, int phraseIndex) {
    final int[] wordIndices = context.allPhrases.wordIndices[phraseIndex];
    final short[] flag = context.allWords.type;

    for (int i = 0; i < wordIndices.length; i++) {
      if (!isQueryWord(flag[wordIndices[i]])) {
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean acceptWord(PreprocessingContext context, int wordIndex) {
    return !isQueryWord(context.allWords.type[wordIndex]);
  }

  private final boolean isQueryWord(short flag) {
    return (flag & Tokenizer.TF_QUERY_WORD) != 0;
  }
}
