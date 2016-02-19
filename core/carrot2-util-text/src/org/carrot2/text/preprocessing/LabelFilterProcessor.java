
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing;

import java.util.Arrays;

import org.carrot2.text.preprocessing.PreprocessingContext.*;
import org.carrot2.text.preprocessing.filter.*;
import org.carrot2.util.attribute.Bindable;

import com.carrotsearch.hppc.IntArrayList;

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
 * This class requires that {@link Tokenizer}, {@link CaseNormalizer},
 * {@link StopListMarker} and {@link PhraseExtractor} be invoked first.
 */
@Bindable(prefix = "LabelFilterProcessor")
public class LabelFilterProcessor
{
    // For the time being we include filters as instance fields here. If there is a need
    // to add custom label filters as parameters, we'll need to come up with something.

    /**
     * Query word label filter for this processor.
     */
    public QueryLabelFilter queryLabelFilter = new QueryLabelFilter();

    /**
     * Stop word label filter for this processor.
     */
    public StopWordLabelFilter stopWordLabelFilter = new StopWordLabelFilter();

    /**
     * Numeric label filter for this processor.
     */
    public NumericLabelFilter numericLabelFilter = new NumericLabelFilter();

    /**
     * Truncated phrase filter for this processor.
     */
    public CompleteLabelFilter completeLabelFilter = new CompleteLabelFilter();

    /**
     * Min length label filter.
     */
    public MinLengthLabelFilter minLengthLabelFilter = new MinLengthLabelFilter();

    /**
     * Genitive length label filter.
     */
    public GenitiveLabelFilter genitiveLabelFilter = new GenitiveLabelFilter();

    /**
     * Stop label filter.
     */
    public StopLabelFilter stopLabelFilter = new StopLabelFilter();

    /**
     * Processes all filters declared as fields of this class.
     */
    public void process(PreprocessingContext context)
    {
        final int wordCount = context.allWords.image.length;
        final boolean [] acceptedStems = new boolean [context.allStems.image.length];
        final boolean [] acceptedPhrases = new boolean [context.allPhrases.tf.length];
        Arrays.fill(acceptedStems, true);
        Arrays.fill(acceptedPhrases, true);

        minLengthLabelFilter.filter(context, acceptedStems, acceptedPhrases);
        genitiveLabelFilter.filter(context, acceptedStems, acceptedPhrases);
        queryLabelFilter.filter(context, acceptedStems, acceptedPhrases);
        stopWordLabelFilter.filter(context, acceptedStems, acceptedPhrases);
        numericLabelFilter.filter(context, acceptedStems, acceptedPhrases);
        stopLabelFilter.filter(context, acceptedStems, acceptedPhrases);
        completeLabelFilter.filter(context, acceptedStems, acceptedPhrases);

        final IntArrayList acceptedFeatures = new IntArrayList(acceptedStems.length
            + acceptedPhrases.length);

        final int [] mostFrequentOriginalWordIndex = context.allStems.mostFrequentOriginalWordIndex;
        for (int i = 0; i < acceptedStems.length; i++)
        {
            if (acceptedStems[i])
            {
                acceptedFeatures.add(mostFrequentOriginalWordIndex[i]);
            }
        }

        for (int i = 0; i < acceptedPhrases.length; i++)
        {
            if (acceptedPhrases[i])
            {
                acceptedFeatures.add(i + wordCount);
            }
        }

        context.allLabels.featureIndex = acceptedFeatures.toArray();
        updateFirstPhraseIndex(context);
    }
    
    static void updateFirstPhraseIndex(PreprocessingContext context)
    {
        final int wordCount = context.allWords.image.length;
        final int [] labelsFeatureIndex = context.allLabels.featureIndex;

        // In theory we could do a binary search here, but the effort of writing
        // a customized version may not be worth the gain
        int firstPhraseIndex = -1;
        for (int i = 0; i < labelsFeatureIndex.length; i++)
        {
            if (labelsFeatureIndex[i] >= wordCount)
            {
                firstPhraseIndex = i;
                break;
            }
        }

        context.allLabels.firstPhraseIndex = firstPhraseIndex;
    }
}
