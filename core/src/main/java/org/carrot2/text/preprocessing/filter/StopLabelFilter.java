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

import org.carrot2.language.LabelFilter;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;

/**
 * Removes labels that are declared as stop labels in the {@code stoplabels.<lang>} files (or are
 * filtered out by {@link org.carrot2.language.EphemeralDictionaries}.
 */
public class StopLabelFilter extends SingleLabelFilterBase {
  private LabelFormatter labelFormatter;
  private LabelFilter labelFilter;

  public StopLabelFilter() {
    super("Stop label filter enabled");
  }

  @Override
  public void filter(
      PreprocessingContext context, boolean[] acceptedStems, boolean[] acceptedPhrases) {
    labelFilter = context.languageComponents.get(LabelFilter.class);
    labelFormatter = context.languageComponents.get(LabelFormatter.class);
    super.filter(context, acceptedStems, acceptedPhrases);
  }

  @Override
  public boolean acceptPhrase(PreprocessingContext context, int phraseIndex) {
    final String formatedLabel =
        context.format(labelFormatter, phraseIndex + context.allWords.image.length);
    return labelFilter.test(formatedLabel);
  }

  @Override
  public boolean acceptWord(PreprocessingContext context, int wordIndex) {
    final String formattedLabel = context.format(labelFormatter, wordIndex);
    return labelFilter.test(formattedLabel);
  }
}
