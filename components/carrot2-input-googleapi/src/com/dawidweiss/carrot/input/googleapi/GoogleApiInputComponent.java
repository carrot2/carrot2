
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

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
import com.dawidweiss.carrot.util.common.StringUtils;
import com.google.soap.search.GoogleSearch;
import com.google.soap.search.GoogleSearchFault;
import com.google.soap.search.GoogleSearchResult;
import com.google.soap.search.GoogleSearchResultElement;

public class GoogleApiInputComponent extends LocalInputComponentBase 
	implements RawDocumentsProducer {
	
	private final static int MAXIMUM_RESULTS = 200;
	private final static int EXPECTED_RESULTS_PER_KEY = 10;

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

		    if (result == null || result.totalEstimated == 0) {
		    	// Nothing more.
		    	return;
		    }
		    pushResults(at, result.results);

		    remaining = Math.min(result.totalEstimated, remaining);

		    // Ok, now we know how large the result set is and how many remaining
		    // chunks we need to download.
		    remaining -= result.results.length;
		    if (remaining <= 0) {
		    	// Ok, nothing more to do.
		    	return;
		    }
		    
		    final int buckets = remaining / EXPECTED_RESULTS_PER_KEY 
		    	+ (remaining % EXPECTED_RESULTS_PER_KEY == 0 ? 0 : 1);
		    final SearchResult [] results = new SearchResult[buckets];

		    class Counter {
		    	int releaseCount;

		    	public Counter(int releaseCount) {
		    		this.releaseCount = releaseCount;
		    	}
		    	
		    	public void done() {
		    		synchronized (this) {
		    			if (releaseCount > 0) {
			    			releaseCount--;
		    			}
		    			if (releaseCount == 0) {
		    				this.notifyAll();
		    			}
		    		}
		    	}

		    	public void blockUntilZero() throws InterruptedException {
		    		synchronized (this) {
		    			if (releaseCount > 0) {
	    					this.wait();
	    					// Assert counter is null.
	    					if (releaseCount != 0) {
	    						throw new RuntimeException("Counter is not zero.");
	    					}
		    			}
		    		}
		    	}
		    }

        	class Loader extends Thread {
        		final int index;
        		final int at;
        		final Counter counter;

        		public Loader(final int index, final int at, final Counter wakeup) {
        			this.index = index;
        			this.at = at;
        			this.counter = wakeup;
        		}

        		public void run() {
        			int acquired = -1;
        			try {
	        			SearchResult result;
	        			try {
	        				log.info("Consecutive GoogleAPI query start (" + at + "):" + query);
	        				result = doSearch(query, at);
	        				acquired = result.results.length;
	        			} catch (Throwable t) {
	        				result = new SearchResult(t);
	        			}
	        			results[index] = result;
        			} finally {
        				log.info("Consecutive GoogleAPI query finished (" + at + ":"
        						+ acquired
        						+ "):" + query);
        				counter.done();
        			}
        		}
        	};

		    Counter c = new Counter(buckets);
        	for (int i = 0; i < buckets; i++) {
	        	remaining -= EXPECTED_RESULTS_PER_KEY;
	        	at += EXPECTED_RESULTS_PER_KEY;

	        	new Loader(i, at, c).start();
        	}
        	if (remaining > 0) {
        		throw new RuntimeException("Assertion failed: remaining should be > 0: " + remaining);
        	}
        	c.blockUntilZero();

        	// Check if there were any processing exceptions.
        	for (int i = 0; i < buckets; i++) {
        		final SearchResult sr = results[i];
        		if (sr == null) {
        			throw new ProcessingException("One of GoogleAPI threads did not leave any result.");
        		}
        		if (sr.error != null) {
        			// Rethrow exception from background thread.
        			throw sr.error;
        		}
        		// Otherwise push the result
        		pushResults(sr.at, sr.results);
        	}
	    } catch (Throwable e) {
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

		        s.setQueryString(query);
		        s.setStartResult(at);
		        s.setMaxResults(EXPECTED_RESULTS_PER_KEY);
		        s.setFilter(false); /* Similar results filtering */
		        s.setSafeSearch(false);

		        // s.setLanguageRestricts(); /* Language restricts -- lang_pl */
		        // s.setRestrict(); /* Location restricts -- countryPL */
	
		        GoogleSearchResult r = s.doSearch();

		        if (r.getStartIndex() != at + 1) {
		        	return null;
		        }
	
		        final int totalEstimated = r.getEstimatedTotalResultsCount();
		        results = r.getResultElements();

		        return new SearchResult(results, at, totalEstimated);
		    } catch (Throwable t) {
		    	if (t instanceof GoogleSearchFault) {
		    		final String msg = ((GoogleSearchFault) t).getMessage();
		    		if (msg.indexOf("exceeded") >= 0) {
		    			// Limit exceeded.
		    			key.setInvalid(true);
		    			log.info("Key limit exceeded: " + key.getName());
		    		} else if (msg.indexOf("Unsupported response content type") >= 0) {
		    			// This indicates temporary Google failure.
		    			log.info("Temporary GoogleAPI failure on key: " + key.getName());
		    			continue;
		    		} else {
		    			log.warn("Unhandled GoogleAPI exception on key: " + key.getName(), t);
		    			key.setInvalid(true);
		    		}
		    	} else {
		    		log.warn("Unhandled doSearch exception on key: " + key.getName(), t);
		    	}
		    } finally {
		    	keyPool.returnKey(key);
		    }

	    	if (keyPool.hasActiveKeys() == false) {
	    		// No more active keys in the pool. Just bail out with an exception.
	    		throw new ProcessingException("No more Google API keys available (please donate!)");
	    	}
    	}
	}
    
	private final void pushResults(final int at, final GoogleSearchResultElement [] results) throws ProcessingException {
    	for (int i = 0; i < results.length; i++) {
    		final Integer id = new Integer(at + i);
    		final GoogleSearchResultElement result = results[i];

            final RawDocument rdoc = new RawDocumentBase(
                    result.getURL(), 
                    StringUtils.removeMarkup(result.getTitle()), 
                    StringUtils.removeMarkup(result.getSnippet())) {
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
