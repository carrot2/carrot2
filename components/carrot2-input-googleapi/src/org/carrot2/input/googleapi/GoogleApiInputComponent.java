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

package org.carrot2.input.googleapi;

import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.fetcher.*;
import org.carrot2.util.StringUtils;

import com.google.soap.search.*;

/**
 * <p>GoogleAPI input component.
 * 
 * <p>Note that GoogleAPI is officially deprecated as of December 2006. and
 * <b>will be removed from future versions of Carrot2</b>.
 * 
 * @author Dawid Weiss
 */
public final class GoogleApiInputComponent 
    extends LocalInputComponentBase implements RawDocumentsProducer
{
    /**
     * Private logger.
     */
    private static Logger log = Logger.getLogger(GoogleApiInputComponent.class);

    /**
     * Maximum results this component can fetch.
     */
    private final static int MAXIMUM_RESULTS = 200;

    /**
     * Expected results per single query.
     */
    private final static int EXPECTED_RESULTS_PER_KEY = 10;

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = toSet(RawDocumentsConsumer.class);

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = toSet(RawDocumentsProducer.class);

    /** Sources array */
    private final static String [] SOURCES = new String [] { "Google" };
    
    /** Current "query". See the docs for query formats. */
    private String query;

    /** Current {@link RawDocumentsConsumer} to feed documents to. */
    private RawDocumentsConsumer rawDocumentConsumer;

    private GoogleKeysPool keyPool;

    /** A map of already returned documents. */
    private HashMap returnedIds = new HashMap();

    /**
     * Creates a default {@link GoogleKeysPool}, using system property {@link GoogleKeysPool#POOL_SYSPROPERTY} to
     * locate a folder with keys. Throws a runtime exception if not found.
     */
    public GoogleApiInputComponent()
    {
        this(GoogleKeysPool.getDefault());
    }

    /**
     * Creates a component with the given pool of keys.
     */
    public GoogleApiInputComponent(GoogleKeysPool keyPool)
    {
        this.keyPool = keyPool;
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
     * Process the query.
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
        final ParallelFetcher pfetcher = new ParallelFetcher("google", query, startAt, resultsRequested,
            MAXIMUM_RESULTS, EXPECTED_RESULTS_PER_KEY)
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

        // Disable parallel mode (save keys).
        pfetcher.setParallelMode(false);

        // Run fetchers and push results.
        pfetcher.fetch();
    }

    /**
     * 
     */
    public String getName()
    {
        return "Google API Input";
    }

    /**
     * 
     */
    public void flushResources()
    {
        super.flushResources();

        returnedIds.clear();
    }
    
    /**
     * Performs a single search to Google. This method is used
     * from {@link SingleFetcher#fetch(String, int, int)}.
     */
    final SearchResult doSearch(final String query, final int at, int totalResultsRequested) throws ProcessingException
    {
        while (true)
        {
            final GoogleApiKey key;
            try
            {
                key = keyPool.borrowKey();
            }
            catch (Exception e)
            {
                throw new ProcessingException("Could not acquire Google API key.", e);
            }

            try
            {
                final GoogleSearch s = new GoogleSearch();
                s.setKey(key.getKey());

                s.setQueryString(query);
                s.setStartResult(at);
                s.setMaxResults(EXPECTED_RESULTS_PER_KEY);
                s.setFilter(false); /* Similar results filtering */
                s.setSafeSearch(false);

                // s.setLanguageRestricts(); /* Language restricts -- lang_pl */
                // s.setRestrict(); /* Location restricts -- countryPL */

                final GoogleSearchResult r = s.doSearch();

                log.debug("Google returned:" + " startIndex=" + r.getStartIndex() + " endIndex=" + r.getEndIndex()
                    + " estimated=" + r.getEstimatedTotalResultsCount() + " exact=" + r.getEstimateIsExact());

                if (r.getStartIndex() != at + 1 && r.getEndIndex() > 0)
                {
                    log.warn("Start index does not match the requested one: " + r.getStartIndex() + ", should be: "
                        + (at + 1));
                }

                final int totalEstimated = r.getEstimatedTotalResultsCount();

                // Convert to raw documents.
                final GoogleSearchResultElement [] results = r.getResultElements();
                final RawDocument [] rawDocuments = new RawDocument [results.length];

                for (int i = 0; i < results.length; i++)
                {
                    final GoogleSearchResultElement gsr = results[i];
                    final Integer id = new Integer(at + i);

                    final RawDocument rdoc = new RawDocumentBase(gsr.getURL(), StringUtils.unescapeHtml(StringUtils
                        .removeMarkup(gsr.getTitle())), StringUtils.unescapeHtml(StringUtils.removeMarkup(gsr
                        .getSnippet())))
                    {
                        public Object getId()
                        {
                            return id;
                        }
                    };
                    rdoc.setProperty(RawDocument.PROPERTY_SOURCES, SOURCES);
                    rawDocuments[i] = rdoc;
                }

                return new SearchResult(rawDocuments, r.getStartIndex(), totalEstimated);
            }
            catch (Throwable t)
            {
                if (t instanceof GoogleSearchFault)
                {
                    final String msg = ((GoogleSearchFault) t).getMessage();
                    if (msg.indexOf("exceeded") >= 0)
                    {
                        // Limit exceeded.
                        log.warn("Key limit exceeded: " + key.getName());
                        key.setInvalid(true, GoogleApiKey.WAIT_TIME_LIMIT_EXCEEDED);
                    }
                    else if (msg.indexOf("Unsupported response content type") >= 0)
                    {
                        // This indicates temporary Google failure.
                        log.warn("Temporary GoogleAPI failure on key: " + key.getName());
                        continue;
                    }
                    else
                    {
                        log.warn("Unhandled GoogleAPI exception on key: " + key.getName(), t);
                        key.setInvalid(true, GoogleApiKey.WAIT_TIME_UNKNOWN_PROBLEM);
                    }
                }
                else
                {
                    log.warn("Unhandled doSearch exception on key: " + key.getName(), t);
                }
            }
            finally
            {
                keyPool.returnKey(key);
            }

            if (keyPool.hasActiveKeys() == false)
            {
                // No more active keys in the pool. Just bail out with an exception.
                throw new ProcessingException("No more Google API keys available (please donate!)");
            }
        }
    }
}
