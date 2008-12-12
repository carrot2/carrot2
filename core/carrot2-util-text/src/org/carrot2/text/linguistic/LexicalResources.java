package org.carrot2.text.linguistic;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Holds lexical resources for one language.
 */
final class LexicalResources
{
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

    static LexicalResources load(ResourceUtils resourceLoaders,
        LanguageCode lang)
    {
        return new LexicalResources(loadStopLabels(resourceLoaders, lang),
            loadStopWords(resourceLoaders, lang));
    }

    /**
     * Loads common words associated with the given language. Logs an error and
     * recovers silently if the given resource cannot be found.
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
                throw new IOException("Common words resource not found: "
                    + resourceName);
            }

            for (String word : TextResourceUtils.load(resource))
            {
                result.add(new MutableCharArray(word));
            }

            return result;
        }
        catch (IOException e)
        {
            Logger.getLogger(DefaultLanguageModelFactory.class).warn(
                "Common words for language: " + lang.toString() + " not found");

            return Collections.emptySet();
        }
    }

    /**
     * Loads stop labels associated with the given language. Logs an error and
     * recovers silently if the given resource cannot be found.
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
                throw new IOException("Stop labels resource not found: "
                    + resourceName);
            }

            for (String word : TextResourceUtils.load(resource))
            {
                try
                {
                    result.add(Pattern.compile(word));
                }
                catch (PatternSyntaxException e)
                {
                    Logger.getLogger(DefaultLanguageModelFactory.class).warn(
                        "Ignoring regular expression with syntax error: " + word
                            + " in " + resourceName);
                }
            }

            return result;
        }
        catch (IOException e)
        {
            Logger.getLogger(DefaultLanguageModelFactory.class).warn(
                "Stop labels for language: " + lang.toString() + " not found");

            return Collections.emptyList();
        }
    }
}