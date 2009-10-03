/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceUtils;

import com.google.common.collect.*;

/**
 * Holds lexical resources for one language.
 */
final class LexicalResources
{
    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(LexicalResources.class);

    /**
     * If we cannot find resources for some languages, emit warning once only.
     */
    final static EnumSet<LanguageCode> problemCache = EnumSet.noneOf(LanguageCode.class);

    final Set<MutableCharArray> stopwords;
    final List<Pattern> stoplabels;

    private LexicalResources(List<Pattern> stoplabels, Set<MutableCharArray> stopwords)
    {
        this.stoplabels = stoplabels;
        this.stopwords = stopwords;
    }

    static LexicalResources merge(Collection<LexicalResources> values)
    {
        final Set<MutableCharArray> mergedStopwords = Sets.newHashSet();
        final List<Pattern> mergedStoplabels = Lists.newArrayList();

        for (LexicalResources lexicalResources : values)
        {
            mergedStopwords.addAll(lexicalResources.stopwords);
            mergedStoplabels.addAll(lexicalResources.stoplabels);
        }

        return new LexicalResources(mergedStoplabels, mergedStopwords);
    }

    static LexicalResources load(ResourceUtils resourceLoaders, LanguageCode lang)
    {
        return new LexicalResources(loadStopLabels(resourceLoaders, lang), loadStopWords(
            resourceLoaders, lang));
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
                DefaultLanguageModelFactory.class);

            if (resource == null)
            {
                throw new IOException("Resource not found: " + resourceName);
            }

            for (String word : TextResourceUtils.load(resource))
            {
                result.add(new MutableCharArray(word.toLowerCase()));
            }

            return result;
        }
        catch (IOException e)
        {
            problemCache.add(lang);
            logger.warn("Common words could not be loaded for language "
                + lang.toString() + ": " + e.getMessage());
            return Collections.emptySet();
        }
    }

    /**
     * Loads stop labels associated with the given language. Logs an error and recovers
     * silently if the given resource cannot be found.
     */
    private static List<Pattern> loadStopLabels(ResourceUtils resourceLoaders,
        LanguageCode lang)
    {
        try
        {
            final ArrayList<Pattern> result = Lists.newArrayList();

            final String resourceName = "stoplabels." + lang.getIsoCode();
            final IResource resource = resourceLoaders.getFirst(resourceName,
                DefaultLanguageModelFactory.class);

            if (resource == null)
            {
                throw new IOException("Resource not found: " + resourceName);
            }

            for (String word : TextResourceUtils.load(resource))
            {
                try
                {
                    result.add(Pattern.compile(word));
                }
                catch (PatternSyntaxException e)
                {
                    problemCache.add(lang);
                    logger.warn("Ignoring regular expression with syntax error: " + word
                        + " in " + resourceName);
                }
            }

            return result;
        }
        catch (IOException e)
        {
            problemCache.add(lang);
            logger.warn("Stop labels for language " + lang.toString() + " not found.");
            return Collections.emptyList();
        }
    }
}
