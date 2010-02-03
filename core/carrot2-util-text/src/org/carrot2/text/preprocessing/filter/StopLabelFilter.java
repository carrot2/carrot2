
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
     * 
     * @level Basic
     * @group Label filtering
     * @label Remove stop labels
     */
    @Input
    @Processing
    @Attribute
    public boolean enabled = true;

    private final LabelFormatter labelFormatter = new LabelFormatter();

    @Override
    public boolean acceptPhrase(PreprocessingContext context, int phraseIndex)
    {
        final String formatedLabel = labelFormatter.format(context, phraseIndex
            + context.allWords.image.length);
        return !context.language.isStopLabel(formatedLabel);
    }

    @Override
    public boolean acceptWord(PreprocessingContext context, int wordIndex)
    {
        final String formattedLabel = labelFormatter.format(context, wordIndex);
        return !context.language.isStopLabel(formattedLabel);
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
