
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing.filter;

import org.carrot2.core.attribute.Processing;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.PreprocessingContext.AllWords;
import org.carrot2.util.attribute.*;

/**
 * Accepts labels that do not consist only of query words.
 */
@Bindable(prefix = "QueryLabelFilter")
public class QueryLabelFilter extends SingleLabelFilterBase
{
    /**
     * Remove query words. Removes labels that consist only of words contained in the
     * query.
     * 
     * @level Basic
     * @group Label filtering
     * @label Remove query words
     */
    @Input
    @Processing
    @Attribute
    public boolean enabled = true;

    @Override
    public boolean acceptPhrase(PreprocessingContext context, int phraseIndex)
    {
        final int [] wordIndices = context.allPhrases.wordIndices[phraseIndex];
        final int [] flag = context.allWords.flag;

        for (int i = 0; i < wordIndices.length; i++)
        {
            if (!isQueryWord(flag[wordIndices[i]]))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean acceptWord(PreprocessingContext context, int wordIndex)
    {
        return !isQueryWord(context.allWords.flag[wordIndex]);
    }

    private final boolean isQueryWord(int flag)
    {
        return (flag & AllWords.FLAG_QUERY) != 0;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
