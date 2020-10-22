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
package org.carrot2.text.preprocessing.filter;

import org.carrot2.attrs.AttrBoolean;
import org.carrot2.language.Tokenizer;
import org.carrot2.text.preprocessing.PreprocessingContext;

/** Accepts labels that do not consist only of query words. */
public class QueryLabelFilter extends SingleLabelFilterBase {
  /** Enables or disables the query label filter. */
  public AttrBoolean enabled =
      attributes.register(
          "enabled",
          AttrBoolean.builder().label("Query label filter enabled").defaultValue(true));

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

  @Override
  public boolean isEnabled() {
    return enabled.get();
  }
}
