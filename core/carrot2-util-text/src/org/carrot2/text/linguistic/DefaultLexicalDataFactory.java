
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

import static org.carrot2.util.resource.ResourceLookup.Location.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.carrot2.core.LanguageCode;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Internal;
import org.carrot2.core.attribute.Processing;
import org.carrot2.text.util.MutableCharArray;
import org.carrot2.util.CharArrayUtils;
import org.carrot2.util.annotations.AspectModified;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.DefaultGroups;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.ResourceCache;
import org.carrot2.util.resource.ResourceLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carrotsearch.hppc.ObjectHashSet;
import org.carrot2.shaded.guava.common.base.Function;
import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;
import org.carrot2.shaded.guava.common.collect.Sets;

/**
 * The default management of lexical resources. Resources are read from disk, cached and shared 
 * between <b>all</b> threads using this class. Additional attributes control resource reloading
 * and merging: {@link #resourceLookup}, {@link #reloadResources}, 
 * {@link #mergeResources}.
 */
@Bindable(inherit = LexicalDataLoader.class)
public class DefaultLexicalDataFactory implements ILexicalDataFactory
{
    /** */
    final static Logger logger = LoggerFactory.getLogger(DefaultLexicalDataFactory.class);

    private final static Function<ResourceLookup, HashMap<LanguageCode, ILexicalData>> resourceLoader =
        new Function<ResourceLookup, HashMap<LanguageCode, ILexicalData>>()
    {
        public java.util.HashMap<LanguageCode, ILexicalData> apply(ResourceLookup resourceLookup) {
            return reloadResources(resourceLookup);
        }

        public boolean equals(Object other) {
            throw new UnsupportedOperationException();
        }

        public int hashCode()
        {
            throw new UnsupportedOperationException();
        }
    };
    
    /**
     * Static shared cache of lexical resources, keyed by a {@link ResourceLookup} 
     * used to search for resources. 
     */
    private final static ResourceCache<HashMap<LanguageCode, ILexicalData>> cache 
        = new ResourceCache<HashMap<LanguageCode, ILexicalData>>(resourceLoader);

    @Processing
    @Input
    @Attribute(key = "reload-resources", inherit = true)
    public boolean reloadResources = false;

    /**
     * Merges stop words and stop labels from all known languages. If set to
     * <code>false</code>, only stop words and stop labels of the active language will be
     * used. If set to <code>true</code>, stop words from all {@link org.carrot2.core.LanguageCode}s will
     * be used together and stop labels from all languages will be used together, no
     * matter the active language. Lexical resource merging is useful when clustering data
     * in a mix of different languages and should increase clustering quality in such
     * settings.
     */
    @Init
    @Processing
    @Input
    @Attribute(key = "merge-resources")
    @Label("Merge lexical resources")
    @Level(AttributeLevel.MEDIUM)
    @Group(DefaultGroups.PREPROCESSING)
    public boolean mergeResources = true;

    @Init
    @Processing
    @Input 
    @Internal
    @Attribute(key = "resource-lookup", inherit = true)
    @ImplementingClasses(classes = {}, strict = false)
    @AspectModified("Substituted with an assembly lookup in .NET release")
    public ResourceLookup resourceLookup = new ResourceLookup(CONTEXT_CLASS_LOADER);

    /**
     * The main logic for acquiring a shared {@link ILexicalData} instance.
     */
    @Override
    public ILexicalData getLexicalData(LanguageCode languageCode)
    {
        // If resource merging is in place, change the language code to null
        // (dedicated cache key).
        if (mergeResources)
        {
            languageCode = null;
        }

        // Prepare cache key.
        ILexicalData lexicalData = cache.get(resourceLookup, reloadResources).get(languageCode);

        // Reset reload resources trigger.
        reloadResources = false;
        
        return lexicalData;
    }

    /**
     * Reload all lexical resources associated with the given key.
     */
    private static HashMap<LanguageCode, ILexicalData> reloadResources(ResourceLookup resourceLookup)
    {
        // Load lexical resources.
        ObjectHashSet<MutableCharArray> mergedStopwords = new ObjectHashSet<>();
        ArrayList<Pattern> mergedStoplabels = Lists.newArrayList();

        HashMap<LanguageCode, ILexicalData> resourceMap = Maps.newHashMap();
        for (LanguageCode languageCode : LanguageCode.values())
        {
            final String isoCode = languageCode.getIsoCode();

            ObjectHashSet<MutableCharArray> stopwords = toLower(load(resourceLookup, "stopwords." + isoCode));
            ArrayList<Pattern> stoplabels = 
                compile(load(resourceLookup, "stoplabels." + isoCode));

            mergedStopwords.addAll(stopwords);
            mergedStoplabels.addAll(stoplabels);
            
            resourceMap.put(languageCode, new DefaultLexicalData(stopwords, stoplabels));
        }
        resourceMap.put(null, new DefaultLexicalData(mergedStopwords, mergedStoplabels));

        return resourceMap;
    }

    /**
     * All entries to lowercase.
     */
    private static ObjectHashSet<MutableCharArray> toLower(Set<String> input)
    {
        ObjectHashSet<MutableCharArray> cloned = 
            new ObjectHashSet<MutableCharArray>(input.size());

        for (String entry : input)
        {
            char [] chars = entry.toCharArray();
            CharArrayUtils.toLowerCaseInPlace(chars);
            cloned.add(new MutableCharArray(chars));
        }

        return cloned;
    }

    /**
     * Compile patterns. 
     */
    private static ArrayList<Pattern> compile(HashSet<String> patterns)
    {
        ArrayList<Pattern> compiled = new ArrayList<Pattern>(patterns.size());
        for (String pattern : patterns)
        {
            try
            {
                compiled.add(Pattern.compile(pattern));
            }
            catch (PatternSyntaxException e)
            {
                logger.warn("Ignoring invalid regular expression: " + pattern);
            }
        }

        return compiled;
    }

    /**
     * Attempts to load <code>resourceName</code> from the provided {@link ResourceLookup}.
     */
    private static HashSet<String> load(ResourceLookup resourceLookup, String resourceName)
    {
        final IResource resource = resourceLookup.getFirst(resourceName);
        if (resource == null)
        {
            throw new RuntimeException(
                "No resource named " + resourceName + 
                " in resource lookup locations: " + 
                Arrays.toString(resourceLookup.getLocators()));
        }
        else
        {
            try
            {
                return load(resource);
            }
            catch (IOException e)
            {
                throw new RuntimeException(
                    "Resource named " + resourceName + 
                    " failed to load from: " + resource.toString());
            }
        }
    }

    /**
     * Loads words from a given {@link IResource} (UTF-8, one word per line, #-starting lines 
     * are considered comments).
     */
    public static HashSet<String> load(IResource resource) throws IOException
    {
        final HashSet<String> words = Sets.newHashSet();

        final InputStream is = resource.open();
        if (is == null)
            throw new IOException("Resource returned null stream: " + resource);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(is,
            "UTF-8"));
        
        try
        {
    
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (line.startsWith("#") || line.length() == 0)
                {
                    continue;
                }
    
                words.add(line);
            }
        }
        finally
        {
            reader.close();
        }

        return words;
    }
}
