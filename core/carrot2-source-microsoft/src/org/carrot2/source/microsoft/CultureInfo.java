
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

package org.carrot2.source.microsoft;

import org.carrot2.core.attribute.AttributeNames;


/**
 * Culture and language restriction.
 */
public enum CultureInfo
{
    /** 
     * Special constant indicating culture consistent with the
     * currently selected Carrot2 language. 
     * 
     * @see AttributeNames#ACTIVE_LANGUAGE
     */
    // CARROT2_CURRENT_LANGUAGE ("carrot2", "Carrot2 - current language"),

    ARABIC_ARABIA            ("ar-XA", "Arabic – Arabia"),
    BULGARIAN_BULGARIA       ("bg-BG", "Bulgarian – Bulgaria"),
    CHINESE_CHINA            ("zh-CN", "Chinese – China"),
    CHINESE_HONG_KONG_SAR    ("zh-HK", "Chinese – Hong Kong SAR"),
    CHINESE_TAIWAN           ("zh-TW", "Chinese – Taiwan"),
    CROATIAN_CROATIA         ("hr-HR", "Croatian – Croatia"),
    CZECH_CZECH_REPUBLIC     ("cs-CZ", "Czech – Czech Republic"),
    DANISH_DENMARK           ("da-DK", "Danish – Denmark"),
    DUTCH_BELGIUM            ("nl-BE", "Dutch – Belgium"),
    DUTCH_NETHERLANDS        ("nl-NL", "Dutch – Netherlands"),
    GERMAN_AUSTRIA           ("de-AT", "German – Austria"),
    GERMAN_GERMANY           ("de-DE", "German – Germany"),
    GERMAN_SWITZERLAND       ("de-CH", "German – Switzerland"),
    GREEK_GREECE             ("el-GR", "Greek – Greece"),
    ENGLISH_AUSTRALIA        ("en-AU", "English – Australia"),
    ENGLISH_ARABIA           ("en-XA", "English – Arabia"),
    ENGLISH_CANADA           ("en-CA", "English – Canada"),
    ENGLISH_INDIA            ("en-IN", "English – India"),
    ENGLISH_INDONESIA        ("en-ID", "English – Indonesia"),
    ENGLISH_IRELAND          ("en-IE", "English – Ireland"),
    ENGLISH_MALAYSIA         ("en-MY", "English – Malaysia"),
    ENGLISH_NEW_ZEALAND      ("en-NZ", "English – New Zealand"),
    ENGLISH_PHILIPPINES      ("en-PH", "English – Philippines"),
    ENGLISH_SINGAPORE        ("en-SG", "English – Singapore"),
    ENGLISH_SOUTH_AFRICA     ("en-ZA", "English – South Africa"),
    ENGLISH_UNITED_KINGDOM   ("en-GB", "English – United Kingdom"),
    ENGLISH_UNITED_STATES    ("en-US", "English – United States"),
    ESTONIAN_ESTONIA         ("et-EE", "Estonian – Estonia"),
    FINNISH_FINLAND          ("fi-FI", "Finnish – Finland"),
    FRENCH_BELGIUM           ("fr-BE", "French – Belgium"),
    FRENCH_FRANCE            ("fr-FR", "French – France"),
    FRENCH_CANADA            ("fr-CA", "French – Canada"),
    FRENCH_SWITZERLAND       ("fr-CH", "French – Switzerland"),
    HEBREW_ISRAEL            ("he-IL", "Hebrew – Israel"),
    HUNGARIAN_HUNGARY        ("hu-HU", "Hungarian – Hungary"),
    ITALIAN_ITALY            ("it-IT", "Italian – Italy"),
    JAPANESE_JAPAN           ("ja-JP", "Japanese – Japan"),
    KOREAN_KOREA             ("ko-KR", "Korean – Korea"),
    LATVIAN_LATVIA           ("lv-LV", "Latvian – Latvia"),
    LITHUANIAN_LITHUANIA     ("lt-LT", "Lithuanian – Lithuania"),
    NORWEGIAN_NORWAY         ("nb-NO", "Norwegian – Norway"),
    POLISH_POLAND            ("pl-PL", "Polish – Poland"),
    PORTUGUESE_BRAZIL        ("pt-BR", "Portuguese – Brazil"),
    PORTUGUESE_PORTUGAL      ("pt-PT", "Portuguese – Portugal"),
    ROMANIAN_ROMANIA         ("ro-RO", "Romanian – Romania"),
    RUSSIAN_RUSSIA           ("ru-RU", "Russian – Russia"),
    SLOVAK_SLOVAK_REPUBLIC   ("sk-SK", "Slovak – Slovak Republic"),
    SLOVENIAN_SLOVENIA       ("sl-SL", "Slovenian – Slovenia"),
    SPANISH_ARGENTINA        ("es-AR", "Spanish – Argentina"),
    SPANISH_CHILE            ("es-CL", "Spanish – Chile"),
    SPANISH_LATIN_AMERICA    ("es-XL", "Spanish – Latin America"),
    SPANISH_MEXICO           ("es-MX", "Spanish – Mexico"),
    SPANISH_SPAIN            ("es-ES", "Spanish – Spain"),
    SWEDISH_SWEDEN           ("sv-SE", "Swedish – Sweden"),
    SPANISH_UNITED_STATES    ("es-US", "Spanish – United States"),
    THAI_THAILAND            ("th-TH", "Thai – Thailand"),
    TURKISH_TURKEY           ("tr-TR", "Turkish – Turkey"),
    UKRAINIAN_UKRAINE        ("uk-UA", "Ukrainian – Ukraine");

    /**
     * Culture info code for Live API.
     */
    public final String cultureInfoCode;
    
    /**
     * Culture info description.
     */
    public final String description;

    private CultureInfo(String cultureInfo, String description)
    {
        this.cultureInfoCode = cultureInfo;
        this.description = description;
    }

    /**
     * Return human-readable description. 
     */
    @Override
    public String toString()
    {
        return description;
    }
}
