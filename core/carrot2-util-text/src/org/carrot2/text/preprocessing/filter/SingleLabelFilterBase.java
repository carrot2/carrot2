package org.carrot2.text.preprocessing.filter;

import org.carrot2.text.preprocessing.PreprocessingContext;

/**
 * A base for {@link LabelFilter} implementations that handle each label independently.
 */
public abstract class SingleLabelFilterBase implements LabelFilter
{
    public void filter(PreprocessingContext context, boolean [] acceptedWords,
        boolean [] acceptedPhrases)
    {
        if (!isEnabled())
        {
            return;
        }

        for (int wordIndex = 0; wordIndex < context.allWords.image.length; wordIndex++)
        {
            if (acceptedWords[wordIndex])
            {
                acceptedWords[wordIndex] = acceptWord(context, wordIndex);
            }
        }

        for (int phraseIndex = 0; phraseIndex < context.allPhrases.wordIndices.length; phraseIndex++)
        {
            if (acceptedPhrases[phraseIndex])
            {
                acceptedPhrases[phraseIndex] = acceptPhrase(context, phraseIndex);
            }
        }
    }

    public abstract boolean acceptWord(PreprocessingContext context, int wordIndex);

    public abstract boolean acceptPhrase(PreprocessingContext context, int phraseIndex);
}
