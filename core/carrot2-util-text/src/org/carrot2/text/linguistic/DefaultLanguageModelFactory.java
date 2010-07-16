
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

import java.util.HashMap;
import java.util.regex.Pattern;

import org.carrot2.core.LanguageCode;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.analysis.ExtendedWhitespaceTokenizer;
import org.carrot2.text.analysis.ITokenizer;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.resource.ResourceUtils;
import org.carrot2.util.resource.ResourceUtilsFactory;

import com.google.common.collect.Maps;

/**
 * Accessor to all {@link ILanguageModel} objects. Default implementation provides support
 * for all languages listed in {@link LanguageCode} except for Chinese and Arabic.
 * 
 * @see LanguageCode
 */
@Bindable(prefix = "DefaultLanguageModelFactory")
public class DefaultLanguageModelFactory implements ILanguageModelFactory
{
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
     * instance has its own instance of {@link DefaultLanguageModelFactory}).
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
        switch (language)
        {
            case POLISH:
                /*
                 * For Polish, we use a dictionary-backed stemmer
                 * from the Morfologik project.
                 */
                return PolishStemmerFactory.createStemmer();

            case KOREAN:
                /*
                 * Korean is agglutinative, but the extent of affixes is unknown to
                 * me [DW] and I don't know whether a stemming engine for Korean 
                 * exists. We fall back to identity. 
                 */
                return IdentityStemmer.INSTANCE; 

            case BULGARIAN:
            case CZECH:
            case ESTONIAN:
            case GREEK:
            case IRISH:
            case LATVIAN:
            case LITHUANIAN:
            case MALTESE:
            case SLOVAK:
            case SLOVENE:
                /*
                 * No stemming engine for these languages at the moment.
                 */
                return IdentityStemmer.INSTANCE; 
                
            case CHINESE_SIMPLIFIED:
            case ARABIC:
                /*
                 * For Chinese and Arabic, use the ExtendedLanguageModelFactory.
                 */
                return IdentityStemmer.INSTANCE; 
                
                
            default:
                /*
                 * For other languages, try to use snowball's stemming. 
                 */
                return SnowballStemmerFactory.createStemmer(language);
        }
    }
    
    protected ITokenizer createTokenizer(LanguageCode language)
    {
        return new ExtendedWhitespaceTokenizer();
    }
}
