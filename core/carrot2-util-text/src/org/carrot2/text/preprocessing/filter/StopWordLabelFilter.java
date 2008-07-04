package org.carrot2.text.preprocessing.filter;

import org.carrot2.core.attribute.Processing;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Input;

/**
 * Accepts words that are not stop words and phrases that do not start nor end in a stop
 * word.
 */
public class StopWordLabelFilter extends SingleLabelFilterBase
{
    /**
     * Remove leading and trailing stop words.
     */
    @Input
    @Processing
    @Attribute
    public boolean enabled = true;

    @Override
    public boolean acceptPhrase(PreprocessingContext context, int phraseIndex)
    {
        final int [] wordIndices = context.allPhrases.wordIndices[phraseIndex];
        final boolean [] commonTermFlag = context.allWords.commonTermFlag;

        return !commonTermFlag[wordIndices[0]]
            && !commonTermFlag[wordIndices[wordIndices.length - 1]];
    }

    @Override
    public boolean acceptWord(PreprocessingContext context, int wordIndex)
    {
        return !context.allWords.commonTermFlag[wordIndex];
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
