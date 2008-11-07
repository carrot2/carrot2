
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

import java.util.HashMap;

import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.*;

import com.google.common.collect.Maps;

/**
 * Accessor to all {@link LanguageModel} objects.
 */
@Bindable(prefix = "SnowballLanguageModelFactory")
public final class SnowballLanguageModelFactory implements LanguageModelFactory
{
    @Required
    @Processing
    @Input
    @Attribute(key = AttributeNames.ACTIVE_LANGUAGE)
    public LanguageCode current = LanguageCode.ENGLISH;

    /**
     * Reloads stopwords on every processing request.For best performance, stop word
     * reloading should be disabled in production.
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
     *
     */
    public LanguageModel getCurrentLanguage()
    {
        return getLanguage(current);
    }

    /**
     *
     */
    public LanguageModel getLanguage(LanguageCode language)
    {
        synchronized (SnowballLanguageModelFactory.class)
        {
            LanguageModel model = languages.get(language);
            if (model == null || reloadStopwords)
            {
                model = createLanguageModel(language);
                languages.put(language, model);
            }

            return model;
        }
    }

    /**
     * Instantiated and available languages.
     */
    private final static HashMap<LanguageCode, LanguageModel> languages = Maps
        .newHashMap();

    /**
     * Initialize languages. For now this assumes no language ever fails to load.
     */
    private LanguageModel createLanguageModel(LanguageCode languageCode)
    {
        final ResourceUtils resourceLoaders = ResourceUtilsFactory
            .getDefaultResourceUtils();
        return new SnowballLanguageModel(languageCode, resourceLoaders, mergeStopwords);
    }
}
