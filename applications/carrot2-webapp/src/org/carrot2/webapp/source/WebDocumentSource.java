package org.carrot2.webapp.source;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import org.carrot2.core.*;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.source.SimpleSearchEngine;
import org.carrot2.source.etools.EToolsDocumentSource;
import org.carrot2.source.google.GoogleDocumentSource;
import org.carrot2.util.ExecutorServiceUtils;
import org.carrot2.util.attribute.Bindable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A {@link DocumentSource} that folds in a few initial hits from Google API into the
 * eTools results. Ultimately, this type of processing should be done in the eTools side.
 */
@Bindable
public class WebDocumentSource extends SimpleSearchEngine
{
    /**
     * ETools document source, contains bindable attributes.
     */
    private EToolsDocumentSource etools = new EToolsDocumentSource();

    /**
     * Google document source, contains bindable attributes.
     */
    private GoogleDocumentSource google = new GoogleDocumentSource();

    /**
     * Maximum concurrent threads from all instances of this component.
     */
    private static final int MAX_CONCURRENT_THREADS = 10;

    /**
     * Static executor for running search threads.
     */
    private final static ExecutorService executor = ExecutorServiceUtils
        .createExecutorService(MAX_CONCURRENT_THREADS, WebDocumentSource.class);

    @Override
    public void init()
    {
        google.init();
        etools.init();
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
        final List<Callable<Object>> tasks = Lists.newArrayList();
        tasks.add(new Callable<Object>()
        {
            public Object call() throws Exception
            {
                google.results = 8;
                google.process();
                return null;
            }
        });
        tasks.add(new Callable<Object>()
        {
            public Object call() throws Exception
            {
                etools.process();
                return null;
            }
        });

        final SearchEngineResponse response = new SearchEngineResponse();

        try
        {
            executor.invokeAll(tasks);

            final Map<String, Document> googleDocumentsByUrl = Maps.newHashMap();
            for (Document googleDocument : google.documents)
            {
                googleDocumentsByUrl.put((String) googleDocument
                    .getField(Document.CONTENT_URL), googleDocument);
                googleDocument.addField(Document.SOURCES, Lists.newArrayList(new String(
                    "Google")));
            }
            response.results.addAll(google.documents);

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
                        sources.add(new String("Google"));
                    }
                    matchingGoogleDocument.addField(Document.SOURCES, sources);
                }
                else
                {
                    response.results.add(etoolsDocument);
                }
            }

            // Trim to size (cannot use sublist because the field is final)
            while (response.results.size() > results)
            {
                response.results.remove(results);
            }
            
            response.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY,
                google.resultsTotal);
        }
        catch (InterruptedException e)
        {
            // If interrupted, return the empty response we have
            return response;
        }

        return response;
    }

    @Override
    public void afterProcessing()
    {
        etools.afterProcessing();
        google.afterProcessing();
    }

    @Override
    public void dispose()
    {
        etools.dispose();
        google.dispose();
    }
}
