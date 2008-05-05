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
    CZECH_CZECH_REPUBLIC     ("cs-CZ", "Czech – Czech Republic"),
    DANISH_DENMARK           ("da-DK", "Danish – Denmark"),
    GERMAN_AUSTRIA           ("de-AT", "German – Austria"),
    GERMAN_SWITZERLAND       ("de-CH", "German – Switzerland"),
    GERMAN_GERMANY           ("de-DE", "German – Germany"),
    GREEK_GREECE             ("el-GR", "Greek – Greece"),
    ENGLISH_AUSTRALIA        ("en-AU", "English – Australia"),
    ENGLISH_CANADA           ("en-CA", "English – Canada"),
    ENGLISH_UNITED_KINGDOM   ("en-GB", "English – United Kingdom"),
    ENGLISH_INDONESIA        ("en-ID", "English – Indonesia"),
    ENGLISH_IRELAND          ("en-IE", "English – Ireland"),
    ENGLISH_INDIA            ("en-IN", "English – India"),
    ENGLISH_MALAYSIA         ("en-MY", "English – Malaysia"),
    ENGLISH_NEW_ZEALAND      ("en-NZ", "English – New Zealand"),
    ENGLISH_PHILIPPINES      ("en-PH", "English – Philippines"),
    ENGLISH_SINGAPORE        ("en-SG", "English – Singapore"),
    ENGLISH_UNITED_STATES    ("en-US", "English – United States"),
    ENGLISH_ARABIA           ("en-XA", "English – Arabia"),
    ENGLISH_SOUTH_AFRICA     ("en-ZA", "English – South Africa"),
    SPANISH_ARGENTINA        ("es-AR", "Spanish – Argentina"),
    SPANISH_CHILE            ("es-CL", "Spanish – Chile"),
    SPANISH_SPAIN            ("es-ES", "Spanish – Spain"),
    SPANISH_MEXICO           ("es-MX", "Spanish – Mexico"),
    SPANISH_UNITED_STATES    ("es-US", "Spanish – United States"),
    SPANISH_LATIN_AMERICA    ("es-XL", "Spanish – Latin America"),
    ESTONIAN_ESTONIA         ("et-EE", "Estonian – Estonia"),
    FINNISH_FINLAND          ("fi-FI", "Finnish – Finland"),
    FRENCH_BELGIUM           ("fr-BE", "French – Belgium"),
    FRENCH_CANADA            ("fr-CA", "French – Canada"),
    FRENCH_SWITZERLAND       ("fr-CH", "French – Switzerland"),
    FRENCH_FRANCE            ("fr-FR", "French – France"),
    HEBREW_ISRAEL            ("he-IL", "Hebrew – Israel"),
    CROATIAN_CROATIA         ("hr-HR", "Croatian – Croatia"),
    HUNGARIAN_HUNGARY        ("hu-HU", "Hungarian – Hungary"),
    ITALIAN_ITALY            ("it-IT", "Italian – Italy"),
    JAPANESE_JAPAN           ("ja-JP", "Japanese – Japan"),
    KOREAN_KOREA             ("ko-KR", "Korean – Korea"),
    LITHUANIAN_LITHUANIA     ("lt-LT", "Lithuanian – Lithuania"),
    LATVIAN_LATVIA           ("lv-LV", "Latvian – Latvia"),
    NORWEGIAN_NORWAY         ("nb-NO", "Norwegian – Norway"),
    DUTCH_BELGIUM            ("nl-BE", "Dutch – Belgium"),
    DUTCH_NETHERLANDS        ("nl-NL", "Dutch – Netherlands"),
    POLISH_POLAND            ("pl-PL", "Polish – Poland"),
    PORTUGUESE_BRAZIL        ("pt-BR", "Portuguese – Brazil"),
    PORTUGUESE_PORTUGAL      ("pt-PT", "Portuguese – Portugal"),
    ROMANIAN_ROMANIA         ("ro-RO", "Romanian – Romania"),
    RUSSIAN_RUSSIA           ("ru-RU", "Russian – Russia"),
    SLOVAK_SLOVAK_REPUBLIC   ("sk-SK", "Slovak – Slovak Republic"),
    SLOVENIAN_SLOVENIA       ("sl-SL", "Slovenian – Slovenia"),
    SWEDISH_SWEDEN           ("sv-SE", "Swedish – Sweden"),
    THAI_THAILAND            ("th-TH", "Thai – Thailand"),
    TURKISH_TURKEY           ("tr-TR", "Turkish – Turkey"),
    UKRAINIAN_UKRAINE        ("uk-UA", "Ukrainian – Ukraine"),
    CHINESE_CHINA            ("zh-CN", "Chinese – China"),
    CHINESE_HONG_KONG_SAR    ("zh-HK", "Chinese – Hong Kong SAR"),
    CHINESE_TAIWAN           ("zh-TW", "Chinese – Taiwan");

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
//    @Override  
//    public String toString()
//    {
//        return description;
//    }
}
