package org.carrot2.text.preprocessing.filter;

import org.carrot2.text.preprocessing.PreprocessingContext;

/**
 *
 */
public interface LabelFilter
{
    public void filter(PreprocessingContext context, boolean [] acceptedWords,
        boolean [] acceptedPhrases);
    
    public boolean isEnabled();
}
