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

        for (int wordIndex = 0; wordIndex < acceptedWords.length; wordIndex++)
        {
            if (acceptedWords[wordIndex])
            {
                acceptedWords[wordIndex] = acceptWord(context, wordIndex);
            }
        }

        for (int phraseIndex = 0; phraseIndex < acceptedPhrases.length; phraseIndex++)
        {
            if (acceptedPhrases[phraseIndex])
            {
                acceptedPhrases[phraseIndex] = acceptPhrase(context, phraseIndex);
            }
        }
    }

    /**
     * Should return <code>true</code> if the word located at <code>wordIndex</code> is to
     * be accepted, <code>false</code> otherwise.
     * 
     * @param context provides access to all information about the word
     * @param wordIndex index of the word for which decision is to be made
     */
    public abstract boolean acceptWord(PreprocessingContext context, int wordIndex);

    /**
     * Should return <code>true</code> if the phrase located at <code>phraseIndex</code>
     * is to be accepted, <code>false</code> otherwise.
     * 
     * @param context provides access to all information about the phrase
     * @param phraseIndex index of the phrase for which decision is to be made
     */
    public abstract boolean acceptPhrase(PreprocessingContext context, int phraseIndex);
}
