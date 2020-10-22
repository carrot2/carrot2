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

/** Accepts labels that start with a non-numeric token. */
public class NumericLabelFilter extends SingleLabelFilterBase {
  /** Enables or disables the numeric label filter. */
  public AttrBoolean enabled =
      attributes.register(
          "enabled",
          AttrBoolean.builder().label("Numeric label filter enabled").defaultValue(true));

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

  private final boolean isNumeric(short type) {
    return (type & Tokenizer.TYPE_MASK) == Tokenizer.TT_NUMERIC;
  }

  @Override
  public boolean isEnabled() {
    return enabled.get();
  }
}
