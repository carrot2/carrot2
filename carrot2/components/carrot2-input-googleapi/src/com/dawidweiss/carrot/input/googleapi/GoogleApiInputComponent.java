package com.dawidweiss.carrot.input.googleapi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalInputComponent;
import com.dawidweiss.carrot.core.local.LocalInputComponentBase;
import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.RequestContext;
import com.dawidweiss.carrot.core.local.clustering.RawDocument;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentBase;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsProducer;
import com.google.soap.search.GoogleSearch;
import com.google.soap.search.GoogleSearchResult;
import com.google.soap.search.GoogleSearchResultElement;

public class GoogleApiInputComponent extends LocalInputComponentBase 
	implements RawDocumentsProducer {
	
	private final static int MAXIMUM_RESULTS = 200;

	private static Logger log = Logger.getLogger(GoogleApiInputComponent.class);

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

	private GoogleKeysPool keyPool;

	public GoogleApiInputComponent(GoogleKeysPool keyPool) {
		this.keyPool = keyPool;
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

    	try {
	    	requestContext.getRequestParameters().put(LocalInputComponent.PARAM_QUERY, this.query);
	    	super.startProcessing(requestContext);
	    	
	    	if (this.query == null || "".equals(query)) {
	    		// empty query. just return.
	    		return;
	    	}
	
		    final int resultsRequested = super.getIntFromRequestContext(requestContext,
		            LocalInputComponent.PARAM_REQUESTED_RESULTS, 100);
	
		    final int startAt = super.getIntFromRequestContext(requestContext,
		            LocalInputComponent.PARAM_START_AT, 0);

		    int remaining = Math.min(resultsRequested, MAXIMUM_RESULTS);
		    int at = startAt;

		    log.info("First query using GoogleAPI (" + at + "):" + query);
		    SearchResult result = doSearch(query, at);

		    // Ok, now we know how large the result set is and how many remaining
		    // chunks we need to download.
	        while (true) {
		        pushResults(at, result.results);

	        	remaining -= result.results.length;
	        	at += result.results.length;

	        	// If last received results < max available results, interrupt.
	        	if (result.results.length < result.keyExpectedSize) {
	        		log.warn("Google API key returned less results then expected (expected: "
	        				+ result.keyExpectedSize + ", got: " + result.results.length + ")");
	        		break;
	        	}
	        	if (remaining <= 0) {
	        		break;
	        	}

			    log.info("Consecutive query using GoogleAPI (" + at + "):" + query);
	        	result = doSearch(query, at);
	        }
	    } catch (Exception e) {
	    	if (e instanceof ProcessingException) {
	    		throw (ProcessingException) e;
	    	}
	        throw new ProcessingException("Could not process query.", e);
	    }
    }
    
    private SearchResult doSearch(final String query, final int at) throws Exception {
    	while (true) {
		    GoogleApiKey key = keyPool.borrowKey();
		    GoogleSearchResultElement [] results = null;
		    try {
		        GoogleSearch s = new GoogleSearch();
		        s.setKey(key.getKey());
		        final int expectedResultSize = key.getMaxResults();
	
		        s.setQueryString(query);
		        s.setStartResult(at);
		        s.setMaxResults(expectedResultSize);
		        s.setFilter(true); /* Similar results filtering */
		        s.setSafeSearch(false);
	
		        // s.setLanguageRestricts(); /* Language restricts -- lang_pl */
		        // s.setRestrict(); /* Location restricts -- countryPL */
	
		        GoogleSearchResult r = s.doSearch();
	
		        final int totalEstimated = r.getEstimatedTotalResultsCount();
		        results = r.getResultElements();
		        
		        return new SearchResult(results, totalEstimated, expectedResultSize);
		    } catch (Throwable t) {
		    	// Something wrong with the key probably.
		    	key.setInvalid(true);
		    	if (keyPool.hasActiveKeys() == false) {
		    		// No more active keys in the pool. Just bail out with an exception.
		    		throw new ProcessingException("No more Google API keys available (please donate!)");
		    	}
		    } finally {
		    	keyPool.returnKey(key);
		    }
    	}
	}

	private final void pushResults(final int at, final GoogleSearchResultElement [] results) throws ProcessingException {
    	for (int i = 0; i < results.length; i++) {
    		final Integer id = new Integer(at + i);
    		final GoogleSearchResultElement result = results[i];

            final RawDocument rdoc = new RawDocumentBase(result.getURL(), result.getTitle(), result.getSnippet()) {
                public Object getId() {
                    return id;
                }
            };
            rawDocumentConsumer.addDocument(rdoc);
    	}
    }
    
    public String getName() {
        return "Google API Input";
    }
}
