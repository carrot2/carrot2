
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

package org.carrot2.source.opensearch;

import java.util.Map;
import java.util.concurrent.Callable;

import org.carrot2.core.Document;
import org.carrot2.core.IDocumentSource;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.attribute.Init;
import org.carrot2.core.attribute.Processing;
import org.carrot2.source.MultipageSearchEngine;
import org.carrot2.source.MultipageSearchEngineMetadata;
import org.carrot2.source.SearchEngineResponse;
import org.carrot2.util.StringUtils;
import org.carrot2.util.attribute.Attribute;
import org.carrot2.util.attribute.AttributeLevel;
import org.carrot2.util.attribute.Bindable;
import org.carrot2.util.attribute.Group;
import org.carrot2.util.attribute.Input;
import org.carrot2.util.attribute.Label;
import org.carrot2.util.attribute.Level;
import org.carrot2.util.attribute.Required;
import org.carrot2.util.attribute.constraint.IntRange;
import org.carrot2.util.resource.URLResourceWithParams;
import org.slf4j.Logger;

import org.carrot2.shaded.guava.common.collect.Maps;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

/**
 * A {@link IDocumentSource} fetching {@link Document}s (search results) from an
 * OpenSearch feed.
 * <p>
 * Based on code donated by Julien Nioche.
 * 
 * @see <a href="http://www.opensearch.org">OpenSearch.org</a>
 */
@Bindable(prefix = "OpenSearchDocumentSource")
public class OpenSearchDocumentSource extends MultipageSearchEngine
{
    /** Logger for this class. */
    final static Logger logger = org.slf4j.LoggerFactory.getLogger(OpenSearchDocumentSource.class);

    /**
     * Maximum concurrent threads from all instances of this component.
     */
    private static final int MAX_CONCURRENT_THREADS = 10;

    /**
     * URL to fetch the search feed from. The URL template can contain variable place
     * holders as defined by the OpenSearch specification that will be replaced during
     * runtime. The format of the place holder is <code>${variable}</code>. The following
     * variables are supported:
     * <ul>
     * <li><code>searchTerms</code> will be replaced by the query</li>
     * <li><code>startIndex</code> index of the first result to be searched. Mutually 
     * exclusive with <code>startPage</code></li>
     * <li><code>startPage</code> index of the first result
     * to be searched. Mutually exclusive with <code>startIndex</code>.</li>
     * <li><code>count</code> the number of search results per page</li>
     * </ul>
     * 
     * <p>Example URL feed templates for public services:</p>
     * <dl>
     *   <dt>nature.com</dt>
     *   <dd><code>http://www.nature.com/opensearch/request?interface=opensearch&amp;operation=searchRetrieve&amp;query=${searchTerms}&amp;startRecord=${startIndex}&amp;maximumRecords=${count}&amp;httpAccept=application/rss%2Bxml</code></dd>
     *   <dt>indeed.com</dt>
     *   <dd><code>http://www.indeed.com/opensearch?q=${searchTerms}&amp;start=${startIndex}&amp;limit=${count}</code></dd>
     * </dl>
     */
    @Input
    @Processing
    @Init
    @Attribute
    @Required
    @Label("Feed URL template")
    @Level(AttributeLevel.BASIC)
    @Group(SERVICE)            
    public String feedUrlTemplate;

    /**
     * Results per page. The number of results per page the document source will expect
     * the feed to return.
     */
    @Input
    @Processing
    @Init
    @Attribute
    @Required
    @IntRange(min = 1)
    @Label("Results per page")
    @Level(AttributeLevel.BASIC)
    @Group(SERVICE)                
    public int resultsPerPage = 50;

    /**
     * Maximum number of results. The maximum number of results the document source can
     * deliver.
     */
    @Input
    @Processing
    @Init
    @Attribute
    @IntRange(min = 1)
    @Label("Maximum results")
    @Level(AttributeLevel.BASIC)
    @Group(SERVICE)            
    public int maximumResults = 1000;

    /**
     * Additional parameters to be appended to {@link #feedUrlTemplate} on each request.
     */
    @Input
    @Init
    @Processing
    @Attribute
    @Label("Feed URL parameters")
    @Level(AttributeLevel.ADVANCED)
    @Group(SERVICE)                
    public Map<String, String> feedUrlParams = null;
    
    /**
     * User agent header. The contents of the User-Agent HTTP header to use when making
     * requests to the feed URL. If empty or <code>null</code> value is provided,
     * the following User-Agent will be sent: <code>Rome Client (http://tinyurl.com/64t5n) 
     * Ver: UNKNOWN</code>.
     */
    @Input
    @Init
    @Processing
    @Attribute
    @Label("User agent")
    @Level(AttributeLevel.ADVANCED)
    @Group(SERVICE)
    public String userAgent = null;

    /**
     * Search engine metadata create upon initialization.
     */
    private MultipageSearchEngineMetadata metadata;

    /** Fetcher for OpenSearch feed. */
    private FeedFetcher feedFetcher;

    /** searchTerms variable */
    private static final String SEARCH_TERMS_VARIABLE_NAME = "searchTerms";

    /** startIndex variable */
    private static final String START_INDEX_VARIABLE_NAME = "startIndex";

    /** startPage variable */
    private static final String START_PAGE_VARIABLE_NAME = "startPage";

    /** count variable */
    private static final String COUNT_VARIABLE_NAME = "count";

    @Override
    public void beforeProcessing()
    {
        // Verify that the attributes are legal
        final boolean hasStartPage = URLResourceWithParams.containsAttributePlaceholder(
            feedUrlTemplate, START_PAGE_VARIABLE_NAME);
        final boolean hasStartIndex = URLResourceWithParams.containsAttributePlaceholder(
            feedUrlTemplate, START_INDEX_VARIABLE_NAME);

        if (!(hasStartPage ^ hasStartIndex))
        {
            throw new ProcessingException(
                "The feedUrlTemplate must contain either "
                    + URLResourceWithParams
                        .formatAttributePlaceholder(START_INDEX_VARIABLE_NAME)
                    + " or "
                    + URLResourceWithParams
                        .formatAttributePlaceholder(START_PAGE_VARIABLE_NAME)
                    + " variable");
        }

        if (!URLResourceWithParams.containsAttributePlaceholder(feedUrlTemplate,
            SEARCH_TERMS_VARIABLE_NAME))
        {
            throw new ProcessingException(
                "The feedUrlTemplate must contain "
                    + URLResourceWithParams
                        .formatAttributePlaceholder(SEARCH_TERMS_VARIABLE_NAME)
                    + " variable");
        }

        if (resultsPerPage == 0)
        {
            throw new ProcessingException("resultsPerPage must be set");
        }

        this.metadata = new MultipageSearchEngineMetadata(resultsPerPage, maximumResults,
            hasStartPage);
        this.feedFetcher = new HttpURLFeedFetcher();
        if (org.apache.commons.lang.StringUtils.isNotBlank(this.userAgent))
        {
            this.feedFetcher.setUserAgent(this.userAgent);
        }
    }

    @Override
    public void process() throws ProcessingException
    {
        super.process(metadata,
            getSharedExecutor(MAX_CONCURRENT_THREADS, this.getClass()));
    }

    @Override
    protected Callable<SearchEngineResponse> createFetcher(final SearchRange bucket)
    {
        return new SearchEngineResponseCallable()
        {
            public SearchEngineResponse search() throws Exception
            {
                // Replace variables in the URL
                final Map<String, Object> values = Maps.newHashMap();
                values.put(SEARCH_TERMS_VARIABLE_NAME, query);
                values.put(START_INDEX_VARIABLE_NAME, bucket.start + 1);
                values.put(START_PAGE_VARIABLE_NAME, bucket.start + 1);
                values.put(COUNT_VARIABLE_NAME, bucket.results);

                final StringBuilder urlExtension = new StringBuilder(
                    URLResourceWithParams.substituteAttributes(feedUrlTemplate, values));
                if (feedUrlParams != null)
                {
                    for (Map.Entry<String, String> entry : feedUrlParams.entrySet())
                    {
                        urlExtension.append('&');
                        urlExtension.append(entry.getKey());
                        urlExtension.append('=');
                        urlExtension.append(StringUtils.urlEncodeWrapException(entry
                            .getValue(), "UTF-8"));
                    }
                }

                final String url = urlExtension.toString();
                logger.debug("Fetching URL: " + url);

                return RomeFetcherUtils.fetchUrl(url, feedFetcher);
            }
        };
    }
}
