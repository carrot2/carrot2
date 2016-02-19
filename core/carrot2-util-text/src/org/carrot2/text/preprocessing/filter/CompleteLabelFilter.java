
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
import org.carrot2.util.attribute.constraint.DoubleRange;

/**
 * A filter that removes "incomplete" labels.
 * <p>
 * See <a href="http://project.carrot2.org/publications/osinski-2003-lingo.pdf">this
 * document</a>, page 31 for a definition of a complete phrase.
 */
@Bindable(prefix = "CompleteLabelFilter")
public class CompleteLabelFilter implements ILabelFilter
{
    /**
     * Remove truncated phrases. Tries to remove "incomplete" cluster labels. For example,
     * in a collection of documents related to <i>Data Mining</i>, the phrase
     * <i>Conference on Data</i> is incomplete in a sense that most likely it should be
     * <i>Conference on Data Mining</i> or even <i>Conference on Data Mining in Large
     * Databases</i>. When truncated phrase removal is enabled, the algorithm would try to
     * remove the "incomplete" phrases like the former one and leave only the more
     * informative variants.
     */
    @Input
    @Processing
    @Attribute
    @Label("Remove truncated phrases")
    @Level(AttributeLevel.BASIC)
    @Group(DefaultGroups.LABELS)    
    public boolean enabled = true;

    /**
     * Truncated label threshold. Determines the strength of the truncated label filter.
     * The lowest value means strongest truncated labels elimination, which may lead to
     * overlong cluster labels and many unclustered documents. The highest value
     * effectively disables the filter, which may result in short or truncated labels.
     */
    @Input
    @Processing
    @Attribute
    @DoubleRange(min = 0.0, max = 1.0)
    @Label("Truncated label threshold")
    @Level(AttributeLevel.ADVANCED)
    @Group(DefaultGroups.LABELS)    
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
