
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
import org.carrot2.text.preprocessing.filter.CompleteLabelFilter;
import org.carrot2.text.preprocessing.filter.GenitiveLabelFilter;
import org.carrot2.text.preprocessing.filter.MinLengthLabelFilter;
import org.carrot2.text.preprocessing.filter.NumericLabelFilter;
import org.carrot2.text.preprocessing.filter.QueryLabelFilter;
import org.carrot2.text.preprocessing.filter.StopLabelFilter;
import org.carrot2.text.preprocessing.filter.StopWordLabelFilter;
import org.carrot2.attrs.AttrComposite;
import org.carrot2.attrs.AttrObject;

import java.util.Arrays;

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
  public AttrObject<QueryLabelFilter> queryLabelFilter = attributes.register("queryLabelFilter", AttrObject.builder(QueryLabelFilter.class)
      .defaultValue(new QueryLabelFilter())
      .build());

  /**
   * Stop word label filter for this processor.
   */
  public AttrObject<StopWordLabelFilter> stopWordLabelFilter = attributes.register("stopWordLabelFilter", AttrObject.builder(StopWordLabelFilter.class)
      .defaultValue(new StopWordLabelFilter())
      .build());

  /**
   * Numeric label filter for this processor.
   */
  public AttrObject<NumericLabelFilter> numericLabelFilter = attributes.register("numericLabelFilter", AttrObject.builder(NumericLabelFilter.class)
      .defaultValue(new NumericLabelFilter())
      .build());

  /**
   * Truncated phrase filter for this processor.
   */
  public AttrObject<CompleteLabelFilter> completeLabelFilter = attributes.register("completeLabelFilter", AttrObject.builder(CompleteLabelFilter.class)
      .defaultValue(new CompleteLabelFilter())
      .build());

  /**
   * Min length label filter.
   */
  public AttrObject<MinLengthLabelFilter> minLengthLabelFilter = attributes.register("minLengthLabelFilter", AttrObject.builder(MinLengthLabelFilter.class)
      .defaultValue(new MinLengthLabelFilter())
      .build());

  /**
   * Genitive length label filter.
   */
  public AttrObject<GenitiveLabelFilter> genitiveLabelFilter = attributes.register("genitiveLabelFilter", AttrObject.builder(GenitiveLabelFilter.class)
      .defaultValue(new GenitiveLabelFilter())
      .build());

  /**
   * Stop label filter.
   */
  public AttrObject<StopLabelFilter> stopLabelFilter = attributes.register("stopLabelFilter", AttrObject.builder(StopLabelFilter.class)
      .defaultValue(new StopLabelFilter())
      .build());

  /**
   * Processes all filters declared as fields of this class.
   */
  public void process(PreprocessingContext context) {
    final int wordCount = context.allWords.image.length;
    final boolean[] acceptedStems = new boolean[context.allStems.image.length];
    final boolean[] acceptedPhrases = new boolean[context.allPhrases.tf.length];
    Arrays.fill(acceptedStems, true);
    Arrays.fill(acceptedPhrases, true);

    minLengthLabelFilter.get().filter(context, acceptedStems, acceptedPhrases);
    genitiveLabelFilter.get().filter(context, acceptedStems, acceptedPhrases);
    queryLabelFilter.get().filter(context, acceptedStems, acceptedPhrases);
    stopWordLabelFilter.get().filter(context, acceptedStems, acceptedPhrases);
    numericLabelFilter.get().filter(context, acceptedStems, acceptedPhrases);
    stopLabelFilter.get().filter(context, acceptedStems, acceptedPhrases);
    completeLabelFilter.get().filter(context, acceptedStems, acceptedPhrases);

    final IntArrayList acceptedFeatures = new IntArrayList(acceptedStems.length
        + acceptedPhrases.length);

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
