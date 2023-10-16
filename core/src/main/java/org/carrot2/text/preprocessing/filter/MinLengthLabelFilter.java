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

import org.carrot2.attrs.AttrInteger;
import org.carrot2.text.preprocessing.PreprocessingContext;

/** Removes labels whose length in characters is smaller than the provided value. */
public class MinLengthLabelFilter extends SingleLabelFilterBase {

  /** Minimum required label length, in characters, inclusive. */
  public AttrInteger minLength =
      attributes.register(
          "minLength", AttrInteger.builder().label("Minimum label length").defaultValue(3));

  public MinLengthLabelFilter() {
    super("Minimum label length filter enabled");
  }

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
}
