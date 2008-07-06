package org.carrot2.text.preprocessing;

import java.util.Arrays;

import org.carrot2.text.preprocessing.PreprocessingContext.*;
import org.carrot2.text.preprocessing.filter.CompleteLabelFilter;
import org.carrot2.text.preprocessing.filter.StopWordLabelFilter;
import org.carrot2.util.attribute.Bindable;

import bak.pcj.list.IntArrayList;

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
@Bindable
public class LabelFilterProcessor
{
    // For the time being we include filters as instance fields here. If there is a need
    // to add custom label filters as parameters, we'll need to come up with something.

    /**
     * Stop word label filter for this processor.
     */
    public StopWordLabelFilter stopWordLabelFilter = new StopWordLabelFilter();

    /**
     * Truncated phrase filter for this processor.
     */
    public CompleteLabelFilter completeLabelFilter = new CompleteLabelFilter();

    /**
     * Processes all filters declared as fields of this class.
     */
    public void process(PreprocessingContext context)
    {
        final boolean [] acceptedWords = new boolean [context.allStems.image.length];
        final boolean [] acceptedPhrases = new boolean [context.allPhrases.tf.length];
        Arrays.fill(acceptedWords, true);
        Arrays.fill(acceptedPhrases, true);

        stopWordLabelFilter.filter(context, acceptedWords, acceptedPhrases);
        completeLabelFilter.filter(context, acceptedWords, acceptedPhrases);

        final IntArrayList acceptedFeatures = new IntArrayList(acceptedWords.length
            + acceptedPhrases.length);

        final int [] mostFrequentOriginalWordIndex = context.allStems.mostFrequentOriginalWordIndex;
        for (int i = 0; i < acceptedWords.length; i++)
        {
            if (acceptedWords[i])
            {
                acceptedFeatures.add(mostFrequentOriginalWordIndex[i]);
            }
        }

        for (int i = 0; i < acceptedPhrases.length; i++)
        {
            if (acceptedPhrases[i])
            {
                acceptedFeatures.add(i + acceptedWords.length);
            }
        }

        context.allLabels.featureIndex = acceptedFeatures.toArray();
    }
}
