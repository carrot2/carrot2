package org.carrot2.text.preprocessing;

import org.carrot2.text.*;
import org.carrot2.text.linguistic.*;

/**
 * 
 */
public final class StemmingTask
{
    /**
     * Token sequence pointing to stemmed images.
     */
    private int [] tokensStemmed;

    public void stem(CharSequenceIntMap tokenCoder, PreprocessingContext context,
        LanguageModel language)
    {
        final MutableCharArray current = new MutableCharArray("");
        final Stemmer stemmer = language.getStemmer();

        final CharSequence [] allTokenImages = context.allTokenImages;
        final int [] allTokens = (context.allTokensNormalized != null ? context.allTokensNormalized
            : context.allTokens);

        final int [] remapping = new int [allTokenImages.length];
        for (int i = 0; i < allTokenImages.length; i++)
        {
            current.reset(allTokenImages[i]);
            final CharSequence stemmed = stemmer.stem(current);
            current.reset(stemmed);

            if (stemmed != null)
            {
                final int stemmedTokenCode = tokenCoder.getIndex(current);
                remapping[i] = stemmedTokenCode;
            }
            else
            {
                remapping[i] = i;
            }
        }

        this.tokensStemmed = new int [allTokens.length];
        for (int i = 0; i < tokensStemmed.length; i++)
        {
            final int tokenCode = allTokens[i];
            tokensStemmed[i] = (tokenCode >= 0 ? remapping[allTokens[i]] : tokenCode);
        }
    }

    /*
     * 
     */
    public int [] getTokensStemmed()
    {
        return tokensStemmed;
    }
}
