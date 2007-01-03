
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

package org.carrot2.input.yahooapi;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.log4j.Logger;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.util.StringUtils;

public class YahooApiInputComponent extends LocalInputComponentBase 
	implements RawDocumentsProducer {

	private final static int MAXIMUM_RESULTS = 1000;

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
    private YahooSearchService service;

    /**
     * Create an input component with the default service descriptor.
     */
    public YahooApiInputComponent() {
        final YahooSearchServiceDescriptor descriptor = new YahooSearchServiceDescriptor();
        try {
            descriptor.initializeFromXML(this.getClass().getClassLoader().getResourceAsStream("resource/yahoo.xml"));
        } catch (IOException e) {
            throw new RuntimeException("Could not find default Yahoo service descriptor.");
        }
        final YahooSearchService service = new YahooSearchService(descriptor);
        this.service = service;
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

		    final int resultsRequested = super.getIntFromRequestContext(requestContext,
		            LocalInputComponent.PARAM_REQUESTED_RESULTS, 100);

		    int results = Math.min(resultsRequested, MAXIMUM_RESULTS);

		    log.info("Yahoo API query (" + results + "):" + query);
            final YahooSearchResultConsumer consumer = new YahooSearchResultConsumer() {
                int id = 0;
                public void add(final YahooSearchResult result)
                    throws ProcessingException
                {
                    RawDocumentSnippet snippet = new RawDocumentSnippet(Integer
                        .toString(id), StringUtils.removeMarkup(result.title),
                        StringUtils.removeMarkup(result.summary), result.url,
                        id);
                    rawDocumentConsumer.addDocument(snippet);
                    id++;
                }
            };
            service.query(query, results, consumer);
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
}
