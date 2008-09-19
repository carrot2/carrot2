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
    /**
     * Currently active language.
     * 
     * @level Basic
     * @group Preprocessing
     */
    @Required
    @Processing
    @Input
    @Attribute(key = AttributeNames.ACTIVE_LANGUAGE)
    public LanguageCode current = LanguageCode.ENGLISH;

    /**
     * Reload stopwords. Reloads stop words file on every processing request. For best
     * performance, stop word reloading should be disabled in production.
     * 
     * @level Medium
     * @group Preprocessing
     */
    @Processing
    @Input
    @Attribute
    public boolean reloadStopwords = false;

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
    private static LanguageModel createLanguageModel(LanguageCode languageCode)
    {
        final ResourceUtils resourceLoaders = ResourceUtilsFactory
            .getDefaultResourceUtils();
        return new SnowballLanguageModel(languageCode, resourceLoaders);
    }
}
