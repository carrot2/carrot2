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

import static org.carrot2.language.TokenTypeUtils.isCommon;

import org.carrot2.attrs.AttrBoolean;
import org.carrot2.text.preprocessing.PreprocessingContext;

/** Removes labels that start or end in a stop word. */
public class StopWordLabelFilter extends SingleLabelFilterBase {
  /** Enables or disables the stop word label filter. */
  public AttrBoolean enabled =
      attributes.register(
          "enabled",
          AttrBoolean.builder().label("Stop word label filter enabled").defaultValue(true));

  @Override
  public boolean acceptPhrase(PreprocessingContext context, int phraseIndex) {
    final int[] wordIndices = context.allPhrases.wordIndices[phraseIndex];
    final short[] termTypes = context.allWords.type;

    return !isCommon(termTypes[wordIndices[0]])
        && !isCommon(termTypes[wordIndices[wordIndices.length - 1]]);
  }

  @Override
  public boolean acceptWord(PreprocessingContext context, int wordIndex) {
    return !isCommon(context.allWords.type[wordIndex]);
  }

  @Override
  public boolean isEnabled() {
    return enabled.get();
  }
}
