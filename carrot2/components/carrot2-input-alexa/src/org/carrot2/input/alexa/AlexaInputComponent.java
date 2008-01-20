
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.alexa;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.rpc.ServiceException;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.fetcher.ParallelFetcher;
import org.carrot2.core.fetcher.SingleFetcher;
import org.carrot2.util.StringUtils;

import com.alexasearch.*;

/**
 * An input component for <a href="http://websearch.amazonaws.com/">Alexa Search</a>.
 *
 * This component was donated to the Carrot2 project by deepVertical.
 *
 * @author Dawid Weiss
 */
public final class AlexaInputComponent extends LocalInputComponentBase implements RawDocumentsProducer
{
    /** An algorithm used for signing Alexa requests. */
    private final static String HMAC_SHA1_ALGORITHM = "HmacSHA1";
    private final static String [] SOURCES = new String [] { "Alexa" };

    /**
     * If any value is set in the request context under this key,
     * full parallel mode is disabled.
     *
     * @see ParallelFetcher#setParallelMode(boolean)
     */
    public final static String PROPERTY_DISABLE_PARALLEL_MODE = "input.alexa.disableParallelMode";

    /** Maximum allowed results per query. */
    public final static int MAXIMUM_RESULTS_PERQUERY = 100;

    /** Maximum number of results (starting offset + length). */
    public final static int MAXIMUM_RESULTS = 200;

    /** Default number of requested results. */
    private final static int DEFAULT_REQUESTED_RESULTS = 80;

    /** Private logger. */
    private final static Logger log = Logger.getLogger(AlexaInputComponent.class);

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = toSet(RawDocumentsConsumer.class);

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = toSet(RawDocumentsProducer.class);

    /**
     * {@link SimpleDateFormat} for request signing.
     */
    private static final String DATEFORMAT_AWS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * A compiled {@link SimpleDateFormat} for use in this component.
     *
     * GuardedBy(this)
     */
    private final SimpleDateFormat awsDateFormat = new SimpleDateFormat(DATEFORMAT_AWS);

    /**
     * A calendar for calculating request timestamp in {@link #getTimestamp()}.
     *
     * GuardedBy(this)
     */
    private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.ENGLISH);

    /**
     * Last timestamp (request timestamps must be unique - Alexa has problems
     * with concurrent requests with the same timestamp).
     *
     * GuardedBy(this)
     */
    private String lastTimestamp;

    /** Current "query". See the docs for query formats. */
    private String query;

    /** Current {@link RawDocumentsConsumer} to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /**
     * Access key.
     */
    private final String accessKey;

    /**
     * The secret key used for signing requests.
     */
    private SecretKeySpec signingKey;

    /**
     * Enables or disables full parallel mode for all queries
     * made on this component instance. <code>true</code> by default.
     *
     * @see ParallelFetcher#setFullParallelMode(int)
     * @see #setFullParallelMode(boolean)
     */
    private boolean parallelMode = true;

    /**
     * Create an input component with the default service descriptor and a custom application identifier.
     */
    public AlexaInputComponent(String accessKey, String secretKey)
    {
        if (accessKey == null || secretKey == null)
        {
            throw new IllegalArgumentException("Access key and secret key are required.");
        }

        this.accessKey = accessKey;
        this.signingKey = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA1_ALGORITHM);
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
            LocalInputComponent.PARAM_REQUESTED_RESULTS, DEFAULT_REQUESTED_RESULTS);

        final int startAt = super.getIntFromRequestContext(requestContext, LocalInputComponent.PARAM_START_AT, 0);

        // Prepare fetchers.
        final ParallelFetcher pfetcher = new ParallelFetcher("alexa", query, startAt, 
            resultsRequested, MAXIMUM_RESULTS, MAXIMUM_RESULTS_PERQUERY)
        {
            /**
             *
             */
            public SingleFetcher getFetcher()
            {
                return new SingleFetcher()
                {
                    public org.carrot2.core.fetcher.SearchResult fetch(String query, int startAt, int totalResultsRequested) throws ProcessingException
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

        final Map requestContextParams = requestContext.getRequestParameters();
        if (parallelMode && !requestContextParams.containsKey(PROPERTY_DISABLE_PARALLEL_MODE)) {
            pfetcher.setParallelMode(true);
        }

        // Run fetchers and push results.
        pfetcher.fetch();
    }

    /**
     *
     */
    public String getName()
    {
        return "Alexa API Input";
    }

    /**
     * Enables or disables full parallel mode of search results fetching.
     */
    public void setParallelMode(boolean value) {
        this.parallelMode = value;
    }

    /**
     * Generate and return a timestamp for use with AWS request signing.
     */
    private String getTimestamp()
    {
        synchronized (this)
        {
            // Do not allow identical timestamps to be sent to Alexa
            // as they result in identical hashes and cause
            // weird exceptions occassionaly.
            String timestamp;
            do
            {
                calendar.setTimeInMillis(System.currentTimeMillis());
                timestamp = awsDateFormat.format(calendar.getTime());
            }
            while (timestamp.equals(lastTimestamp));

            lastTimestamp = timestamp;
            return timestamp;
        }
    }

    /**
     * Performs a single search in the Alexa Search API.
     */
    private org.carrot2.core.fetcher.SearchResult doSearch(String query, int startAt, int totalResultsRequested) throws ProcessingException
    {
        // Modify the query to include only sensible page types and English language results.
        // This is supposedly what they use at Alexa's web site, but the counts don't match --
        // with -pagetype:irrelevant added, the counts drop an order of magnitude.
        final String modifiedQuery = query + " lang:(en|unknown) -pagetype:irrelevant";

        // Perform the query.
        final int fetchSize = Math.min(totalResultsRequested, MAXIMUM_RESULTS_PERQUERY);
        final String ACTION_NAME = "Search";
        final String timestamp = getTimestamp();

        final Search searchRequest = new Search();
        searchRequest.setResponseGroup(new SearchResponseGroup [] {SearchResponseGroup.Context});

        final int thisPageNumber = 1 + (startAt / fetchSize);

        searchRequest.setMaxNumberOfDocumentsPerPage(new BigInteger(Long.toString(fetchSize)));

        searchRequest.setMaxTime(new BigInteger("10"));

        searchRequest.setAWSAccessKeyId(this.accessKey);
        searchRequest.setSignature(getSignature(ACTION_NAME + timestamp));
        searchRequest.setTimestamp(timestamp);
        searchRequest.setQuery(modifiedQuery);
        searchRequest.setPageNumber(new BigInteger(Integer.toString(thisPageNumber)));

        final AlexaWebSearchPortType service;
        try
        {
            service = new AlexaWebSearchLocator().getAlexaWebSearchHttpPort();
        }
        catch (ServiceException e)
        {
            throw new ProcessingException("Could not initialize Alexa service.", e);
        }

        // Execute the query.
        final SearchResponse response;
        try
        {
            log.debug("Searching Alexa; start=" + startAt + ", size=" + fetchSize
                + ", page=" + thisPageNumber + ", ts=" + timestamp);
            response = service.search(searchRequest);

            if (response == null)
            {
                throw new RemoteException("Null returned from the Alexa Web Search.");
            }
        }
        catch (RemoteException e)
        {
            throw new ProcessingException("Alexa API query failed.", e);
        }

        final SearchResult webResults = response.getSearchResult();

        // Adjust total approximation.
        final long total = Long.parseLong(webResults.getEstimatedNumberOfDocuments());

        // Fetch remaining documents.
        Document [] documents = webResults.getDocument();
        if (documents == null)
        {
            documents = new Document [0];
        }
        if (documents.length / (double) fetchSize < 0.5)
        {
            log.warn("Requested results: " + fetchSize + ", but received: " + documents.length);
        }

        int id = startAt;
        final ArrayList docs = new ArrayList();
        for (int j = 0; j < documents.length; j++)
        {
            String url = documents[j].getUrl().toString();
            String snippet = documents[j].getContext();
            String title = documents[j].getTitle();

            if (url == null)
            {
                log.warn("Empty URL in search result.");
                continue;
            }

            final String docId = Integer.toString(id);
            final RawDocumentBase rawDocument = new RawDocumentBase(url, StringUtils.removeMarkup(title),
                StringUtils.removeMarkup(snippet))
            {
                public Object getId()
                {
                    return docId;
                }
            };
            rawDocument.setProperty(RawDocument.PROPERTY_SOURCES, SOURCES);
            docs.add(rawDocument);

            id++;
        }

        return new org.carrot2.core.fetcher.SearchResult(
            (RawDocument []) docs.toArray(new RawDocument [docs.size()]), startAt, total);
    }

    /**
     * Computes RFC 2104-compliant HMAC signature.
     *
     * @param data The data to be signed.
     * @param key The signing key.
     * @return The base64-encoded RFC 2104-compliant HMAC signature.
     */
    private String getSignature(final String data)
    {
        try
        {
            final Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);

            // compute the hmac on input data bytes
            final byte [] rawHmac = mac.doFinal(data.getBytes());

            return new String(new Base64().encode(rawHmac), "iso8859-1");
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to generate Alexa signature.", e);
        }
    }
}
