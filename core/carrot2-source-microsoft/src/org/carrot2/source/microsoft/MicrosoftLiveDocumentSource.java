
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

package org.carrot2.source.microsoft;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.carrot2.core.*;
import org.carrot2.core.attribute.*;
import org.carrot2.source.*;
import org.carrot2.util.attribute.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.msnsearch.*;

/**
 * A {@link IDocumentSource} fetching {@link Document}s (search results) from Microsoft
 * Live!.
 */
@Bindable(prefix = "MicrosoftLiveDocumentSource")
public final class MicrosoftLiveDocumentSource extends MultipageSearchEngine
{
    /** Application ID assigned to Carrot Search s.c. */
    public final static String CARROTSEARCH_APPID = "DE531D8A42139F590B253CADFAD7A86172F93B96";

    /** Logger for this class. */
    private final static Logger logger = LoggerFactory.getLogger(MicrosoftLiveDocumentSource.class);

    /**
     * Maximum concurrent threads from all instances of this component.
     */
    private static final int MAX_CONCURRENT_THREADS = 10;

    /**
     * Microsoft-assigned application ID for querying the API. Please <strong>generate
     * your own ID</strong> for production deployments and branches off the Carrot2.org's
     * code.
     * 
     * @label Application ID
     * @level Advanced
     * @group Service
     */
    @Init
    @Input
    @Attribute
    @Required
    public String appid = CARROTSEARCH_APPID;

    /**
     * Culture and language restriction.
     * 
     * @label Culture
     * @group Results filtering
     * @level Medium
     */
    @Input
    @Processing
    @Attribute
    @Required
    public CultureInfo culture = CultureInfo.ENGLISH_UNITED_STATES;

    /**
     * Safe search restriction (porn filter).
     * 
     * @label Safe Search
     * @group Results filtering
     * @level Medium
     */
    @Processing
    @Input
    @Attribute
    @Required
    public SafeSearch safeSearch = SafeSearch.MODERATE;

    /**
     * Microsoft Live! metadata.
     */
    static final MultipageSearchEngineMetadata metadata = new MultipageSearchEngineMetadata(
        50, 1000);

    /**
     * Run a single query.
     */
    @Override
    public void process() throws ProcessingException
    {
        super.process(metadata, getSharedExecutor(MAX_CONCURRENT_THREADS, getClass()));
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
                return MicrosoftLiveDocumentSource.this.search(query, bucket.start,
                    bucket.results);
            }
        };
    }

    /**
     * Run the actual single-page search against MSN.
     */
    private final SearchEngineResponse search(String query, int startAt,
        int totalResultsRequested)
    {
        final SearchRequest request = new SearchRequest(appid, // application id
            query, // query
            culture.cultureInfoCode, // culture info
            safeSearch.getSafeSearchOption(), // safe search options
            new String []
            {
                SearchFlagsNull._None, SearchFlagsNull._DisableLocationDetection,
            }, // search flags
            null, // location
            null // requests
        );

        final String [] searchFields = new String []
        {
            ResultFieldMaskNull._Url, ResultFieldMaskNull._Title,
            ResultFieldMaskNull._Description,
        };

        final int fetchSize = Math.min(totalResultsRequested, metadata.resultsPerPage);
        final SourceRequest sourceRequest = new SourceRequest(SourceType.Web, startAt,
            fetchSize, "", new String []
            {
                SortByTypeNull._Default
            }, // sort by field?
            searchFields, // result fields
            new String [] {}); // search tag filters

        request.setRequests(new SourceRequest []
        {
            sourceRequest
        });

        MSNSearchPortType service;
        try
        {
            service = new MSNSearchServiceLocator().getMSNSearchPort();
        }
        catch (Exception e)
        {
            throw new ProcessingException(
                "Could not initialize Microsoft Live! service.", e);
        }

        final SourceResponse [] responses;
        try
        {
            responses = service.search(new Search(request)).getResponse().getResponses();
        }
        catch (RemoteException e)
        {
            throw new ProcessingException("Microsoft Live! query failed.", e);
        }

        // Check for suspicious responses.
        if (responses.length != 1)
        {
            logger.warn("More than one response for a search: " + responses.length);
        }

        final SourceResponse response = responses[0];
        final Result [] searchResults = response.getResults();

        final SearchEngineResponse searchEngineResponse = new SearchEngineResponse();
        final ArrayList<Document> docs = searchEngineResponse.results;
        for (int j = 0; j < searchResults.length; j++)
        {
            String tmp = searchResults[j].getUrl();

            if (StringUtils.isEmpty(tmp))
            {
                logger.warn("Empty URL in search results.");
                continue;
            }

            final Document document = new Document();
            document.setField(Document.CONTENT_URL, tmp);

            tmp = StringEscapeUtils.unescapeHtml(searchResults[j].getTitle());
            if (!StringUtils.isEmpty(tmp))
            {
                document.setField(Document.TITLE, tmp);
            }

            tmp = StringEscapeUtils.unescapeHtml(searchResults[j].getDescription());
            if (!StringUtils.isEmpty(tmp))
            {
                document.setField(Document.SUMMARY, tmp);
            }

            document.setLanguage(culture.toLanguageCode());
            
            docs.add(document);
        }

        searchEngineResponse.metadata.put(SearchEngineResponse.RESULTS_TOTAL_KEY,
            (long) response.getTotal());

        return searchEngineResponse;
    }
}
