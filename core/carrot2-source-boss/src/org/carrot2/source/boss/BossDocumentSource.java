
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

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.MultipageSearchEngine;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.util.attribute.*;
import org.carrot2.util.attribute.constraint.ImplementingClasses;

/**
 * A {@link IDocumentSource} fetching {@link Document}s (search results) from <a
 * href="http://developer.yahoo.com/search/boss/">Yahoo BOSS</a>.
 */
@Bindable(prefix = "BossDocumentSource")
public final class BossDocumentSource extends MultipageSearchEngine
{
    /** Logger for this class. */
    final static Logger logger = org.slf4j.LoggerFactory.getLogger(BossDocumentSource.class);

    /**
     * Maximum concurrent threads from all instances of this component.
     */
    private static final int MAX_CONCURRENT_THREADS = 5;

    /**
     * Determines whether to keep the original query word highlights. Yahoo by default
     * highlights query words in search results using the &lt;b&gt; HTML tag. Set this
     * attribute to <code>true</code> to keep these highlights.
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
     * The specific search service to be used by this document source. Use this attribute
     * to choose which BOSS's service to query, e.g. Web, News or Image search.
     * 
     * @label Boss Search Service
     * @level Advanced
     * @group Service
     */
    @Init
    @Input
    @Attribute
    @ImplementingClasses(classes =
    {
        BossWebSearchService.class, BossNewsSearchService.class,
        BossImageSearchService.class
    })
    public BossSearchService service = new BossWebSearchService();

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

    private static final Pattern WBR_PATTERN = Pattern.compile("<wbr>");
    
    @Override
    protected void afterFetch(SearchEngineResponse response)
    {
        final String [] fields = new String[] { Document.TITLE, Document.SUMMARY };
        clean(response, keepHighlights, fields);

        // Remove all occurrences of <wbr>. It is used for optional breaking of words,
        // so removing (instead of replacing with space) is just right.
        // http://www.quirksmode.org/oddsandends/wbr.html
        for (Document document : response.results)
        {
            for (String field : fields)
            {
                final String originalField = document.getField(field);
                if (StringUtils.isNotBlank(originalField))
                {
                    final Matcher matcher = WBR_PATTERN.matcher(originalField);
                    document.setField(field, matcher.replaceAll(""));
                }
            }
        }
    }
}
