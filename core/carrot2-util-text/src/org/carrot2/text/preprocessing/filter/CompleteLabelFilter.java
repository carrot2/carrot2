package org.carrot2.text.preprocessing.filter;

import org.carrot2.core.attribute.Processing;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.DoubleRange;

/**
 * A filter that removes "incomplete" labels.
 * <p>
 * See {@link http://project.carrot2.org/publications/osinski-2003-lingo.pdf}, page 31 for
 * a definition of a complete phrase.
 */
@Bindable
public class CompleteLabelFilter implements LabelFilter
{
    /**
     * Remove truncated phrases. Try to remove "incomplete" cluster labels, e.g. prefer
     * "Conference on Data" to "Conference on Data Mining".
     * 
     * @level Basic
     * @group Label filtering
     */
    @Input
    @Processing
    @Attribute
    public boolean enabled = true;

    /**
     * Truncated label threshold. Determines the strength of the truncated label filter.
     * The lowest value means strongest truncated labels elimination, which may lead to
     * overlong cluster labels and many unclustered documents. The highest value
     * effectively disables the filter, which may result in short or truncated labels.
     * 
     * @level Advanced
     * @group Phrase extraction
     */
    @Input
    @Processing
    @Attribute
    @DoubleRange(min = 0.0, max = 1.0)
    public double labelOverrideThreshold = 0.65;

    /**
     * Left complete label filter.
     */
    private LeftCompleteLabelFilter leftCompleteLabelFilter = new LeftCompleteLabelFilter();

    /**
     * Right complete label filter.
     */
    private RightCompleteLabelFilter rightCompleteLabelFilter = new RightCompleteLabelFilter();

    /**
     * Marks incomplete labels.
     */
    public void filter(PreprocessingContext context, boolean [] acceptedStems,
        boolean [] acceptedPhrases)
    {
        if (!enabled)
        {
            return;
        }

        leftCompleteLabelFilter.filter(context, acceptedStems, acceptedPhrases,
            labelOverrideThreshold);
        rightCompleteLabelFilter.filter(context, acceptedStems, acceptedPhrases,
            labelOverrideThreshold);
    }

    public boolean isEnabled()
    {
        return enabled;
    }
}
