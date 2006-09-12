
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

package org.carrot2.input.msnapi;

import java.util.Set;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.util.StringUtils;

import com.microsoft.msnsearch.*;

/**
 * An input component for <a href="http://search.msn.com/">MSN Search</a>
 * 
 * @see <a href="http://msdn.microsoft.com/live/msnsearch/default.aspx">Microsoft Developers site</a>
 * @author Dawid Weiss
 */
public final class MsnApiInputComponent extends LocalInputComponentBase 
	implements RawDocumentsProducer {
    private final static String CARROTSEARCH_APPID = "DE531D8A42139F590B253CADFAD7A86172F93B96";

    /** Maximum number of results (starting offset + length) */
	private final static int MAXIMUM_RESULTS = 1000;

    /** Maximum allowed results per query*/
    private final static int MAXIMUM_RESULTS_PERQUERY = 50;

	private static Logger log = Logger.getLogger(MsnApiInputComponent.class);

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = toSet(RawDocumentsConsumer.class);

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = toSet(RawDocumentsProducer.class);

    /** Current "query". See the docs for query formats. */
    private String query;

    /** Current {@link RawDocumentsConsumer} to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /**
     * MSN search service wrapper.
     */
    private final MSNSearchPortType service;

    /**
     * Application id for querying MSN Search.
     */
    private final String appid;

    /**
     * Create an input component with the default service descriptor and
     * a custom application identifier.
     */
    public MsnApiInputComponent(String appid) {
        this.appid = appid;

        try {
            this.service = new MSNSearchServiceLocator().getMSNSearchPort();
        } catch (ServiceException e) {
            throw new RuntimeException("Could not initialize MSN service.", e);
        }
    }

    /**
     * Creates an input component with the default service descriptor
     * and Carrot Search's application identifier.
     */
    public MsnApiInputComponent() {
        this(CARROTSEARCH_APPID);
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

		    final int resultsRequested = super.getIntFromRequestContext(requestContext, LocalInputComponent.PARAM_REQUESTED_RESULTS, 100);
		    int results = Math.min(resultsRequested, MAXIMUM_RESULTS);

		    log.info("MSN API query (" + results + "):" + query);
            
            // perform initial query and alter total number of result if necessary.
            final SearchRequest request = new SearchRequest(
                    appid,            // application id 
                    query,            // query
                    "en-US",          // culture info
                    SafeSearchOptions.Off,                 // safe search options
                    new String [] {SearchFlagsNull._None}, // search flags
                    null,                                  // location
                    null                                   // requests
                    );

            final String [] fields = new String [] {
                    ResultFieldMaskNull._Url,
                    ResultFieldMaskNull._Title,
                    ResultFieldMaskNull._Description
                    };

            int id = 0;
            int offset = 0;
            while (results > 0) {
                final SourceRequest sourceRequest = new SourceRequest(
                        SourceType.Web,
                        offset, Math.min(results, MAXIMUM_RESULTS_PERQUERY), 
                        fields);

                request.setRequests(new SourceRequest [] {sourceRequest});
                final SourceResponse [] responses = service.search(request).getResponses();
                
                if (responses.length != 1) {
                    log.warn("More than one response for a search: " + responses.length);
                }
                
                final SourceResponse response = responses[0];
                
                if (id == 0) {
                    // adjust total approximation.
                    results = Math.min(results, response.getTotal());
                }
                
                // feed documents.
                final Result [] searchResults = response.getResults();
                final int fetchSize = Math.min(results, MAXIMUM_RESULTS_PERQUERY);
                if (searchResults.length != fetchSize) {
                    log.warn("Requested results: " + fetchSize
                            + ", but received: " + searchResults.length);
                }

                for (int j = 0; j < searchResults.length; j++) {
                    if (searchResults[j].getUrl() == null) {
                        log.warn("Empty URL in search result.");
                        continue;
                    }

                    final String docId = Integer.toString(id);
                    this.rawDocumentConsumer.addDocument(new RawDocumentBase(
                            searchResults[j].getUrl(),
                            StringUtils.removeMarkup(searchResults[j].getTitle()),
                            StringUtils.removeMarkup(searchResults[j].getDescription()))
                    {
                        public Object getId() {
                            return docId;
                        }
                    });

                    id++;
                }
                results -= fetchSize;
            }
	    } catch (Throwable e) {
	    	if (e instanceof ProcessingException) {
	    		throw (ProcessingException) e;
	    	}
	        throw new ProcessingException("Could not process query.", e);
	    }
    }

    public String getName() {
        return "MSN API Input";
    }
}
