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

import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;
import org.carrot2.core.attribute.*;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.*;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Accessor to all {@link ILanguageModel} objects.
 */
@Bindable(prefix = "SnowballLanguageModelFactory")
public final class SnowballLanguageModelFactory implements ILanguageModelFactory
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
     * Reloads cached stopwords on every processing request for this factory. For best
     * performance, stop word reloading should be disabled in production.
     * 
     * @level Medium
     * @group Preprocessing
     * @label Reload stopwords
     */
    @Processing
    @Input
    @Attribute
    public boolean reloadStopwords = false;

    /**
     * Merges stopwords from all known languages. If set to <code>false</code>, only
     * stopwords of the active language will be used. If set to <code>true</code>,
     * stopwords from all {@link LanguageCode}s will be used together, no matter the
     * active language. Stopword merging is useful when clustering data in a mix of
     * different languages and should increase clustering quality in such settings.
     * 
     * @level Medium
     * @group Preprocessing
     * @label Merge stopwords
     */
    @Init
    @Processing
    @Input
    @Attribute
    public boolean mergeStopwords = true;

    /**
     * Preloaded and cached stopword lists, shared among all instances of this factory.
     */
    private final static HashMap<LanguageCode, Set<MutableCharArray>> stopwords_cache = Maps
        .newHashMap();

    /**
     * Preloaded and cached merged stopword lists.
     */
    private static Set<MutableCharArray> stopwords_merged = Sets.newHashSet();

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
        synchronized (SnowballLanguageModelFactory.class)
        {
            if (reloadStopwords || !stopwords_cache.containsKey(language)
                || (mergeStopwords && stopwords_merged == null))
            {
                /*
                 * We must reload stopwords or a language not already cached has been
                 * requested.
                 */
                final ResourceUtils resourceLoaders = ResourceUtilsFactory
                    .getDefaultResourceUtils();
                if (mergeStopwords)
                {
                    // Load stopwords for all languages.
                    for (LanguageCode lang : LanguageCode.values())
                    {
                        // Only reload if requested.
                        if (stopwords_cache.containsKey(lang) && !reloadStopwords) continue;

                        stopwords_cache.put(lang, loadStopWords(resourceLoaders, lang));
                    }

                    stopwords_merged = Sets.newHashSet();
                    for (Set<MutableCharArray> stopwords : stopwords_cache.values())
                    {
                        stopwords_merged.addAll(stopwords);
                    }
                }
                else
                {
                    // Load stopwords for this language only.
                    stopwords_cache.put(language, loadStopWords(resourceLoaders, language));
                }
            }

            final Set<MutableCharArray> stopwords;
            if (mergeStopwords)
            {
                stopwords = stopwords_merged;
            }
            else
            {
                stopwords = stopwords_cache.get(language);
            }

            return new SnowballLanguageModel(language, stopwords);
        }
    }

    /**
     * Loads common words associated with the given language. Logs an error and recovers
     * silently if the given resource cannot be found.
     */
    private static Set<MutableCharArray> loadStopWords(ResourceUtils resourceLoaders,
        LanguageCode lang)
    {
        try
        {
            final Set<MutableCharArray> result = Sets.newHashSet();

            final String resourceName = "stopwords." + lang.getIsoCode();
            final IResource resource = resourceLoaders.getFirst(resourceName,
                SnowballLanguageModelFactory.class);

            if (resource == null)
            {
                throw new IOException("Common words resource not found: " + resourceName);
            }

            for (String word : TextResourceUtils.load(resource))
            {
                result.add(new MutableCharArray(word));
            }

            return result;
        }
        catch (IOException e)
        {
            Logger.getLogger(SnowballLanguageModelFactory.class).error(
                "Failed to load " + "common words for language: " + lang.toString());

            return Collections.emptySet();
        }
    }
}
