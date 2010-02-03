
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

package org.carrot2.source.yahoo;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.MultipageSearchEngine;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

/**
 * A {@link IDocumentSource} fetching {@link Document}s (search results) from Yahoo!.
 */
@Bindable(prefix = "YahooDocumentSource")
public final class YahooDocumentSource extends MultipageSearchEngine
{
    /** Logger for this class. */
    final static Logger logger = org.slf4j.LoggerFactory.getLogger(YahooDocumentSource.class);

    /**
     * Maximum concurrent threads from all instances of this component.
     */
    private static final int MAX_CONCURRENT_THREADS = 10;

    /**
     * Keep query word highlighting. Yahoo by default highlights query words in
     * snippets using the bold HTML tag. Set this attribute to <code>true</code> to keep
     * these highlights.
     * 
     * @group Postprocessing
     * @level Advanced
     * @label Keep highlights
     */
    @Input
    @Processing
    @Attribute
    public boolean keepHighlights = false;

    /**
     * The specific search service to be used by this document source. You can use this
     * attribute to choose which Yahoo! service to query, e.g. Yahoo Web Search or Yahoo
     * News.
     * 
     * @label Yahoo Search Service
     * @level Advanced
     * @group Service
     */
    @Init
    @Input
    @Attribute
    @ImplementingClasses(classes =
    {
        YahooWebSearchService.class, YahooNewsSearchService.class
    })
    public YahooSearchService service = new YahooWebSearchService();

    /**
     * Run a single query.
     */
    @Override
    public void process() throws ProcessingException
    {
        super.process(service.metadata, getSharedExecutor(MAX_CONCURRENT_THREADS, getClass()));
    }

    /**
     * Create a single page fetcher for the search range.
     */
    @Override
    protected final Callable<SearchEngineResponse> createFetcher(final SearchRange bucket)
    {
        return new SearchEngineResponseCallable()
        {
            public SearchEngineResponse search() throws Exception
            {
                return service.query(query, bucket.start, bucket.results);
            }
        };
    }

    @Override
    protected void afterFetch(SearchEngineResponse response)
    {
        clean(response, keepHighlights, Document.TITLE, Document.SUMMARY);
    }
}
