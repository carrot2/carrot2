
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
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
public class StopLabelFilter
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

    public void filter(PreprocessingContext context,
                       ILexicalData lexicalData,
                       boolean insertSpace,
                       boolean [] acceptedStems,
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
                int wordIndex = mostFrequentOriginalWordIndex[stemIndex];
                final String formattedLabel = labelFormatter.format(context, wordIndex, insertSpace);
                boolean accept = !lexicalData.isStopLabel(formattedLabel);

                acceptedStems[stemIndex] = accept;
            }
        }

        for (int phraseIndex = 0; phraseIndex < acceptedPhrases.length; phraseIndex++)
        {
            if (acceptedPhrases[phraseIndex])
            {
                final String formattedLabel = labelFormatter.format(
                    context, phraseIndex + context.allWords.image.length, insertSpace);
                boolean accept = !lexicalData.isStopLabel(formattedLabel);

                acceptedPhrases[phraseIndex] = accept;
            }
        }
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
