
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
import java.util.HashMap;
import java.util.regex.Pattern;

import org.carrot2.core.LanguageCode;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.text.linguistic.lucene.ArabicStemmerFactory;
import org.carrot2.text.linguistic.lucene.ChineseSimplifiedTokenizerFactory;
import org.carrot2.text.linguistic.lucene.SnowballStemmerFactory;
import org.carrot2.text.linguistic.morfologik.PolishStemmerFactory;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.resource.ResourceUtils;
import org.carrot2.util.resource.ResourceUtilsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * A factory of {@link ILanguageModel} objects. Internally, for a number of languages,
 * this class creates adapters from Lucene's stemmers and tokenizers to Carrot2-specific
 * interfaces. 
 * 
 * @see LanguageCode
 */
@Bindable(prefix = "DefaultLanguageModelFactory")
public class DefaultLanguageModelFactory implements ILanguageModelFactory
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
     * Lexical resources path. A path within the classpath to load lexical resources from.
     * For example, if resource path is <code>/my/custom/resources</code>, stopwords
     * for English will be loaded from
     * <code>/my/custom/resources/stopwords.en</code>. Other lexical resources
     * and other languages will be loaded in the same way.
     * 
     * @group Preprocessing
     * @level Advanced
     * @label Lexical resources path
     */
    @Init
    @Input
    @Attribute(key = "resource-path")
    public String resourcePath = "/";
    
    /**
     * Reloads cached stop words and stop labels on every processing request. For best
     * performance, lexical resource reloading should be disabled in production.
     * 
     * @level Medium
     * @group Preprocessing
     * @label Reload lexical resources
     */
    @Processing
    @Input
    @Attribute
    public boolean reloadResources = false;

    /**
     * Merges stop words and stop labels from all known languages. If set to
     * <code>false</code>, only stop words and stop labels of the active language will be
     * used. If set to <code>true</code>, stop words from all {@link LanguageCode}s will
     * be used together and stop labels from all languages will be used together, no
     * matter the active language. Lexical resource merging is useful when clustering data
     * in a mix of different languages and should increase clustering quality in such
     * settings.
     * 
     * @level Medium
     * @group Preprocessing
     * @label Merge lexical resources
     */
    @Init
    @Processing
    @Input
    @Attribute
    public boolean mergeResources = true;

    /**
     * Preloaded and cached lexical resources, shared among all instances of this factory.
     * Instances of {@link Pattern} are immutable and thread safe, so we're fine to share
     * them across concurrent threads.
     */
    private final static HashMap<LanguageCode, LexicalResources> LEXICAL_RESOURCES_CACHE = Maps
        .newHashMap();

    /**
     * Preloaded and cached merged lexical resources.
     */
    private static LexicalResources LEXICAL_RESOURCES_MERGED;

    /**
     * A stemmer cache for this particular factory. As opposed to lexical resources, which
     * are cached globally, stemmer are cached on a per-factory basis to make sure
     * stemmers are not shared between processing threads (each processing component
     * instance has its own instance of {@link ILanguageModelFactory}).
     */
    private final HashMap<LanguageCode, IStemmer> stemmerCache = Maps.newHashMap();
    
    /**
     * A tokenizer cache for this particular factory.
     */
    private final HashMap<LanguageCode, ITokenizer> tokenizerCache = Maps.newHashMap();

    /**
     * @return Return a language model for one of the languages in {@link LanguageCode}.
     */
    public final ILanguageModel getLanguageModel(LanguageCode language)
    {
        synchronized (DefaultLanguageModelFactory.class)
        {
            if (reloadResources || !LEXICAL_RESOURCES_CACHE.containsKey(language)
                || (mergeResources && LEXICAL_RESOURCES_MERGED == null))
            {
                /*
                 * We must reload resources or a language not already cached has been
                 * requested.
                 */
                final ResourceUtils resourceLoaders = ResourceUtilsFactory
                    .getDefaultResourceUtils();
                if (mergeResources)
                {
                    // Load stopwords for all languages.
                    for (LanguageCode lang : LanguageCode.values())
                    {
                        // Only reload if requested.
                        if (LEXICAL_RESOURCES_CACHE.containsKey(lang) && !reloadResources)
                        {
                            continue;
                        }

                        LEXICAL_RESOURCES_CACHE.put(lang, LexicalResources.load(
                            resourceLoaders, lang, resourcePath));
                    }

                    LEXICAL_RESOURCES_MERGED = LexicalResources
                        .merge(LEXICAL_RESOURCES_CACHE.values());
                }
                else
                {
                    // Load stopwords for this language only.
                    LEXICAL_RESOURCES_CACHE.put(language, LexicalResources.load(
                        resourceLoaders, language, resourcePath));
                }
            }
        }

        final LexicalResources lexicalResources;
        if (mergeResources)
        {
            lexicalResources = LEXICAL_RESOURCES_MERGED;
        }
        else
        {
            lexicalResources = LEXICAL_RESOURCES_CACHE.get(language);
        }

        IStemmer stemmer;
        synchronized (stemmerCache)
        {
            stemmer = stemmerCache.get(language);
            if (!stemmerCache.containsKey(language))
            {
                stemmer = createStemmer(language);
                stemmerCache.put(language, stemmer);
            }
        }

        ITokenizer tokenizer;
        synchronized (tokenizerCache)
        {
            tokenizer = tokenizerCache.get(language);
            if (!tokenizerCache.containsKey(language))
            {
                tokenizer = createTokenizer(language);
                tokenizerCache.put(language, tokenizer);
            }
        }
        
        return new DefaultLanguageModel(language, lexicalResources, stemmer, tokenizer);
    }
    
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
                    + " This may degrade clustering quality of " + lc.toString() + " content.";

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
                    + " This may degrade clustering quality of " + lc.toString() + " content.";

                logger.warn(message);
            }
        }
        
        return map;
    }    
}
