package com.dawidweiss.carrot.input.yahoo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalInputComponent;
import com.dawidweiss.carrot.core.local.LocalInputComponentBase;
import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.RequestContext;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentBase;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsProducer;
import com.dawidweiss.carrot.util.common.StringUtils;

public class YahooApiInputComponent extends LocalInputComponentBase 
	implements RawDocumentsProducer {

	private final static int MAXIMUM_RESULTS = 400;

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
            YahooSearchResult [] docs = service.query(query, results);
            for (int i = 0; i < docs.length; i++) {
                final int id = i;
                rawDocumentConsumer.addDocument( 
                        new RawDocumentBase(
                                docs[i].url, 
                                StringUtils.removeMarkup(docs[i].title), 
                                StringUtils.removeMarkup(docs[i].summary)) {
                            public Object getId() {
                                return Integer.toString(id);
                            }
                        });
            }
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
