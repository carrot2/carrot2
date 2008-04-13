package org.carrot2.text.preprocessing;

import java.util.Locale;

import org.carrot2.text.*;
import org.carrot2.text.linguistic.LanguageModel;

/**
 * Simple case normalization.
 */
public class CaseNormalizerTask
{
    /**
     * Token sequence pointing to normalized images.
     */
    private int [] tokensNormalized;

    /*
     * 
     */
    public void normalize(CharSequenceIntMap tokenCoder, CharSequence [] allTokenImages,
        int [] allTokens, LanguageModel language)
    {
        final MutableCharArray current = new MutableCharArray("");

        Locale locale = language.getLanguageCode().getLocale();
        if (locale == null)
        {
            locale = Locale.ENGLISH;
        }

        final int [] remapping = new int [allTokenImages.length];
        for (int i = 0; i < allTokenImages.length; i++)
        {
            final String normalized = allTokenImages[i].toString().toLowerCase(locale);
            current.reset(normalized);

            final int normalizedTokenCode = tokenCoder.getIndex(current);
            remapping[i] = normalizedTokenCode;
        }

        this.tokensNormalized = new int [allTokens.length];
        for (int i = 0; i < tokensNormalized.length; i++)
        {
            final int tokenCode = allTokens[i];
            if (tokenCode >= 0)
            {
                tokensNormalized[i] = remapping[allTokens[i]];
            }
            else
            {
                tokensNormalized[i] = tokenCode;
            }
        }
    }

    /*
     * 
     */
    public int [] getTokensNormalized()
    {
        return tokensNormalized;
    }
}
