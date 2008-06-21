package org.carrot2.text.preprocessing;

import org.carrot2.text.MutableCharArray;
import org.carrot2.text.linguistic.LanguageModel;

/**
 * Implementation of {@link PreprocessingTasks#MARK_TOKENS_STOPLIST}.
 */
final class StopListMarker
{
    /**
     * Marks stop words and saves the results to the <code>context</code>.
     */
    void mark(PreprocessingContext context, LanguageModel language)
    {
        final char [][] wordImages = context.allWords.image;
        final boolean [] commonTermFlags = new boolean [wordImages.length];
        final MutableCharArray current = new MutableCharArray("");

        for (int i = 0; i < commonTermFlags.length; i++)
        {
            current.reset(wordImages[i]);
            commonTermFlags[i] = language.isCommonWord(current);
        }

        context.allWords.commonTermFlag = commonTermFlags;
    }
}
