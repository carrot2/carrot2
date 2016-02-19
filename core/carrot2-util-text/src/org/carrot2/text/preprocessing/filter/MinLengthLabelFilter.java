
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
 * Accepts labels whose length in characters is greater or equal to the provided value.
 */
@Bindable(prefix = "MinLengthLabelFilter")
public class MinLengthLabelFilter extends SingleLabelFilterBase
{
    /**
     * Remove labels shorter than 3 characters. Removes labels whose total length in
     * characters, including spaces, is less than 3.
     */
    @Input
    @Processing
    @Attribute
    @Label("Remove short labels")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.LABELS)    
    public boolean enabled = true;

    private final static int MIN_LENGTH = 3;

    @Override
    public boolean acceptPhrase(PreprocessingContext context, int phraseIndex)
    {
        final int [] wordIndices = context.allPhrases.wordIndices[phraseIndex];
        char [][] wordImage = context.allWords.image;

        int wordIndex = 0;
        int length = wordImage[wordIndices[wordIndex++]].length;
        while (length < MIN_LENGTH && wordIndex < wordIndices.length)
        {
            length += wordImage[wordIndices[wordIndex]].length + 1 /* space */;
            wordIndex++;
        }

        return length >= MIN_LENGTH;
    }

    @Override
    public boolean acceptWord(PreprocessingContext context, int wordIndex)
    {
        return context.allWords.image[wordIndex].length >= MIN_LENGTH;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
