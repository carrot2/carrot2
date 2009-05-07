
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

package org.carrot2.source.boss;

import org.carrot2.util.attribute.constraint.IValueHintMapping;

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
    DENMARK("Denmark", "dk", "da"),
    FINLAND("Finland", "fi", "fi"),
    FRENCH("French", "fr", "fr"),
    GERMAN("German", "de", "de"),
    INDIA("India", "in", "en"),
    INDONESIA_ENGLISH("Indonesia – English", "id", "en"),
    INDONESIA_INDONESIAN("Indonesia – Indonesian", "id", "id"),
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
    RUSSIA("Russia", "ru", "ru"),
    SINGAPORE("Singapore", "sg", "en"),
    SPANISH("Spanish", "es", "es"),
    SWEDEN("Sweden", "se", "sv"),
    SWITZERLAND_GERMAN("Switzerland – German", "ch", "de"),
    SWITZERLAND_FRENCH("Switzerland – French", "ch", "fr"),
    SWITZERLAND_ITALIAN("Switzerland – Italian", "ch", "it"),
    THAILAND("Thailand", "th", "th"),
    UNITED_KINGDOM("United Kingdom", "uk", "en"),
    UNITED_STATES("United States – English", "us", "en"),
    UNITED_STATES_SPANISH("United States – Spanish", "us", "es"),
    VIETNAM("Vietnam", "vn", "vi"),
    VENEZUELA("Venezuela", "ve", "es");

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
}
