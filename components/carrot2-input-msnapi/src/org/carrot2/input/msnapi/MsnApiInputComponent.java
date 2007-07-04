/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.msnapi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.fetcher.*;
import org.carrot2.util.StringUtils;

import com.microsoft.msnsearch.*;

/**
 * An input component for <a href="http://search.msn.com/">MSN Search</a>
 * 
 * @see <a href="http://msdn.microsoft.com/live/msnsearch/default.aspx">Microsoft Developers site</a>
 * @author Dawid Weiss
 */
public final class MsnApiInputComponent extends LocalInputComponentBase implements RawDocumentsProducer
{
    /** Carrot Search application ID. */
    public final static String CARROTSEARCH_APPID = "DE531D8A42139F590B253CADFAD7A86172F93B96";

    /** Maximum number of results (starting offset + length) */
    public final static int MAXIMUM_RESULTS = 1000;

    /** Maximum allowed results per query */
    public final static int MAXIMUM_RESULTS_PERQUERY = 50;

    /** Private logger. */
    private final static Logger log = Logger.getLogger(MsnApiInputComponent.class);

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = toSet(RawDocumentsConsumer.class);

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = toSet(RawDocumentsProducer.class);

    /** Current "query". See the docs for query formats. */
    private String query;

    /** Current {@link RawDocumentsConsumer} to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /**
     * Application id for querying MSN Search.
     */
    private final String appid;
    
    /** Culture setting (language) */
    private final String culture;
    public static final String DEFAULT_CULTURE = "en-US";

    /**
     * Create an input component with the default service descriptor, 
     * a custom application identifier and culture.
     */
    public MsnApiInputComponent(String appid, String culture)
    {
        this.appid = appid;
        this.culture = culture;
    }
    
    /**
     * Create an input component with the default service descriptor, a 
     * custom application identifier and default culture.
     */
    public MsnApiInputComponent(String appid)
    {
        this(appid, DEFAULT_CULTURE);
    }

    /**
     * Creates an input component with the default service descriptor, Carrot 
     * Search's application identifier and default culture.
     */
    public MsnApiInputComponent()
    {
        this(CARROTSEARCH_APPID);
    }

    /**
     * 
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /**
     * 
     */
    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }

    /**
     * 
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }

    /**
     * 
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        rawDocumentConsumer = (RawDocumentsConsumer) next;
    }

    /**
     * 
     */
    public void startProcessing(RequestContext requestContext) throws ProcessingException
    {
        requestContext.getRequestParameters().put(LocalInputComponent.PARAM_QUERY, this.query);
        super.startProcessing(requestContext);

        if (this.query == null || "".equals(query))
        {
            // empty query. just return.
            return;
        }

        // Number of requested results.
        final int resultsRequested = super.getIntFromRequestContext(requestContext,
            LocalInputComponent.PARAM_REQUESTED_RESULTS, 100);

        final int startAt = super.getIntFromRequestContext(requestContext, LocalInputComponent.PARAM_START_AT, 0);

        // Prepare fetchers.
        final ParallelFetcher pfetcher = new ParallelFetcher("MSN API", query, startAt, resultsRequested,
            MAXIMUM_RESULTS)
        {
            /**
             *
             */
            public SingleFetcher getFetcher()
            {
                return new SingleFetcher()
                {
                    public SearchResult fetch(String query, int startAt, int totalResultsRequested) throws ProcessingException
                    {
                        return doSearch(query, startAt, totalResultsRequested);
                    }
                };
            }

            /**
             *
             */
            public void pushResults(int at, final RawDocument rawDocument) throws ProcessingException
            {
                rawDocumentConsumer.addDocument(rawDocument);
            }
        };

        // Enable full parallel mode.
        pfetcher.setFullParallelMode(MAXIMUM_RESULTS_PERQUERY);

        // Run fetchers and push results.
        pfetcher.fetch();
    }

    public String getName()
    {
        return "MSN API Input";
    }

    /**
     * 
     */
    final SearchResult doSearch(String query, int startAt, int totalResultsRequested) throws ProcessingException
    {
        final SearchRequest request = new SearchRequest(appid, // application id
            query, // query
            culture, // culture info
            SafeSearchOptions.Off, // safe search options
            new String []
            {
                SearchFlagsNull._None
            }, // search flags
            null, // location
            null // requests
        );

        final String [] searchFields = new String []
        {
            ResultFieldMaskNull._Url, ResultFieldMaskNull._Title, ResultFieldMaskNull._Description,
        };

        final int fetchSize = Math.min(totalResultsRequested, MAXIMUM_RESULTS_PERQUERY);
        final SourceRequest sourceRequest = new SourceRequest(SourceType.Web, startAt, fetchSize, "", new String []
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
        catch (ServiceException e)
        {
            throw new ProcessingException("Could not initialize MSN service.", e);
        }

        final SourceResponse [] responses;
        try
        {
            responses = service.search(request).getResponses();
        }
        catch (RemoteException e)
        {
            throw new ProcessingException("MSN API query failed.", e);
        }

        if (responses.length != 1)
        {
            log.warn("More than one response for a search: " + responses.length);
        }

        final SourceResponse response = responses[0];

        // feed documents.
        final Result [] searchResults = response.getResults();
        if (searchResults.length / (double) fetchSize < 0.5)
        {
            log.warn("Requested results: " + fetchSize + ", but received: " + searchResults.length);
        }

        final ArrayList docs = new ArrayList();
        int id = startAt;
        for (int j = 0; j < searchResults.length; j++)
        {
            if (searchResults[j].getUrl() == null)
            {
                log.warn("Empty URL in a search result.");
                continue;
            }

            final String docId = Integer.toString(id);
            final RawDocument rd = new RawDocumentBase(searchResults[j].getUrl(), StringUtils
                .removeMarkup(searchResults[j].getTitle()), StringUtils.removeMarkup(searchResults[j].getDescription()))
            {
                public Object getId()
                {
                    return docId;
                }
            };
            docs.add(rd);
            id++;
        }

        return new SearchResult((RawDocument []) docs.toArray(new RawDocument [docs.size()]), startAt, response
            .getTotal());
    }
}
