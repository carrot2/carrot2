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
package org.carrot2.text.preprocessing;

import com.carrotsearch.hppc.IntArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrObject;
import org.carrot2.text.preprocessing.PreprocessingContext.AllLabels;
import org.carrot2.text.preprocessing.PreprocessingContext.AllPhrases;
import org.carrot2.text.preprocessing.PreprocessingContext.AllWords;
import org.carrot2.text.preprocessing.filter.CompleteLabelFilter;
import org.carrot2.text.preprocessing.filter.ContextLabelFilter;
import org.carrot2.text.preprocessing.filter.GenitiveLabelFilter;
import org.carrot2.text.preprocessing.filter.MinLengthLabelFilter;
import org.carrot2.text.preprocessing.filter.NumericLabelFilter;
import org.carrot2.text.preprocessing.filter.QueryLabelFilter;
import org.carrot2.text.preprocessing.filter.StopLabelFilter;
import org.carrot2.text.preprocessing.filter.StopWordLabelFilter;

/**
 * Applies basic filtering to words and phrases to produce candidates for cluster labels. Filtering
 * is applied to {@link AllWords} and {@link AllPhrases}, the results are saved to {@link
 * AllLabels}. Currently, the following filters are applied:
 *
 * <ol>
 *   <li>{@link StopWordLabelFilter}
 *   <li>{@link CompleteLabelFilter}
 * </ol>
 *
 * This class saves the following results to the {@link PreprocessingContext}:
 *
 * <ul>
 *   <li>{@link AllLabels#featureIndex}
 * </ul>
 *
 * <p>This class requires that {@link InputTokenizer}, {@link CaseNormalizer}, {@link
 * StopListMarker} and {@link PhraseExtractor} be invoked first.
 */
public class LabelFilterProcessor extends AttrComposite {
  // For the time being we include filters as instance fields here. If there is a need
  // to add custom label filters as parameters, we'll need to come up with something.

  /** Query word label filter for this processor. */
  public QueryLabelFilter queryLabelFilter;

  {
    attributes.register(
        "queryLabelFilter",
        AttrObject.builder(QueryLabelFilter.class)
            .label("Filters out labels consisting of query hint terms")
            .getset(() -> queryLabelFilter, (v) -> queryLabelFilter = v)
            .defaultValue(QueryLabelFilter::new));
  }

  /** Stop word label filter for this processor. */
  public StopWordLabelFilter stopWordLabelFilter;

  {
    attributes.register(
        "stopWordLabelFilter",
        AttrObject.builder(StopWordLabelFilter.class)
            .label("Filters out labels starting or ending with ignorable words")
            .getset(() -> stopWordLabelFilter, (v) -> stopWordLabelFilter = v)
            .defaultValue(StopWordLabelFilter::new));
  }

  /** Stop label filter. */
  public StopLabelFilter stopLabelFilter;

  {
    attributes.register(
        "stopLabelFilter",
        AttrObject.builder(StopLabelFilter.class)
            .label("Filters out labels tagged ignorable by the lexical data filters")
            .getset(() -> stopLabelFilter, (v) -> stopLabelFilter = v)
            .defaultValue(StopLabelFilter::new));
  }

  /** Numeric label filter for this processor. */
  public NumericLabelFilter numericLabelFilter;

  {
    attributes.register(
        "numericLabelFilter",
        AttrObject.builder(NumericLabelFilter.class)
            .label("Filters out labels that start with numerics")
            .getset(() -> numericLabelFilter, (v) -> numericLabelFilter = v)
            .defaultValue(NumericLabelFilter::new));
  }

  /** Truncated phrase filter for this processor. */
  public CompleteLabelFilter completeLabelFilter;

  {
    attributes.register(
        "completeLabelFilter",
        AttrObject.builder(CompleteLabelFilter.class)
            .label(
                "Filters out labels that appear to be sub-sequences of other good candidate phrases")
            .getset(() -> completeLabelFilter, (v) -> completeLabelFilter = v)
            .defaultValue(CompleteLabelFilter::new));
  }

  /** Min length label filter. */
  public MinLengthLabelFilter minLengthLabelFilter;

  {
    attributes.register(
        "minLengthLabelFilter",
        AttrObject.builder(MinLengthLabelFilter.class)
            .label("Filters out labels that are shorter than the provided threshold")
            .getset(() -> minLengthLabelFilter, (v) -> minLengthLabelFilter = v)
            .defaultValue(MinLengthLabelFilter::new));
  }

  /** Genitive length label filter. */
  public GenitiveLabelFilter genitiveLabelFilter;

  {
    attributes.register(
        "genitiveLabelFilter",
        AttrObject.builder(GenitiveLabelFilter.class)
            .label("Filters out labels ending with Saxon Genitive ('s)")
            .getset(() -> genitiveLabelFilter, (v) -> genitiveLabelFilter = v)
            .defaultValue(GenitiveLabelFilter::new));
  }

  /** Processes all filters declared as fields of this class. */
  public void process(PreprocessingContext context) {
    final int wordCount = context.allWords.image.length;
    final boolean[] acceptedStems = new boolean[context.allStems.image.length];
    final boolean[] acceptedPhrases = new boolean[context.allPhrases.tf.length];
    Arrays.fill(acceptedStems, true);
    Arrays.fill(acceptedPhrases, true);

    Stream.of(
            minLengthLabelFilter,
            genitiveLabelFilter,
            queryLabelFilter,
            stopWordLabelFilter,
            numericLabelFilter,
            stopLabelFilter,
            completeLabelFilter)
        .forEachOrdered(
            (ContextLabelFilter filter) -> {
              if (filter != null && filter.isEnabled()) {
                filter.filter(context, acceptedStems, acceptedPhrases);
              }
            });

    final IntArrayList acceptedFeatures =
        new IntArrayList(acceptedStems.length + acceptedPhrases.length);

    final int[] mostFrequentOriginalWordIndex = context.allStems.mostFrequentOriginalWordIndex;
    for (int i = 0; i < acceptedStems.length; i++) {
      if (acceptedStems[i]) {
        acceptedFeatures.add(mostFrequentOriginalWordIndex[i]);
      }
    }

    for (int i = 0; i < acceptedPhrases.length; i++) {
      if (acceptedPhrases[i]) {
        acceptedFeatures.add(i + wordCount);
      }
    }

    context.allLabels.featureIndex = acceptedFeatures.toArray();
    updateFirstPhraseIndex(context);
  }

  static void updateFirstPhraseIndex(PreprocessingContext context) {
    final int wordCount = context.allWords.image.length;
    final int[] labelsFeatureIndex = context.allLabels.featureIndex;

    // In theory we could do a binary search here, but the effort of writing
    // a customized version may not be worth the gain
    int firstPhraseIndex = -1;
    for (int i = 0; i < labelsFeatureIndex.length; i++) {
      if (labelsFeatureIndex[i] >= wordCount) {
        firstPhraseIndex = i;
        break;
      }
    }

    context.allLabels.firstPhraseIndex = firstPhraseIndex;
  }
}
