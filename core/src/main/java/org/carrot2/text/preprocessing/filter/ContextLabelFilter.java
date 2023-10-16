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

import org.carrot2.attrs.AttrBoolean;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.text.preprocessing.LabelFilterProcessor;
import org.carrot2.text.preprocessing.PreprocessingContext;

/**
 * Defines the contract for label filtering components, which mark words and phrases that should not
 * be considered as candidates for cluster labels
 *
 * @see LabelFilterProcessor
 */
public abstract class ContextLabelFilter extends AttrComposite {
  /** Enables or disables this filter. */
  public final AttrBoolean enabled;

  protected ContextLabelFilter(String enabledLabel) {
    enabled =
        attributes.register(
            "enabled", AttrBoolean.builder().label(enabledLabel).defaultValue(true));
  }

  /**
   * Called to perform label filtering.
   *
   * @param context contains words and phrases to be filtered
   * @param acceptedStems the filter should set to <code>false</code> those elements that correspond
   *     to the stems to be filtered out
   * @param acceptedPhrases the filter should set to <code>false</code> those elements that
   *     correspond to the phrases to be filtered out
   */
  public abstract void filter(
      PreprocessingContext context, boolean[] acceptedStems, boolean[] acceptedPhrases);

  /**
   * @return {@code true} if the filter is enabled.
   */
  public final boolean isEnabled() {
    return enabled.get();
  }
}
