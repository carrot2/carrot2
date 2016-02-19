
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
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.attribute.*;

/**
 * Accepts labels that do not consist only of numeric tokens and start with a non-numeric
 * token.
 */
@Bindable(prefix = "NumericLabelFilter")
public class NumericLabelFilter extends SingleLabelFilterBase
{
    /**
     * Remove numeric labels. Remove labels that consist only of or start with numbers.
     */
    @Input
    @Processing
    @Attribute
    @Label("Remove numeric labels")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.LABELS)    
    public boolean enabled = true;

    @Override
    public boolean acceptPhrase(PreprocessingContext context, int phraseIndex)
    {
        final int [] wordIndices = context.allPhrases.wordIndices[phraseIndex];
        final short [] type = context.allWords.type;

        return !isNumeric(type[wordIndices[0]]);
    }

    @Override
    public boolean acceptWord(PreprocessingContext context, int wordIndex)
    {
        return !isNumeric(context.allWords.type[wordIndex]);
    }

    private final boolean isNumeric(short type)
    {
        return (type & ITokenizer.TYPE_MASK) == ITokenizer.TT_NUMERIC;
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
