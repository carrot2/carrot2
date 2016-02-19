
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

package org.carrot2.text.preprocessing.filter;

import org.carrot2.text.preprocessing.PreprocessingContext;

/**
 * A base for {@link ILabelFilter} implementations that handle each label independently.
 */
public abstract class SingleLabelFilterBase implements ILabelFilter
{
    public void filter(PreprocessingContext context, boolean [] acceptedStems,
        boolean [] acceptedPhrases)
    {
        if (!isEnabled())
        {
            return;
        }

        final int [] mostFrequentOriginalWordIndex = context.allStems.mostFrequentOriginalWordIndex;

        for (int stemIndex = 0; stemIndex < acceptedStems.length; stemIndex++)
        {
            if (acceptedStems[stemIndex])
            {
                acceptedStems[stemIndex] = acceptWord(context,
                    mostFrequentOriginalWordIndex[stemIndex]);
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
