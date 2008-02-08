
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

package org.carrot2.input.yahooapi;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;
import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.fetcher.*;
import org.carrot2.util.resources.*;

public class YahooApiInputComponent extends LocalInputComponentBase 
	implements RawDocumentsProducer {

	private final static int MAXIMUM_RESULTS = 1000;
	private final static String [] SOURCES = new String [] { "Yahoo!" };

	private static Logger log = Logger.getLogger(YahooApiInputComponent.class);

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(
    		Arrays.asList(new Object [] { RawDocumentsConsumer.class }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(
    		Arrays.asList(new Object [] { RawDocumentsProducer.class }));

    /** Current "query". See the docs for query formats. */
    private String query;

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /**
     * Yahoo search service wrapper.
     */
    protected final YahooSearchService service;

    /**
     * Create an input component reading service descriptor from the specified resource. 
     */
    protected YahooApiInputComponent(String descriptorResource) {
        final YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        try {
            final Resource resource = ResourceUtilsFactory.getDefaultResourceUtils().getFirst(
                descriptorResource);
            if (resource == null) {
                throw new RuntimeException("Could not find default Yahoo service descriptor: "
                    + descriptorResource);
            }
            // Prefetch so that we don't have to worry about closing the stream.
            descriptor.initializeFromXML(ResourceUtils.prefetch(resource.open()));
        } catch (IOException e) {
            throw new RuntimeException("Could not load service descriptor: "
                + descriptorResource, e);
        }
        final YahooSearchService service = new YahooSearchService(descriptor);
        this.service = service;
    }

    /**
     * Create an input component with the default service descriptor.
     */
    public YahooApiInputComponent() {
        this("resource/yahoo.xml");
    }

    /**
     * Create a service descriptor from the input XML and use it. The input
     * stream is always closed.
     */
    public YahooApiInputComponent(final InputStream serviceDescriptorXML) throws IOException {
        try {
            final YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
            descriptor.initializeFromXML(serviceDescriptorXML);
            this.service = new YahooSearchService(descriptor);
        } finally {
            try { serviceDescriptorXML.close(); } catch (IOException e) {/* ignore */}
        }
    }

	public YahooApiInputComponent(YahooSearchService service) {
        this.service = service;
	}

	public void setQuery(String query) {
		this.query = query;
	}

    public Set getComponentCapabilities() {
        return COMPONENT_CAPABILITIES;
    }

    public Set getRequiredSuccessorCapabilities() {
        return SUCCESSOR_CAPABILITIES;
    }
    
    public void setNext(LocalComponent next) {
        super.setNext(next);
        rawDocumentConsumer = (RawDocumentsConsumer) next;
    }
    
    public void startProcessing(RequestContext requestContext) 
    	throws ProcessingException {

        if (service == null) {
            throw new ProcessingException("Yahoo API service not set.");
        }
        
    	try {
	    	requestContext.getRequestParameters().put(
                    LocalInputComponent.PARAM_QUERY, this.query);
	    	super.startProcessing(requestContext);

	    	if (this.query == null || "".equals(query)) {
	    		// empty query. just return.
	    		return;
	    	}

		    final int resultsRequested = Math.min(super.getIntFromRequestContext(requestContext,
		            LocalInputComponent.PARAM_REQUESTED_RESULTS, 100), MAXIMUM_RESULTS);

            final int startAt = super.getIntFromRequestContext(requestContext, LocalInputComponent.PARAM_START_AT, 0);

            // Prepare fetchers.
            final ParallelFetcher pfetcher = new ParallelFetcher("yahoo", query, startAt, 
                resultsRequested, MAXIMUM_RESULTS, this.service.getMaxResultsPerQuery())
            {
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

                public void pushResults(int at, final RawDocument rawDocument) throws ProcessingException
                {
                    rawDocumentConsumer.addDocument(rawDocument);
                }
            };

            // Enable full parallel mode.
            pfetcher.setParallelMode(true);

            // Run fetchers and push results.
            pfetcher.fetch();
	    } catch (Throwable e) {
	    	if (e instanceof ProcessingException) {
	    		throw (ProcessingException) e;
	    	}
	        throw new ProcessingException("Could not process query.", e);
	    }
    }

    public String getName() {
        return "Yahoo API Input";
    }

    final SearchResult doSearch(String query, int startAt, int totalResultsRequested) throws ProcessingException
    {
        final int maxResultsPerQuery = Math.min(totalResultsRequested, 
            service.getMaxResultsPerQuery());

        log.info("Yahoo API query (" + maxResultsPerQuery + "): " + query);
        final long [] estimatedResultsArray = new long[1];
        final List results = new ArrayList();
        try {
            service.query(query, maxResultsPerQuery,
                    new YahooSearchResultConsumer() {
                        public void add(YahooSearchResult result)
                            throws ProcessingException
                        {
                            results.add(result);
                        }

                        public void estimatedResultsReceived(
                                long estimatedResults)
                        {
                            estimatedResultsArray[0] = estimatedResults;
                        }
                    }, startAt);

            final RawDocument [] rawDocuments = new RawDocument[results.size()];
            for (int i = 0; i < rawDocuments.length; i++)
            {
                final YahooSearchResult yahooSearchResult = (YahooSearchResult) results.get(i);
                rawDocuments[i] = new RawDocumentSnippet(Integer.toString(i + startAt),
                        yahooSearchResult.title, yahooSearchResult.summary,
                        yahooSearchResult.url, 0.0f);
                rawDocuments[i].setProperty(RawDocument.PROPERTY_SOURCES, SOURCES);

                if (yahooSearchResult.newsSource != null) {
                    rawDocuments[i].setProperty(RawDocument.PROPERTY_SOURCES, new String [] {yahooSearchResult.newsSource});
                }
            }
            
            // Convert to SearchResult
            return new SearchResult(rawDocuments, startAt, estimatedResultsArray[0]);
        }
        catch (IOException e) {
            throw new ProcessingException(e);
        }
    }
}
