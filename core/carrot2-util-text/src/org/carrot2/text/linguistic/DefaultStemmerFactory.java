
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

package org.carrot2.text.linguistic;

import java.util.EnumMap;

import org.carrot2.core.LanguageCode;
import org.carrot2.shaded.guava.common.base.Predicate;
import org.carrot2.shaded.guava.common.collect.Maps;
import org.carrot2.text.linguistic.lucene.ArabicStemmerAdapter;
import org.carrot2.text.linguistic.lucene.HindiStemmerAdapter;
import org.carrot2.text.linguistic.morfologik.MorfologikStemmerAdapter;
import org.carrot2.text.linguistic.snowball.SnowballProgram;
import org.carrot2.text.linguistic.snowball.stemmers.*;
import org.carrot2.util.annotations.ThreadSafe;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.factory.FallbackFactory;
import org.carrot2.util.factory.IFactory;
import org.carrot2.util.factory.NewClassInstanceFactory;
import org.carrot2.util.factory.SingletonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Bindable
@ThreadSafe
public class DefaultStemmerFactory implements IStemmerFactory
{
    private final static Logger logger = LoggerFactory.getLogger(DefaultStemmerFactory.class);

    private final static EnumMap<LanguageCode, IFactory<IStemmer>> stemmerFactories;
    
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
     * Initialize factories.
     */
    static
    {
        stemmerFactories = createDefaultStemmers();
    }


    @Override
    public IStemmer getStemmer(LanguageCode languageCode)
    {
        return stemmerFactories.get(languageCode).createInstance();
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
        map.put(LanguageCode.DANISH,     snowball(DanishStemmer.class));
        map.put(LanguageCode.DUTCH,      snowball(DutchStemmer.class));
        map.put(LanguageCode.ENGLISH,    snowball(EnglishStemmer.class));
        map.put(LanguageCode.FINNISH,    snowball(FinnishStemmer.class));
        map.put(LanguageCode.FRENCH,     snowball(FrenchStemmer.class));
        map.put(LanguageCode.GERMAN,     snowball(GermanStemmer.class));
        map.put(LanguageCode.HUNGARIAN,  snowball(HungarianStemmer.class));
        map.put(LanguageCode.ITALIAN,    snowball(ItalianStemmer.class));
        map.put(LanguageCode.NORWEGIAN,  snowball(NorwegianStemmer.class));
        map.put(LanguageCode.PORTUGUESE, snowball(PortugueseStemmer.class));
        map.put(LanguageCode.ROMANIAN,   snowball(RomanianStemmer.class));
        map.put(LanguageCode.RUSSIAN,    snowball(RussianStemmer.class));
        map.put(LanguageCode.SPANISH,    snowball(SpanishStemmer.class));
        map.put(LanguageCode.SWEDISH,    snowball(SwedishStemmer.class));
        map.put(LanguageCode.TURKISH,    snowball(TurkishStemmer.class));

        // Identity stemming for Chinese.
        map.put(LanguageCode.CHINESE_SIMPLIFIED, identity);
        
        // Specialized stemming for Hindi (ported from Lucene)
        map.put(LanguageCode.HINDI, new NewClassInstanceFactory<IStemmer>(HindiStemmerAdapter.class));

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

    private static IFactory<IStemmer> snowball(final Class<? extends SnowballProgram> clazz) {
      return new IFactory<IStemmer>() {
        @Override
        public IStemmer createInstance() {
          try {
            return new SnowballStemmerAdapter(clazz.newInstance());
          } catch (InstantiationException |IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        }
      };
    }
}
