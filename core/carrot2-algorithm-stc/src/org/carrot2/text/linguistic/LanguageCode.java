package org.carrot2.text.linguistic;

import java.util.Locale;

/**
 * Codes for languages for which linguistic resources are available.
 */
public enum LanguageCode
{
    DANISH ("da"),
    DUTCH ("nl"),
    ENGLISH ("en"),
    FINNISH ("fi"),
    FRENCH ("fr"),
    GERMAN ("de"),
    HUNGARIAN ("hu"),
    ITALIAN ("it"),
    NORWEGIAN ("no"),
    PORTUGUESE ("pt"),
    ROMANIAN ("ro"),
    RUSSIAN ("ru"),
    SPANISH ("es"),
    SWEDISH ("sv"),
    TURKISH ("tr");

    /**
     * ISO code for this language.
     */
    private final String isoCode;

    /**
     * Java {@link Locale} for this language (for case folding,
     * for example).
     */
    private final Locale locale;

    /**
     * 
     */
    private LanguageCode(String isoCode)
    {
        this.isoCode = isoCode;
        this.locale = new Locale(isoCode);
    }

    /**
     * @return ISO code for this language.
     */
    public String getIsoCode()
    {
        return isoCode;
    }
    
    /**
     * @return Returns {@link Locale} associated with the given 
     * language.
     */
    public Locale getLocale()
    {
        return locale;
    }
}
