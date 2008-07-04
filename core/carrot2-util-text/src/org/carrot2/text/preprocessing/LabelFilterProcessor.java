package org.carrot2.text.preprocessing;

import java.util.Arrays;

import org.carrot2.text.preprocessing.filter.CompleteLabelFilter;
import org.carrot2.text.preprocessing.filter.StopWordLabelFilter;
import org.carrot2.util.attribute.Bindable;

import bak.pcj.list.IntArrayList;

/**
 * Applies {@link LabelFilterProcessor}s to words from
 * {@link PreprocessingContext#allWords} and phrases from
 * {@link PreprocessingContext#allPhrases} and stores the filtered list in
 * {@link PreprocessingContext#allLabels}.
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
        final boolean [] acceptedWords = new boolean [context.allWords.image.length];
        final boolean [] acceptedPhrases = new boolean [context.allPhrases.tf.length];
        Arrays.fill(acceptedWords, true);
        Arrays.fill(acceptedPhrases, true);

        stopWordLabelFilter.filter(context, acceptedWords, acceptedPhrases);
        completeLabelFilter.filter(context, acceptedWords, acceptedPhrases);

        final IntArrayList acceptedFeatures = new IntArrayList(acceptedWords.length
            + acceptedPhrases.length);

        for (int i = 0; i < acceptedWords.length; i++)
        {
            if (acceptedWords[i])
            {
                acceptedFeatures.add(i);
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
