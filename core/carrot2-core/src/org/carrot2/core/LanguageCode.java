
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

package org.carrot2.core;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.carrot2.util.StringUtils;
import org.slf4j.LoggerFactory;

/**
 * Codes for languages for which linguistic resources are available 
 * in <code>DefaultLanguageModelFactory</code>.
 * <p>
 * Notes about third-party or extra implementation needs.
 * <ul>
 *  <li>{@link #POLISH} makes use of Morfologik stemming library
 *  if it is available in the classpath.</li>
 *  <li>{@link #CHINESE_SIMPLIFIED} makes use of Lucene's <code>smartcn</code> tokenizer
 *  if it is available in the classpath.</li>
 *  <li>{@link #THAI} makes use of Lucene's <code>ThaiWordFilter</code>
 *  if it is available in the classpath.</li>
 * </ul> 
 */
public enum LanguageCode
{
    //
    // When adding constants here add appropriate mappings to Bing and other 
    // document sources.
    //
    
    ARABIC ("ar"),
    BULGARIAN ("bg"),
    CZECH ("cs"),
    CHINESE_SIMPLIFIED ("zh_cn"),
    CROATIAN("hr"),
    DANISH ("da"),
    DUTCH ("nl"),
    ENGLISH ("en"),
    ESTONIAN ("et"),
    FINNISH ("fi"),
    FRENCH ("fr"),
    GERMAN ("de"),
    GREEK ("el"),  
    HUNGARIAN ("hu"),
    HINDI("hi"),
    ITALIAN ("it"),
    IRISH ("ga"), // (Gaelic)
    JAPANESE ("ja"),
    KOREAN ("ko"),
    LATVIAN ("lv"),
    LITHUANIAN ("lt"),
    MALTESE ("mt"),
    NORWEGIAN ("no"),
    POLISH ("pl"),
    PORTUGUESE ("pt"),
    ROMANIAN ("ro"),
    RUSSIAN ("ru"),
    SLOVAK ("sk"),
    SLOVENE ("sl"),
    SPANISH ("es"),
    SWEDISH ("sv"),
    THAI ("th"),
    TURKISH ("tr");

    /**
     * ISO 639-1 code for this language. An underscore may separate additional country/region
     * variant, should it be relevant (as in Simplified and Traditional Chinese).
     * 
     * @see "https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes"
     */
    private final String isoCode;

    /**
     * Java {@link Locale} for this language (for case folding,
     * for example).
     */
    private final Locale locale;

    /** @see "http://issues.carrot2.org/browse/CARROT-1129" */
    private final static Map<String, AtomicBoolean> wrongCodes;

    /**
     * A hash map of all ISO language codes.
     */
    private final static Map<String, LanguageCode> isoToLangCode;
    static
    {
        isoToLangCode = new HashMap<>();
        for (LanguageCode langCode : values())
        {
            isoToLangCode.put(langCode.getIsoCode(), langCode);
        }
        
        // Incorrect language codes, supported for legacy reasons.
        wrongCodes = new HashMap<>();
        wrongCodes.put("gr", new AtomicBoolean());
        wrongCodes.put("cz", new AtomicBoolean());
        wrongCodes.put("ee", new AtomicBoolean());
        wrongCodes.put("ie", new AtomicBoolean());

        isoToLangCode.put("gr", LanguageCode.GREEK);
        isoToLangCode.put("cz", LanguageCode.CZECH);
        isoToLangCode.put("ee", LanguageCode.ESTONIAN);
        isoToLangCode.put("ie", LanguageCode.IRISH);
    }

    /**
     * A hash map of all languages which do NOT use space delimiters.
     */
    private final static Set<LanguageCode> noSpaceLanguages;
    static {
        noSpaceLanguages = EnumSet.of(
            LanguageCode.CHINESE_SIMPLIFIED,
            LanguageCode.JAPANESE);
    }

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

        // Emit the warning once.
        if (wrongCodes.containsKey(language) &&
            !wrongCodes.get(language).getAndSet(true)) {
          LoggerFactory.getLogger(LanguageCode.class).warn(
              "This language code was incorrect in C2 (see issue CARROT-1129), correct it to a proper ISO639-1: " + language);
        }

        return isoToLangCode.get(language);
    }
    
    /**
     * Returns <code>true</code> if this language uses space delimiters between words.
     * This is a hint for formatting cluster labels.
     */
    public boolean usesSpaceDelimiters() {
        return !noSpaceLanguages.contains(this);
    }

    @Override
    public String toString()
    {
        return StringUtils.identifierToHumanReadable(name());
    }
}
