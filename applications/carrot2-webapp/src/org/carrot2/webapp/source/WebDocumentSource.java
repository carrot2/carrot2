
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2015, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.webapp.source;

import java.util.List;
import java.util.Map;

import org.carrot2.core.Document;
import org.carrot2.core.IControllerContext;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingException;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.SimpleSearchEngine;
import org.carrot2.source.etools.EToolsDocumentSource;
import org.carrot2.source.google.GoogleDocumentSource;
import org.carrot2.util.attribute.Bindable;

import org.carrot2.shaded.guava.common.collect.Lists;
import org.carrot2.shaded.guava.common.collect.Maps;

/**
 * A {@link IDocumentSource} that folds in a few initial hits from Google API into the
 * eTools results. Ultimately, this type of processing should be done on the eTools side.
 */
@Bindable
public class WebDocumentSource extends SimpleSearchEngine
{
    /**
     * ETools document source, contains bindable attributes.
     */
    public EToolsDocumentSource etools = new EToolsDocumentSource();

    /**
     * Google document source, contains bindable attributes.
     */
    public GoogleDocumentSource google = new GoogleDocumentSource();

    /**
     * Query failure string (must be an identical object to cause a query for tests).
     */
    static final String QUERY_FAILURE = new String("foobar");

    @Override
    public void init(IControllerContext context)
    {
        super.init(context);

        google.init(context);
        etools.init(context);
    }

    @Override
    public void beforeProcessing() throws ProcessingException
    {
        google.beforeProcessing();
        etools.beforeProcessing();
    }

    @Override
    public SearchEngineResponse fetchSearchResponse() throws Exception
    {
        final SearchEngineResponse response = new SearchEngineResponse();

        // Run sub-components sequentially so that we have a chance to weed out spammers
        // before we query Google.
        try {
            // String equality intentional; will work for tests only.
            if (query == QUERY_FAILURE)
            {
                throw new RuntimeException("Synthetic failure.");
            }

            etools.process();
    
            google.results = 8;
            google.process();
        } catch (ProcessingException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause(); 
            } else {
                throw e;
            }
        }

        final Map<String, Document> googleDocumentsByUrl = Maps.newHashMap();
        if (google.documents != null)
        {
            for (Document googleDocument : google.documents)
            {
                googleDocumentsByUrl.put((String) googleDocument
                    .getField(Document.CONTENT_URL), googleDocument);
                googleDocument.setField(Document.SOURCES, Lists
                    .newArrayList("Google"));

                // Set the language based on the eTools source configuration
                googleDocument.setLanguage(etools.language != null ? etools.language
                    .toLanguageCode() : null);
            }
            response.results.addAll(google.documents);
        }

        if (etools.documents != null)
        {
            for (Document etoolsDocument : etools.documents)
            {
                final Document matchingGoogleDocument = googleDocumentsByUrl
                    .get(etoolsDocument.getField(Document.CONTENT_URL));
                if (matchingGoogleDocument != null)
                {
                    final List<String> sources = etoolsDocument
                        .getField(Document.SOURCES);
                    if (!sources.contains("Google"))
                    {
                        sources.add("Google");
                    }
                    matchingGoogleDocument.setField(Document.SOURCES, sources);
                }
                else
                {
                    response.results.add(etoolsDocument);
                }
            }
        }

        // Trim to size (cannot use sublist because the field is final)
        while (response.results.size() > results)
        {
            response.results.remove(results);
        }

        response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY,
            google.resultsTotal);

        return response;
    }

    @Override
    public void afterProcessing()
    {
        etools.afterProcessing();
        google.afterProcessing();

        etools.documents = null;
        google.documents = null;
    }

    @Override
    public void dispose()
    {
        etools.dispose();
        google.dispose();
    }
}
