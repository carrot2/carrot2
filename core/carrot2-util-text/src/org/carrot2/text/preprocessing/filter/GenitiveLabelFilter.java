
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

import org.carrot2.core.attribute.Processing;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.attribute.*;

/**
 * Accepts labels that do not end in words in the Saxon Genitive form (e.g. "Threatening
 * the Country's").
 */
@Bindable(prefix = "GenitiveLabelFilter")
public class GenitiveLabelFilter extends SingleLabelFilterBase
{
    /**
     * Remove labels ending in genitive form. Removes labels that do end in words
     * in the Saxon Genitive form (e.g. "Threatening the Country's").
     */
    @Input
    @Processing
    @Attribute
    @Label("Remove labels ending in genitive form")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.LABELS)    
    public boolean enabled = true;

    private final static char [][] ENDINGS = new char [] []
    {
        "'s".toCharArray(), "`s".toCharArray(), "s'".toCharArray(), "s`".toCharArray()
    };

    @Override
    public boolean acceptPhrase(PreprocessingContext context, int phraseIndex)
    {
        final int [] wordIndices = context.allPhrases.wordIndices[phraseIndex];
        return isGenitive(context.allWords.image, wordIndices[wordIndices.length - 1]);
    }
    
    @Override
    public boolean acceptWord(PreprocessingContext context, int wordIndex)
    {
        return isGenitive(context.allWords.image, wordIndex);
    }

    private boolean isGenitive(char [][] wordImage, final int wordIndex)
    {
        char [] image = wordImage[wordIndex];

        outer: for (char [] ending : ENDINGS)
        {
            if (image.length >= ending.length)
            {
                for (int i = 0; i < ending.length; i++)
                {
                    if (image[image.length - ending.length + i] != ending[i])
                    {
                        continue outer;
                    }
                }
                return false;
            }
        }
        return true;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
