
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

package org.carrot2.source.boss;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.source.SearchEngineResponse;
import org.simpleframework.xml.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Search response model for Yahoo Boss.
 */
@Root(name = "ysearchresponse", strict = false)
final class YSearchResponse
{
    @Attribute(name = "responsecode", required = false)
    public Integer responseCode;

    @Element(name = "nextpage", required = false)
    public String nextPageURI;

    @Element(name = "resultset_web", required = false)
    public WebResultSet webResultSet;

    @Element(name = "resultset_news", required = false)
    public NewsResultSet newsResultSet;

    @Element(name = "resultset_images", required = false)
    public ImagesResultSet imagesResultSet;
    
    @Element(name = "language", required = false)
    public String language;

    /**
     * Populate {@link SearchEngineResponse} depending on the type of the search result
     * returned.
     * 
     * @param response
     * @param requestedLanguage the language requested by the user, mapped from
     *            {@link BossLanguageCodes} to {@link LanguageCode}.
     */
    public void populate(SearchEngineResponse response, LanguageCode requestedLanguage)
    {
        if (webResultSet != null)
        {
            response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY,
                webResultSet.deephits);

            if (webResultSet.results != null)
            {
                for (WebResult result : webResultSet.results)
                {
                    final Document document = new Document(result.title, result.summary,
                        result.url);

                    document.setField(Document.CLICK_URL, result.clickURL);

                    try
                    {
                        document.setField(Document.SIZE, Long.parseLong(result.size));
                    }
                    catch (NumberFormatException e)
                    {
                        // Ignore if cannot parse.
                    }

                    response.results.add(document);
                }
            }
        }
        else if (newsResultSet != null)
        {
            response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY,
                newsResultSet.deephits);

            if (newsResultSet.results != null)
            {
                final Set<String> unknownLanguages = Sets.newHashSet();
                for (NewsResult result : newsResultSet.results)
                {
                    final Document document = new Document(result.title, result.summary,
                        result.url);

                    document.setField(Document.CLICK_URL, result.clickURL);
                    if (StringUtils.isNotBlank(result.source))
                    {
                        document.setField(Document.SOURCES, Lists.newArrayList(result.source));
                    }
                    
                    // BOSS news returns language name as a string, but there is no list
                    // of supported values in the documentation. It seems that the strings
                    // are parallel to LanguageCode enum names, so we use them here.
                    if (StringUtils.isNotBlank(result.language))
                    {
                        try
                        {
                            document.setLanguage(LanguageCode.valueOf(result.language));
                        }
                        catch (IllegalArgumentException ignored)
                        {
                            unknownLanguages.add(result.language);
                        }
                    }
                    
                    response.results.add(document);
                }
                
                // Log unknown languages, if any
                if (!unknownLanguages.isEmpty())
                {
                    org.slf4j.LoggerFactory.getLogger(this.getClass().getName()).warn(
                        "Unknown language: " + unknownLanguages.toString());
                }
            }
        }
        else if (imagesResultSet != null)
        {
            response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY,
                imagesResultSet.deephits);

            if (imagesResultSet.results != null)
            {
                for (ImageResult result : imagesResultSet.results)
                {
                    final Document document = new Document(result.title, result.summary, result.refererURL);

                    // We use the image's referer page as the target click for the title.
                    document.setField(Document.CLICK_URL, result.refererClickURL);

                    // Attach thumbnail URL.
                    document.setField(Document.THUMBNAIL_URL, result.thumbnailURL);
                    response.results.add(document);
                }
            }
        }
        
        // If language has not been set based on the response, set it based on the request
        if (requestedLanguage != null) {
            for (Document document : response.results)
            {
                if (document.getLanguage() == null)
                {
                    document.setLanguage(requestedLanguage);
                }
            }
        }
    }
}
