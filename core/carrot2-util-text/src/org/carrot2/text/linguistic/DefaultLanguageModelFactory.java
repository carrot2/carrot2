/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import java.util.*;
import java.util.regex.Pattern;

import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.*;

import com.google.common.collect.*;

/**
 * Accessor to all {@link ILanguageModel} objects. Default implementation provides support
 * for all languages listed in {@link LanguageCode}, but certain languages may require
 * additional resources (such as external stemming libraries). Refer to each language's
 * constant for specific information.
 * 
 * @see LanguageCode
 */
@Bindable(prefix = "DefaultLanguageModelFactory")
public final class DefaultLanguageModelFactory implements ILanguageModelFactory
{
    /**
     * The default language. This language is returned from {@link #getCurrentLanguage()}.
     */
    @Required
    @Processing
    @Input
    @Attribute(key = AttributeNames.ACTIVE_LANGUAGE)
    public LanguageCode current = LanguageCode.ENGLISH;

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
     * <code>false</code>, only stop words and stop labels of the active language will
     * be used. If set to <code>true</code>, stop words from all {@link LanguageCode}s
     * will be used together and stop labels from all languages will be used together, no
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
     * @see #current
     */
    public ILanguageModel getCurrentLanguage()
    {
        return getLanguage(current);
    }

    /**
     * @return Return a language model for one of the languages in {@link LanguageCode}.
     */
    public ILanguageModel getLanguage(LanguageCode language)
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
                            resourceLoaders, lang));
                    }

                    LEXICAL_RESOURCES_MERGED = LexicalResources
                        .merge(LEXICAL_RESOURCES_CACHE.values());
                }
                else
                {
                    // Load stopwords for this language only.
                    LEXICAL_RESOURCES_CACHE.put(language, LexicalResources.load(
                        resourceLoaders, language));
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

            final IStemmer stemmer = createStemmer(language);

            return new DefaultLanguageModel(language, lexicalResources, stemmer);
        }
    }

    /**
     * Provide an {@link IStemmer} implementation for a given language. 
     */
    private IStemmer createStemmer(LanguageCode language)
    {
        if (language == LanguageCode.POLISH)
        {
            return PolishStemmerFactory.createStemmer();
        }
        else
        {
            return SnowballStemmerFactory.createStemmer(language);
        }
    }
}
