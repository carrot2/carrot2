
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

package org.carrot2.source.microsoft;

import java.util.Collections;
import java.util.Map;

import org.carrot2.core.LanguageCode;

import org.carrot2.shaded.guava.common.collect.Maps;


/**
 * Language and country/region information for {@link Bing3DocumentSource}.
 */
public enum MarketOption
{
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
    GERMAN_AUSTRIA           ("de-AT", "German – Austria"),
    GERMAN_GERMANY           ("de-DE", "German – Germany"),
    GERMAN_SWITZERLAND       ("de-CH", "German – Switzerland"),
    GREEK_GREECE             ("el-GR", "Greek – Greece"),
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
    SPANISH_UNITED_STATES    ("es-US", "Spanish – United States"),
    SWEDISH_SWEDEN           ("sv-SE", "Swedish – Sweden"),
    THAI_THAILAND            ("th-TH", "Thai – Thailand"),
    TURKISH_TURKEY           ("tr-TR", "Turkish – Turkey"),
    UKRAINIAN_UKRAINE        ("uk-UA", "Ukrainian – Ukraine");

    /**
     * Maps <b>some</b> of the values of this enum to {@link LanguageCode}s.
     */
    public static final Map<MarketOption, LanguageCode> TO_LANGUAGE_CODE;
    
    static 
    {
        final Map<MarketOption, LanguageCode> map = Maps.newEnumMap(MarketOption.class);
        
        map.put(ARABIC_ARABIA, LanguageCode.ARABIC);
        map.put(CHINESE_CHINA, LanguageCode.CHINESE_SIMPLIFIED);
        // TODO: Hong Kong uses traditional Chinese I believe.
        map.put(CHINESE_HONG_KONG_SAR, LanguageCode.CHINESE_SIMPLIFIED);
        map.put(CHINESE_TAIWAN, LanguageCode.CHINESE_SIMPLIFIED);
        map.put(CROATIAN_CROATIA, LanguageCode.CROATIAN);
        map.put(DANISH_DENMARK, LanguageCode.DANISH);
        map.put(DUTCH_BELGIUM, LanguageCode.DUTCH);
        map.put(DUTCH_NETHERLANDS, LanguageCode.DUTCH);
        map.put(ENGLISH_AUSTRALIA, LanguageCode.ENGLISH);
        map.put(ENGLISH_ARABIA, LanguageCode.ENGLISH);
        map.put(ENGLISH_CANADA, LanguageCode.ENGLISH);
        map.put(ENGLISH_INDIA, LanguageCode.ENGLISH);
        map.put(ENGLISH_INDONESIA, LanguageCode.ENGLISH);
        map.put(ENGLISH_IRELAND, LanguageCode.ENGLISH);
        map.put(ENGLISH_MALAYSIA, LanguageCode.ENGLISH);
        map.put(ENGLISH_NEW_ZEALAND, LanguageCode.ENGLISH);
        map.put(ENGLISH_PHILIPPINES, LanguageCode.ENGLISH);
        map.put(ENGLISH_SOUTH_AFRICA, LanguageCode.ENGLISH);
        map.put(ENGLISH_UNITED_KINGDOM, LanguageCode.ENGLISH);
        map.put(ENGLISH_UNITED_STATES, LanguageCode.ENGLISH);
        map.put(FINNISH_FINLAND, LanguageCode.FINNISH);
        map.put(FRENCH_BELGIUM, LanguageCode.FRENCH);
        map.put(FRENCH_FRANCE, LanguageCode.FRENCH);
        map.put(FRENCH_CANADA, LanguageCode.FRENCH);
        map.put(FRENCH_SWITZERLAND, LanguageCode.FRENCH);
        map.put(GERMAN_AUSTRIA, LanguageCode.GERMAN);
        map.put(GERMAN_GERMANY, LanguageCode.GERMAN);
        map.put(GERMAN_SWITZERLAND, LanguageCode.GERMAN);
        map.put(HUNGARIAN_HUNGARY, LanguageCode.HUNGARIAN);
        map.put(ITALIAN_ITALY, LanguageCode.ITALIAN);
        map.put(KOREAN_KOREA, LanguageCode.KOREAN);
        map.put(NORWEGIAN_NORWAY, LanguageCode.NORWEGIAN);
        map.put(POLISH_POLAND, LanguageCode.POLISH);
        map.put(PORTUGUESE_BRAZIL, LanguageCode.PORTUGUESE);
        map.put(PORTUGUESE_PORTUGAL, LanguageCode.PORTUGUESE);
        map.put(ROMANIAN_ROMANIA, LanguageCode.ROMANIAN);
        map.put(RUSSIAN_RUSSIA, LanguageCode.RUSSIAN);
        map.put(SPANISH_ARGENTINA, LanguageCode.SPANISH);
        map.put(SPANISH_CHILE, LanguageCode.SPANISH);
        map.put(SPANISH_LATIN_AMERICA, LanguageCode.SPANISH);
        map.put(SPANISH_MEXICO, LanguageCode.SPANISH);
        map.put(SPANISH_SPAIN, LanguageCode.SPANISH);
        map.put(SPANISH_UNITED_STATES, LanguageCode.SPANISH);
        map.put(SWEDISH_SWEDEN, LanguageCode.SWEDISH);
        map.put(TURKISH_TURKEY, LanguageCode.TURKISH);
        map.put(THAI_THAILAND, LanguageCode.THAI);
        
        TO_LANGUAGE_CODE = Collections.unmodifiableMap(map);
    }
    
    /**
     * Culture info code for Live API.
     */
    public final String marketCode;
    
    /**
     * Culture info description.
     */
    public final String description;

    private MarketOption(String cultureInfo, String description)
    {
        this.marketCode = cultureInfo;
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
    
    /**
     * Returns a corresponding {@link LanguageCode} or <code>null</code> if no
     * {@link LanguageCode} corresponds to this {@link MarketOption} constant.
     */
    public LanguageCode toLanguageCode()
    {
        return TO_LANGUAGE_CODE.get(this);
    }
}
