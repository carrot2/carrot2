
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * Codes for languages for which linguistic resources are available 
 * in {@link DefaultLanguageModelFactory}.
 * <p>
 * Notes about third-party or extra implementation needs.
 * <ul>
 *  <li>{@link #POLISH} makes use of Morfologik stemming library
 *  if it is available in the classpath.</li>
 * </ul> 
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
    POLISH ("pl"),
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

    /**
     * Return a {@link LanguageCode} constant for a given ISO code (or <code>null</code>)
     * if not available. 
     */
    public static LanguageCode forISOCode(String language)
    {
        language = language.toLowerCase();

        // Simple scan here, if the number of languages grows, switch to a hashmap.
        for (LanguageCode code : values()) {
            if (code.getIsoCode().equals(language)) {
                return code;
            }
        }

        return null;
    }
    
    @Override
    public String toString()
    {
        return StringUtils.capitalize(name().toLowerCase());
    }
}
