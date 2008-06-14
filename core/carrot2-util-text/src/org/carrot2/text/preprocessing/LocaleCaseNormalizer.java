package org.carrot2.text.preprocessing;

import java.util.Locale;

import org.carrot2.text.*;
import org.carrot2.text.linguistic.*;

/**
 * Simple case normalization using Java {@link Locale} instances.
 */
public final class LocaleCaseNormalizer implements CaseNormalizerTask
{
    /**
     * Token sequence pointing to normalized images.
     */
    private int [] tokensNormalized;

    /*
     * 
     */
    public void normalize(CharSequenceIntMap tokenCoder, CharSequence [] allTokenImages,
        int [] allTokens, LanguageModelFactory languageFactory)
    {
        final MutableCharArray current = new MutableCharArray("");

        Locale locale = languageFactory.getCurrentLanguage().getLanguageCode()
            .getLocale();
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
