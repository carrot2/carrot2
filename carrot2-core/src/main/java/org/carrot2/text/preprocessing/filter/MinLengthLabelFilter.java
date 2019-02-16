
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

package org.carrot2.text.preprocessing.filter;

import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.attrs.AttrBoolean;
import org.carrot2.util.attrs.AttrInteger;

/**
 * Accepts labels whose length in characters is greater or equal to the provided value.
 */
public class MinLengthLabelFilter extends SingleLabelFilterBase {
  /**
   * Remove labels shorter than a given number of characters. Removes labels whose total length in
   * characters, including spaces, is less than the given length.
   */
  public AttrBoolean enabled = attributes.register("enabled", AttrBoolean.builder()
      .label("Remove short labels")
      .defaultValue(true)
      .build());

  public AttrInteger minLength = attributes.register("minLength", AttrInteger.builder()
      .label("Minimum label length (inclusive)")
      .defaultValue(3)
      .build());

  @Override
  public boolean acceptPhrase(PreprocessingContext context, int phraseIndex) {
    final int[] wordIndices = context.allPhrases.wordIndices[phraseIndex];
    char[][] wordImage = context.allWords.image;
    int minLength = this.minLength.get();

    int wordIndex = 0;
    int length = wordImage[wordIndices[wordIndex++]].length;
    while (length < minLength && wordIndex < wordIndices.length) {
      length += wordImage[wordIndices[wordIndex]].length + 1 /* space */;
      wordIndex++;
    }

    return length >= minLength;
  }

  @Override
  public boolean acceptWord(PreprocessingContext context, int wordIndex) {
    return context.allWords.image[wordIndex].length >= minLength.get();
  }

  public boolean isEnabled() {
    return enabled.get();
  }
}
