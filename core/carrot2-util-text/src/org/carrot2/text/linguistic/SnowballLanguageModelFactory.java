package org.carrot2.text.linguistic;

import java.util.HashMap;

import org.carrot2.core.attribute.*;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.*;

import com.google.common.collect.Maps;

/**
 * Accessor to all {@link LanguageModel} objects.
 */
@Bindable
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
            if (languages == null)
            {
                languages = createLanguageModels();
            }

            return languages.get(language);
        }
    }

    /**
     * Instantiated and available languages.
     */
    private static HashMap<LanguageCode, LanguageModel> languages;

    /**
     * Initialize languages. For now this assumes no language ever fails to load.
     */
    private static HashMap<LanguageCode, LanguageModel> createLanguageModels()
    {
        final HashMap<LanguageCode, LanguageModel> languages = Maps.newHashMap();

        // Initialize all Snowball-based languages.
        final LanguageCode [] snowballLanguages = new LanguageCode []
        {
            LanguageCode.DANISH, LanguageCode.DUTCH, LanguageCode.ENGLISH,
            LanguageCode.FINNISH, LanguageCode.FRENCH, LanguageCode.GERMAN,
            LanguageCode.HUNGARIAN, LanguageCode.ITALIAN, LanguageCode.NORWEGIAN,
            LanguageCode.PORTUGUESE, LanguageCode.ROMANIAN, LanguageCode.RUSSIAN,
            LanguageCode.SPANISH, LanguageCode.SWEDISH, LanguageCode.TURKISH,
        };

        final ResourceUtils resourceLoaders = ResourceUtilsFactory
            .getDefaultResourceUtils();
        for (LanguageCode lang : snowballLanguages)
        {
            languages.put(lang, new SnowballLanguageModel(lang, resourceLoaders));
        }

        return languages;
    }
}
