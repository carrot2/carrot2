
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

import java.io.StringReader;
import java.util.EnumMap;

import org.carrot2.core.LanguageCode;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.lucene.*;
import org.carrot2.text.linguistic.morfologik.PolishStemmerFactory;
import org.carrot2.util.attribute.Bindable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

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
    private final static Logger logger = LoggerFactory.getLogger(DefaultLanguageModel.class);

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

        map.put(LanguageCode.DANISH,     new SnowballStemmerFactory("org.tartarus.snowball.ext.DanishStemmer"));
        map.put(LanguageCode.DUTCH,      new SnowballStemmerFactory("org.tartarus.snowball.ext.DutchStemmer"));
        map.put(LanguageCode.ENGLISH,    new SnowballStemmerFactory("org.tartarus.snowball.ext.EnglishStemmer"));
        map.put(LanguageCode.FINNISH,    new SnowballStemmerFactory("org.tartarus.snowball.ext.FinnishStemmer"));
        map.put(LanguageCode.FRENCH,     new SnowballStemmerFactory("org.tartarus.snowball.ext.FrenchStemmer"));
        map.put(LanguageCode.GERMAN,     new SnowballStemmerFactory("org.tartarus.snowball.ext.GermanStemmer"));
        map.put(LanguageCode.HUNGARIAN,  new SnowballStemmerFactory("org.tartarus.snowball.ext.HungarianStemmer"));
        map.put(LanguageCode.ITALIAN,    new SnowballStemmerFactory("org.tartarus.snowball.ext.ItalianStemmer"));
        map.put(LanguageCode.NORWEGIAN,  new SnowballStemmerFactory("org.tartarus.snowball.ext.NorwegianStemmer"));
        map.put(LanguageCode.PORTUGUESE, new SnowballStemmerFactory("org.tartarus.snowball.ext.PortugueseStemmer"));
        map.put(LanguageCode.ROMANIAN,   new SnowballStemmerFactory("org.tartarus.snowball.ext.RomanianStemmer"));
        map.put(LanguageCode.RUSSIAN,    new SnowballStemmerFactory("org.tartarus.snowball.ext.RussianStemmer"));
        map.put(LanguageCode.SPANISH,    new SnowballStemmerFactory("org.tartarus.snowball.ext.SpanishStemmer"));
        map.put(LanguageCode.SWEDISH,    new SnowballStemmerFactory("org.tartarus.snowball.ext.SwedishStemmer"));
        map.put(LanguageCode.TURKISH,    new SnowballStemmerFactory("org.tartarus.snowball.ext.TurkishStemmer"));

        /*
         * Chinese uses identity stemmer.
         */
        map.put(LanguageCode.CHINESE_SIMPLIFIED, new IdentityStemmerFactory());

        /*
         * Check for stemmer availability and replace with a fallback if not available.
         */
        for (LanguageCode lc : Sets.newTreeSet(map.keySet()))
        {
            try
            {
                map.get(lc).createInstance().stem("test");
            }
            catch (Throwable t)
            {
                map.put(lc, new IdentityStemmerFactory());

                String message = "Stemmer for "
                    + lc.toString() + " (" + lc.getIsoCode() + ") is not available."
                    + " This may degrade clustering accurracy.";

                logger.warn(message);
            }
        }
        
        return map;
    }

    /**
     * Create default tokenizers.
     */
    private static EnumMap<LanguageCode, ITokenizerFactory> createDefaultTokenizers()
    {
        EnumMap<LanguageCode, ITokenizerFactory> map = Maps.newEnumMap(LanguageCode.class);

        /*
         * We use our own analyzer for all languages
         */
        ITokenizerFactory defaultTokenizerFactory = new ExtendedWhitespaceTokenizerFactory();
        for (LanguageCode lc : LanguageCode.values())
        {
            map.put(lc, defaultTokenizerFactory);
        }

        /*
         * Chinese is an exception, we use an adapter around a tokenizer from Lucene.
         */
        map.put(LanguageCode.CHINESE_SIMPLIFIED, new ChineseSimplifiedTokenizerFactory());
        
        /*
         * Check for tokenizer availability and replace with a fallback if not available.
         */
        for (LanguageCode lc : Sets.newTreeSet(map.keySet()))
        {
            try
            {
                map.get(lc).createInstance().reset(new StringReader("test"));
            }
            catch (Throwable t)
            {
                map.put(lc, new ExtendedWhitespaceTokenizerFactory());

                String message = "Tokenizer for "
                    + lc.toString() + " (" + lc.getIsoCode() + ") is not available."
                    + " This may degrade clustering accurracy.";

                logger.warn(message);
            }
        }
        
        return map;
    }    
}
