
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
import org.carrot2.text.linguistic.ILexicalData;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.attribute.*;

/**
 * Accepts labels that are not declared as stop labels in the stoplabels.&lt;lang&gt;
 * files.
 */
@Bindable(prefix = "StopLabelFilter")
public class StopLabelFilter extends SingleLabelFilterBase
{
    /**
     * Remove stop labels. Removes labels that are declared as stop labels in the
     * stoplabels.&lt;lang&gt; files. Please note that adding a long list of regular
     * expressions to the stoplabels file may result in a noticeable performance penalty.
     */
    @Input
    @Processing
    @Attribute
    @Label("Remove stop labels")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.LABELS)    
    public boolean enabled = true;

    /*
     * 
     */
    public final LabelFormatter labelFormatter = new LabelFormatter();

    /*
     * 
     */
    public ILexicalData lexicalData;

    @Override
    public void filter(PreprocessingContext context, boolean [] acceptedStems,
        boolean [] acceptedPhrases)
    {
        lexicalData = context.language.getLexicalData();

        super.filter(context, acceptedStems, acceptedPhrases);
    }
    
    @Override
    public boolean acceptPhrase(PreprocessingContext context, int phraseIndex)
    {
        final String formatedLabel = labelFormatter.format(context, phraseIndex
            + context.allWords.image.length);
        return !lexicalData.isStopLabel(formatedLabel);
    }

    @Override
    public boolean acceptWord(PreprocessingContext context, int wordIndex)
    {
        final String formattedLabel = labelFormatter.format(context, wordIndex);
        return !lexicalData.isStopLabel(formattedLabel);
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
