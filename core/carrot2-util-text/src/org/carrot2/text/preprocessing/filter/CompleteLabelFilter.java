package org.carrot2.text.preprocessing.filter;

import org.carrot2.core.attribute.Processing;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.DoubleRange;

/**
 *
 */
@Bindable
public class CompleteLabelFilter implements LabelFilter
{
    /**
     * Remove truncated phrases.
     */
    @Input
    @Processing
    @Attribute
    public boolean enabled = true;

    /**
     * Label override threshold.
     */
    @Input
    @Processing
    @Attribute
    @DoubleRange(min = 0.0, max = 1.0)
    public double labelOverrideCutoff = 0.5;

    /**
     * Left complete label filter.
     */
    private LeftCompleteLabelFilter leftCompleteLabelFilter = new LeftCompleteLabelFilter();

    /**
     * Right complete label filter.
     */
    private RightCompleteLabelFilter rightCompleteLabelFilter = new RightCompleteLabelFilter();

    public void filter(PreprocessingContext context, boolean [] acceptedWords,
        boolean [] acceptedPhrases)
    {
        if (!enabled)
        {
            return;
        }

        leftCompleteLabelFilter.filter(context, acceptedWords, acceptedPhrases,
            labelOverrideCutoff);
        rightCompleteLabelFilter.filter(context, acceptedWords, acceptedPhrases,
            labelOverrideCutoff);
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
