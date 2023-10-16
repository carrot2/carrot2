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

import org.carrot2.attrs.AttrDouble;
import org.carrot2.text.preprocessing.PreprocessingContext;

/**
 * A filter that removes "incomplete" labels.
 *
 * <p>For example, in a collection of documents related to <i>Data Mining</i>, the phrase
 * <i>Conference on Data</i> is incomplete in a sense that most likely it should be <i>Conference on
 * Data Mining</i> or even <i>Conference on Data Mining in Large Databases</i>. When truncated
 * phrase removal is enabled, the algorithm would try to remove the "incomplete" phrases like the
 * former one and leave only the more informative variants.
 *
 * <p>See <a href="http://project.carrot2.org/publications/osinski-2003-lingo.pdf">this
 * document</a>, page 31 for a definition of a complete phrase.
 */
public class CompleteLabelFilter extends ContextLabelFilter {
  public CompleteLabelFilter() {
    super("Truncated label filter enabled");
  }

  /**
   * Determines the strength of the truncated label filter. The lowest value means strongest
   * truncated labels elimination, which may lead to overlong cluster labels and many unclustered
   * documents. The highest value effectively disables the filter, which may result in short or
   * truncated labels.
   */
  public AttrDouble labelOverrideThreshold =
      attributes.register(
          "labelOverrideThreshold",
          AttrDouble.builder().label("Truncated label threshold").min(0).max(1).defaultValue(0.65));

  /** Left complete label filter. */
  private LeftCompleteLabelFilter leftCompleteLabelFilter = new LeftCompleteLabelFilter();

  /** Right complete label filter. */
  private RightCompleteLabelFilter rightCompleteLabelFilter = new RightCompleteLabelFilter();

  /** Marks incomplete labels. */
  public void filter(
      PreprocessingContext context, boolean[] acceptedStems, boolean[] acceptedPhrases) {
    double labelOverrideThreshold = this.labelOverrideThreshold.get();
    leftCompleteLabelFilter.filter(context, acceptedStems, acceptedPhrases, labelOverrideThreshold);
    rightCompleteLabelFilter.filter(
        context, acceptedStems, acceptedPhrases, labelOverrideThreshold);
  }
}
