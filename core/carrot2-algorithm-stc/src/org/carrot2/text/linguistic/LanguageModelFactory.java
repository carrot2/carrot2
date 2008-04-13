package org.carrot2.text.linguistic;

import java.util.HashMap;

import org.carrot2.core.attribute.Processing;
import org.carrot2.util.attribute.*;
import org.carrot2.util.resource.*;

import com.google.common.collect.Maps;

/**
 * Accessor to all {@link LanguageModel} objects.
 */
@Bindable
public final class LanguageModelFactory
{
    /**
     * Currently active language.
     */
    @Required
    @Processing
    @Input
    @Attribute
    private LanguageCode current = LanguageCode.ENGLISH;

    /**
     * @return Returns {@link LanguageModel} for the {@link #current} language or
     *         <code>null</code> if such language model is not available.
     */
    public LanguageModel getCurrentLanguage()
    {
        return getLanguage(current);
    }

    /**
     * @return Return a {@link LanguageModel} associated with the given code or
     *         <code>null</code> if this language is not supported or its resources are
     *         not available.
     */
    public LanguageModel getLanguage(LanguageCode language)
    {
        synchronized (LanguageModelFactory.class)
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
            languages.put(lang, new SnowballLanguageImpl(lang, resourceLoaders));
        }

        return languages;
    }
}
