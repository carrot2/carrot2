
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

package org.carrot2.text.linguistic;

import java.util.EnumMap;

import org.carrot2.core.LanguageCode;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.util.attribute.Bindable;
import org.tartarus.snowball.ext.DanishStemmer;
import org.tartarus.snowball.ext.DutchStemmer;
import org.tartarus.snowball.ext.EnglishStemmer;
import org.tartarus.snowball.ext.FinnishStemmer;
import org.tartarus.snowball.ext.FrenchStemmer;
import org.tartarus.snowball.ext.GermanStemmer;
import org.tartarus.snowball.ext.HungarianStemmer;
import org.tartarus.snowball.ext.ItalianStemmer;
import org.tartarus.snowball.ext.NorwegianStemmer;
import org.tartarus.snowball.ext.PortugueseStemmer;
import org.tartarus.snowball.ext.RomanianStemmer;
import org.tartarus.snowball.ext.RussianStemmer;
import org.tartarus.snowball.ext.SpanishStemmer;
import org.tartarus.snowball.ext.SwedishStemmer;
import org.tartarus.snowball.ext.TurkishStemmer;

import com.google.common.collect.Maps;

/**
 * A factory of {@link ILanguageModel} objects. Internally, for a number of languages,
 * this class creates adapters from Lucene's stemmers and tokenizers to Carrot2-specific
 * interfaces. This is the only class in Carrot2 core that depends on Lucene APIs. 
 * 
 * @see LanguageCode
 */
@Bindable(prefix = "DefaultLanguageModelFactory")
public class DefaultLanguageModelFactory extends BaseLanguageModelFactory
{
    /**
     * Stemmer factories or fallbacks.
     */
    private final static EnumMap<LanguageCode, IStemmerFactory> stemmerFactories = 
        createDefaultStemmers();

    /**
     * Tokenizer factories or fallbacks.
     */
    private final static EnumMap<LanguageCode, ITokenizerFactory> tokenizerFactories = 
        createDefaultTokenizers();

    /**
     * Provide an {@link IStemmer} implementation for a given language.
     */
    protected IStemmer createStemmer(LanguageCode language)
    {
        IStemmerFactory factory = stemmerFactories.get(language);
        return (factory != null ? factory.createInstance() : IdentityStemmer.INSTANCE);  
    }

    /**
     * Provide a {@link ITokenizer} implementation for a given language.
     */
    @Override
    protected ITokenizer createTokenizer(LanguageCode language)
    {
        return tokenizerFactories.get(language).createInstance();
    }

    /**
     * Create default stemmer factories.
     */
    private static EnumMap<LanguageCode, IStemmerFactory> createDefaultStemmers()
    {
        EnumMap<LanguageCode, IStemmerFactory> map = Maps.newEnumMap(LanguageCode.class);

        map.put(LanguageCode.POLISH,     new PolishStemmerFactory());
        map.put(LanguageCode.ARABIC,     new ArabicStemmerFactory());

        map.put(LanguageCode.DANISH,     new SnowballStemmerFactory(DanishStemmer.class));
        map.put(LanguageCode.DUTCH,      new SnowballStemmerFactory(DutchStemmer.class));
        map.put(LanguageCode.ENGLISH,    new SnowballStemmerFactory(EnglishStemmer.class));
        map.put(LanguageCode.FINNISH,    new SnowballStemmerFactory(FinnishStemmer.class));
        map.put(LanguageCode.FRENCH,     new SnowballStemmerFactory(FrenchStemmer.class));
        map.put(LanguageCode.GERMAN,     new SnowballStemmerFactory(GermanStemmer.class));
        map.put(LanguageCode.HUNGARIAN,  new SnowballStemmerFactory(HungarianStemmer.class));
        map.put(LanguageCode.ITALIAN,    new SnowballStemmerFactory(ItalianStemmer.class));
        map.put(LanguageCode.NORWEGIAN,  new SnowballStemmerFactory(NorwegianStemmer.class));
        map.put(LanguageCode.PORTUGUESE, new SnowballStemmerFactory(PortugueseStemmer.class));
        map.put(LanguageCode.ROMANIAN,   new SnowballStemmerFactory(RomanianStemmer.class));
        map.put(LanguageCode.RUSSIAN,    new SnowballStemmerFactory(RussianStemmer.class));
        map.put(LanguageCode.SPANISH,    new SnowballStemmerFactory(SpanishStemmer.class));
        map.put(LanguageCode.SWEDISH,    new SnowballStemmerFactory(SwedishStemmer.class));
        map.put(LanguageCode.TURKISH,    new SnowballStemmerFactory(TurkishStemmer.class));

        /*
         * Chinese uses identity stemmer.
         */
        map.put(LanguageCode.CHINESE_SIMPLIFIED, new IdentityStemmerFactory());

        return map;
    }

    /**
     * Create default tokenizers.
     */
    private static EnumMap<LanguageCode, ITokenizerFactory> createDefaultTokenizers()
    {
        EnumMap<LanguageCode, ITokenizerFactory> map = Maps.newEnumMap(LanguageCode.class);

        /*
         * We use our own analyzer for all languages, including Arabic. 
         * 
         * For Arabic, Lucene has a version with special support for Nonspacing-Mark 
         * characters (see http://www.fileformat.info/info/unicode/category/Mn/index.htm), 
         * but we have them included as letters in the parser anyway.
         */

        ITokenizerFactory defaultTokenizerFactory = new ExtendedWhitespaceTokenizerFactory();
        for (LanguageCode lc : LanguageCode.values())
        {
            map.put(lc, defaultTokenizerFactory);
        }

        map.put(LanguageCode.CHINESE_SIMPLIFIED, new ChineseSimplifiedTokenizerFactory());
        return map;
    }    
}
