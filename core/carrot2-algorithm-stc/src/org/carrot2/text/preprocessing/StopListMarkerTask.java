package org.carrot2.text.preprocessing;

import org.carrot2.text.linguistic.LanguageModel;

/**
 * Marks common words as indicated by the current {@link LanguageModel}.
 */
final class StopListMarkerTask
{
    private boolean [] commonTermFlags;

    /*
     * 
     */
    public void mark(PreprocessingContext context, LanguageModel language)
    {
        this.commonTermFlags = new boolean [context.allTokenImages.length];
        
        final CharSequence [] images = context.allTokenImages;
        for (int i = 0; i < commonTermFlags.length; i++)
        {
            commonTermFlags[i] = language.isCommonWord(images[i]);
        }
    }

    /*
     * 
     */
    public boolean [] getCommonTermFlags()
    {
        return commonTermFlags;
    }

}
