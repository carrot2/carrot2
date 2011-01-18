
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

import java.io.IOException;
import java.io.StringReader;
import java.util.EnumMap;

import org.carrot2.core.LanguageCode;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.text.analysis.ExtendedWhitespaceTokenizer;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.lucene.ArabicStemmerAdapter;
import org.carrot2.text.linguistic.lucene.ChineseTokenizerAdapter;
import org.carrot2.text.linguistic.lucene.SnowballStemmerFactory;
import org.carrot2.text.linguistic.morfologik.MorfologikStemmerAdapter;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.factory.CachedInstanceFactoryDecorator;
import org.carrot2.util.factory.FallbackFactory;
import org.carrot2.util.factory.IFactory;
import org.carrot2.util.factory.NewClassInstanceFactory;
import org.carrot2.util.factory.SingletonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

/**
 * A factory of {@link ILanguageModel} objects.
 * 
 * @see LanguageCode
 */
@Bindable
public class DefaultLanguageModelFactory implements ILanguageModelFactory
{
    private final static Logger logger = LoggerFactory.getLogger(DefaultLanguageModelFactory.class);

    private final static EnumMap<LanguageCode, IFactory<IStemmer>> stemmerFactories;
    private final static EnumMap<LanguageCode, IFactory<ITokenizer>> tokenizerFactories;

    /**
     * Functional verification for {@link IStemmer}.
     */
    private final static Predicate<IStemmer> stemmerVerifier = new Predicate<IStemmer>()
    {
        @Override
        public boolean apply(IStemmer stemmer)
        {
            // Assume functional if there's no exception.
            stemmer.stem("verification");
            return true;
        }
    };

    /**
     * Functional verification for {@link ITokenizer}.
     */
    private final static Predicate<ITokenizer> tokenizerVerifier = new Predicate<ITokenizer>()
    {
        @Override
        public boolean apply(ITokenizer tokenizer)
        {
            // Assume functional if there's no exception.
            try
            {
                tokenizer.reset(new StringReader("verify"));
                tokenizer.nextToken();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }

            return true;
        }
    };

    /**
     * Initialize factories.
     */
    static
    {
        stemmerFactories = createDefaultStemmers();
        tokenizerFactories = createDefaultTokenizers();
    }

    /**
     * Factory providing {@link ILexicalData} for each language.
     * 
     * @group Preprocessing
     * @level Advanced
     * @label Lexical data factory
     */
    @Init
    @Input
    @Internal
    @Attribute
    @ImplementingClasses(classes = {}, strict = false)
    public ILexicalDataFactory lexicalDataFactory = new DefaultLexicalDataFactory();

    /*
     * 
     */
    @Override
    public ILanguageModel getLanguageModel(final LanguageCode languageCode)
    {
        return new DefaultLanguageModel(
            languageCode, 
            new CachedInstanceFactoryDecorator<IStemmer>(stemmerFactories.get(languageCode)),
            new CachedInstanceFactoryDecorator<ITokenizer>(tokenizerFactories.get(languageCode)),
            new CachedInstanceFactoryDecorator<ILexicalData>(new IFactory<ILexicalData>() {
                @Override
                public ILexicalData createInstance()
                {
                    return lexicalDataFactory.getLexicalData(languageCode);
                }
            }));
    }

    /**
     * Create default stemmer factories.
     */
    private static EnumMap<LanguageCode, IFactory<IStemmer>> createDefaultStemmers()
    {
        final IFactory<IStemmer> identity = new SingletonFactory<IStemmer>(new IdentityStemmer());
        final EnumMap<LanguageCode, IFactory<IStemmer>> map = Maps.newEnumMap(LanguageCode.class);

        // Adapters to third-party libraries.
        map.put(LanguageCode.POLISH,     new NewClassInstanceFactory<IStemmer>(MorfologikStemmerAdapter.class));
        map.put(LanguageCode.ARABIC,     new NewClassInstanceFactory<IStemmer>(ArabicStemmerAdapter.class));

        // Adapters to snowball.
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

        // Identity stemming for Chinese.
        map.put(LanguageCode.CHINESE_SIMPLIFIED, identity);

        // Decorate everything with a fallback identity stemmer.
        for (LanguageCode lc : LanguageCode.values())
        {
            if (map.containsKey(lc))
            {
                IFactory<IStemmer> factory = map.get(lc);
                if (factory != identity)
                {
                    factory = new FallbackFactory<IStemmer>(
                        factory, identity, stemmerVerifier,
                        logger, "Stemmer for "
                            + lc.toString() + " (" + lc.getIsoCode() + ") is not available."
                            + " This may degrade clustering quality of " 
                            + lc.toString() + " content. Cause: {}");

                    map.put(lc, factory);
                }
            }
            else
            {
                map.put(lc, identity);
            }
        }

        return map;
    }

    /**
     * Create default tokenizer factories.
     */
    private static EnumMap<LanguageCode, IFactory<ITokenizer>> createDefaultTokenizers()
    {
        EnumMap<LanguageCode, IFactory<ITokenizer>> map = Maps.newEnumMap(LanguageCode.class);

        // By default, we use our own tokenizer for all languages.
        IFactory<ITokenizer> whitespaceTokenizerFactory = 
            new NewClassInstanceFactory<ITokenizer>(ExtendedWhitespaceTokenizer.class);

        for (LanguageCode lc : LanguageCode.values())
        {
            map.put(lc, whitespaceTokenizerFactory);
        }

        // Chinese is an exception, we use an adapter around a tokenizer from Lucene.
        map.put(LanguageCode.CHINESE_SIMPLIFIED, 
            new NewClassInstanceFactory<ITokenizer>(ChineseTokenizerAdapter.class));

        // Decorate everything with a fallback tokenizer.
        for (LanguageCode lc : LanguageCode.values())
        {
            if (map.containsKey(lc))
            {
                IFactory<ITokenizer> factory = map.get(lc);
                if (factory != whitespaceTokenizerFactory)
                {
                    map.put(lc, new FallbackFactory<ITokenizer>(
                        factory, whitespaceTokenizerFactory, tokenizerVerifier,
                        logger, "Tokenizer for "
                            + lc.toString() + " (" + lc.getIsoCode() + ") is not available."
                            + " This may degrade clustering quality of " 
                            + lc.toString() + " content. Cause: {}"));
                }
            }
            else
            {
                map.put(lc, whitespaceTokenizerFactory);
            }
        }

        return map;
    }    
}