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

import java.util.stream.Stream;
import org.carrot2.text.preprocessing.PreprocessingContext;

/**
 * Removes labels that end in words in the Saxon Genitive form, for example <em>Threatening the
 * Country's</em>.
 */
public class GenitiveLabelFilter extends SingleLabelFilterBase {
  private static final char[][] ENDINGS =
      Stream.of("'s", "`s", "s'", "s`").map(String::toCharArray).toArray(char[][]::new);

  public GenitiveLabelFilter() {
    super("Genitive label filter enabled");
  }

  @Override
  public boolean acceptPhrase(PreprocessingContext context, int phraseIndex) {
    final int[] wordIndices = context.allPhrases.wordIndices[phraseIndex];
    return isGenitive(context.allWords.image, wordIndices[wordIndices.length - 1]);
  }

  @Override
  public boolean acceptWord(PreprocessingContext context, int wordIndex) {
    return isGenitive(context.allWords.image, wordIndex);
  }

  private boolean isGenitive(char[][] wordImage, final int wordIndex) {
    char[] image = wordImage[wordIndex];

    outer:
    for (char[] ending : ENDINGS) {
      if (image.length >= ending.length) {
        for (int i = 0; i < ending.length; i++) {
          if (image[image.length - ending.length + i] != ending[i]) {
            continue outer;
          }
        }
        return false;
      }
    }
    return true;
  }
}
