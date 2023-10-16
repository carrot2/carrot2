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

/** Removes labels that start with a numeric token. */
public class NumericLabelFilter extends SingleLabelFilterBase {
  public NumericLabelFilter() {
    super("Numeric label filter enabled");
  }

  @Override
  public boolean acceptPhrase(PreprocessingContext context, int phraseIndex) {
    final int[] wordIndices = context.allPhrases.wordIndices[phraseIndex];
    final short[] type = context.allWords.type;

    return !isNumeric(type[wordIndices[0]]);
  }

  @Override
  public boolean acceptWord(PreprocessingContext context, int wordIndex) {
    return !isNumeric(context.allWords.type[wordIndex]);
  }

  private boolean isNumeric(short type) {
    return (type & Tokenizer.TYPE_MASK) == Tokenizer.TT_NUMERIC;
  }
}
