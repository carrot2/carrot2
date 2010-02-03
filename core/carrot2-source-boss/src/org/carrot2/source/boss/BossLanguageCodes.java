
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.source.boss;

import java.util.Collections;
import java.util.Map;

import org.carrot2.core.LanguageCode;
import org.carrot2.util.attribute.constraint.IValueHintMapping;

import com.google.common.collect.Maps;

/**
 * Yahoo Boss region and language codes.
 * 
 * @see <a href="http://developer.yahoo.com/search/boss/boss_guide/supp_regions_lang.html">Supported regions</a>
 */
public enum BossLanguageCodes implements IValueHintMapping
{
    ARGENTINA("Argentina", "ar", "es"),
    AUSTRIA("Austria", "at", "de"),
    AUSTRALIA("Australia", "au", "en"),
    BRAZIL("Brazil", "br", "pt"),
    CANADA_ENGLISH("Canada – English", "ca", "en"),
    CANADA_FRENCH("Canada – French", "ca", "fr"),
    CATALAN("Catalan", "ct", "ca"),
    CHILE("Chile", "cl", "es"),
    COLUMBIA("Columbia", "co", "es"),
    CZECH_REPUBLIC("Czech Republic", "cz", "cs"),
    DENMARK("Denmark", "dk", "da"),
    FINLAND("Finland", "fi", "fi"),
    FRENCH("French", "fr", "fr"),
    GERMAN("German", "de", "de"),
    HONG_KONG("Hong Kong", "hk", "tzh"),
    HUNGARY("Hungary", "hu", "hu"),
    INDIA("India", "in", "en"),
    INDONESIA_ENGLISH("Indonesia – English", "id", "en"),
    INDONESIA_INDONESIAN("Indonesia – Indonesian", "id", "id"),
    ISRAEL("Israel", "il", "he"),
    ITALIAN("Italian", "it", "it"),
    JAPAN("Japan", "jp", "jp"),
    KOREA("Korea", "kr", "kr"),
    MALAYSIA_ENGLISH("Malaysia – English", "my", "en"),
    MALAYSIA_MALAYSIAN("Malaysia", "my", "ms"),
    MEXICO("Mexico", "mx", "es"),
    NETHERLANDS("Netherlands", "nl", "nl"),
    NEW_ZEALAND("New Zealand", "nz", "en"),
    NORWAY("Norway", "no", "no"),
    PERU("Peru", "pe", "es"),
    PHILIPPINES("Philippines", "ph", "tl"),
    PHILIPPINES_ENGLISH("Philippines – English", "ph", "en"),
    ROMANIA("Romania", "ro", "ro"),
    RUSSIA("Russia", "ru", "ru"),
    SINGAPORE("Singapore", "sg", "en"),
    SPANISH("Spanish", "es", "es"),
    SWEDEN("Sweden", "se", "sv"),
    SWITZERLAND_GERMAN("Switzerland – German", "ch", "de"),
    SWITZERLAND_FRENCH("Switzerland – French", "ch", "fr"),
    SWITZERLAND_ITALIAN("Switzerland – Italian", "ch", "it"),
    TAIWAN("Taiwan", "tw", "tzh"),
    THAILAND("Thailand", "th", "th"),
    TURKEY("Turkey", "tr", "tr"),
    UNITED_KINGDOM("United Kingdom", "uk", "en"),
    UNITED_STATES("United States – English", "us", "en"),
    UNITED_STATES_SPANISH("United States – Spanish", "us", "es"),
    VIETNAM("Vietnam", "vn", "vi"),
    VENEZUELA("Venezuela", "ve", "es");

    /**
     * Maps <b>some</b> of the values of this enum to {@link LanguageCode}s.
     */
    private static final Map<BossLanguageCodes, LanguageCode> TO_LANGUAGE_CODE;
    static 
    {
        final Map<BossLanguageCodes, LanguageCode> map = Maps.newEnumMap(BossLanguageCodes.class);
        
        // TODO: Hong Kong uses traditional Chinese, I believe.
        map.put(HONG_KONG, LanguageCode.CHINESE_SIMPLIFIED);
        map.put(TAIWAN, LanguageCode.CHINESE_SIMPLIFIED);
        map.put(DENMARK, LanguageCode.DANISH);
        map.put(NETHERLANDS, LanguageCode.DUTCH);
        map.put(AUSTRALIA, LanguageCode.ENGLISH);
        map.put(CANADA_ENGLISH, LanguageCode.ENGLISH);
        map.put(INDIA, LanguageCode.ENGLISH);
        map.put(INDONESIA_ENGLISH, LanguageCode.ENGLISH);
        map.put(MALAYSIA_ENGLISH, LanguageCode.ENGLISH);
        map.put(PHILIPPINES_ENGLISH, LanguageCode.ENGLISH);
        map.put(NEW_ZEALAND, LanguageCode.ENGLISH);
        map.put(SINGAPORE, LanguageCode.ENGLISH);
        map.put(UNITED_KINGDOM, LanguageCode.ENGLISH);
        map.put(UNITED_STATES, LanguageCode.ENGLISH);
        map.put(FINLAND, LanguageCode.FINNISH);
        map.put(FRENCH, LanguageCode.FRENCH);
        map.put(AUSTRIA, LanguageCode.GERMAN);
        map.put(GERMAN, LanguageCode.GERMAN);
        map.put(SWITZERLAND_GERMAN, LanguageCode.GERMAN);
        map.put(HUNGARY, LanguageCode.HUNGARIAN);
        map.put(ITALIAN, LanguageCode.ITALIAN);
        map.put(KOREA, LanguageCode.KOREAN);
        map.put(NORWAY, LanguageCode.NORWEGIAN);
        map.put(BRAZIL, LanguageCode.PORTUGUESE);
        map.put(ROMANIA, LanguageCode.ROMANIAN);
        map.put(RUSSIA, LanguageCode.RUSSIAN);
        map.put(ARGENTINA, LanguageCode.SPANISH);
        map.put(CHILE, LanguageCode.SPANISH);
        map.put(COLUMBIA, LanguageCode.SPANISH);
        map.put(MEXICO, LanguageCode.SPANISH);
        map.put(PERU, LanguageCode.SPANISH);
        map.put(SPANISH, LanguageCode.SPANISH);
        map.put(UNITED_STATES_SPANISH, LanguageCode.SPANISH);
        map.put(VENEZUELA, LanguageCode.SPANISH);
        map.put(SWEDEN, LanguageCode.SWEDISH);
        map.put(TURKEY, LanguageCode.TURKISH);
        
        TO_LANGUAGE_CODE = Collections.unmodifiableMap(map);
    }
    
    public final String friendlyName;
    public final String langCode;
    public final String regionCode;

    private BossLanguageCodes(String friendlyName, String regionCode, String langCode)
    {
        this.friendlyName = friendlyName;
        this.langCode = langCode;
        this.regionCode = regionCode;
    }

    public String getAttributeValue()
    {
        return name();
    }

    public String getUserFriendlyName()
    {
        return friendlyName;
    }
    
    @Override
    public String toString()
    {
        return getUserFriendlyName();
    }
    
    /**
     * Returns a corresponding {@link LanguageCode} or <code>null</code> if no
     * {@link LanguageCode} corresponds to this {@link BossLanguageCodes} constant.
     */
    public LanguageCode toLanguageCode()
    {
        return TO_LANGUAGE_CODE.get(this);
    }
}
