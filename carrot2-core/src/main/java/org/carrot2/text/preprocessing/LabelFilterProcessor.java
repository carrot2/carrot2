
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

package org.carrot2.text.preprocessing;

import com.carrotsearch.hppc.IntArrayList;
import org.carrot2.text.preprocessing.PreprocessingContext.AllLabels;
import org.carrot2.text.preprocessing.PreprocessingContext.AllPhrases;
import org.carrot2.text.preprocessing.PreprocessingContext.AllWords;
import org.carrot2.text.preprocessing.filter.*;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrObject;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Applies basic filtering to words and phrases to produce candidates for cluster labels.
 * Filtering is applied to {@link AllWords} and {@link AllPhrases}, the results are saved
 * to {@link AllLabels}. Currently, the following filters are applied:
 * <ol>
 * <li>{@link StopWordLabelFilter}</li>
 * <li>{@link CompleteLabelFilter}</li>
 * </ol>
 * This class saves the following results to the {@link PreprocessingContext}:
 * <ul>
 * <li>{@link AllLabels#featureIndex}</li>
 * </ul>
 * <p>
 * This class requires that {@link InputTokenizer}, {@link CaseNormalizer},
 * {@link StopListMarker} and {@link PhraseExtractor} be invoked first.
 */
public class LabelFilterProcessor extends AttrComposite {
  // For the time being we include filters as instance fields here. If there is a need
  // to add custom label filters as parameters, we'll need to come up with something.

  /**
   * Query word label filter for this processor.
   */
  public QueryLabelFilter queryLabelFilter = new QueryLabelFilter();
  {
    attributes.register("queryLabelFilter",
        () -> queryLabelFilter,
        (v) -> queryLabelFilter = v,
        () -> new QueryLabelFilter());
  }

  /**
   * Stop word label filter for this processor.
   */
  public StopWordLabelFilter stopWordLabelFilter = new StopWordLabelFilter();
  {
    attributes.register("stopWordLabelFilter",
        () -> stopWordLabelFilter,
        (v) -> stopWordLabelFilter = v,
        () -> new StopWordLabelFilter());
  }

  /**
   * Numeric label filter for this processor.
   */
  public NumericLabelFilter numericLabelFilter = new NumericLabelFilter();
  {
    attributes.register("numericLabelFilter",
        () -> numericLabelFilter,
        (v) -> numericLabelFilter = v,
        () -> new NumericLabelFilter());
  }

  /**
   * Truncated phrase filter for this processor.
   */
  public CompleteLabelFilter completeLabelFilter = new CompleteLabelFilter();
  {
    attributes.register("completeLabelFilter",
        () -> completeLabelFilter,
        (v) -> completeLabelFilter = v,
        () -> new CompleteLabelFilter());
  }

  /**
   * Min length label filter.
   */
  public MinLengthLabelFilter minLengthLabelFilter = new MinLengthLabelFilter();
  {
    attributes.register("minLengthLabelFilter",
        () -> minLengthLabelFilter,
        (v) -> minLengthLabelFilter = v,
        () -> new MinLengthLabelFilter());
  }

  /**
   * Genitive length label filter.
   */
  public GenitiveLabelFilter genitiveLabelFilter = new GenitiveLabelFilter();
  {
    attributes.register("genitiveLabelFilter",
        () -> genitiveLabelFilter,
        (v) -> genitiveLabelFilter = v,
        () -> new GenitiveLabelFilter());
  }

  /**
   * Stop label filter.
   */
  public StopLabelFilter stopLabelFilter = new StopLabelFilter();
  {
    attributes.register("stopLabelFilter",
        () -> stopLabelFilter,
        (v) -> stopLabelFilter = v,
        () -> new StopLabelFilter());
  }

  /**
   * Processes all filters declared as fields of this class.
   */
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
    .forEachOrdered((ILabelFilter filter) -> {
      if (filter != null) {
        filter.filter(context, acceptedStems, acceptedPhrases);
      }
    });

    final IntArrayList acceptedFeatures = new IntArrayList(acceptedStems.length + acceptedPhrases.length);

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
