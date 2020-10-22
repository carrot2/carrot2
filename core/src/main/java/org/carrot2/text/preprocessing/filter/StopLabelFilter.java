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
import org.carrot2.language.LexicalData;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;

/** Accepts labels that are not declared as stop labels in the {@code stoplabels.<lang>} files. */
public class StopLabelFilter extends SingleLabelFilterBase {
  /** Enables or disables the stop label filter. */
  public AttrBoolean enabled =
      attributes.register(
          "enabled",
          AttrBoolean.builder().label("Stop label filter enabled").defaultValue(true));

  private LabelFormatter labelFormatter;
  private LexicalData lexicalData;

  @Override
  public void filter(
      PreprocessingContext context, boolean[] acceptedStems, boolean[] acceptedPhrases) {
    lexicalData = context.languageComponents.get(LexicalData.class);
    labelFormatter = context.languageComponents.get(LabelFormatter.class);
    super.filter(context, acceptedStems, acceptedPhrases);
  }

  @Override
  public boolean acceptPhrase(PreprocessingContext context, int phraseIndex) {
    final String formatedLabel =
        context.format(labelFormatter, phraseIndex + context.allWords.image.length);
    return !lexicalData.ignoreLabel(formatedLabel);
  }

  @Override
  public boolean acceptWord(PreprocessingContext context, int wordIndex) {
    final String formattedLabel = context.format(labelFormatter, wordIndex);
    return !lexicalData.ignoreLabel(formattedLabel);
  }

  @Override
  public boolean isEnabled() {
    return enabled.get();
  }
}
