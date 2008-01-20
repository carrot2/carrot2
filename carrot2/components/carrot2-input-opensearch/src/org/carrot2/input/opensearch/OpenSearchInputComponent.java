
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

package org.carrot2.input.opensearch;

import java.util.*;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.RawDocumentsConsumer;
import org.carrot2.core.clustering.RawDocumentsProducer;

/**
 * Local input component using OpenSearch API.
 * 
 * @author Julien Nioche
 */
public class OpenSearchInputComponent extends LocalInputComponentBase implements RawDocumentsProducer {

    /**
     * Default value for {@link #maxResults} field.
     */
    private final static int DEFAULT_MAX_RESULTS = 1000;
    final static String [] SOURCES = new String [] { "OpenSearch" };

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(Arrays
            .asList(new Object[] { RawDocumentsConsumer.class }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(Arrays
            .asList(new Object[] { RawDocumentsProducer.class }));

    private static Logger log = Logger.getLogger(OpenSearchInputComponent.class);

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /** Current "query". See the docs for query formats. */
    private String query;

    private OpenSearchService service = null;
    
    /**
     * Maximum number of results allowed from the open 
     * search input component.
     * 
     * @see #setMaxResults(int)
     */
    private int maxResults = DEFAULT_MAX_RESULTS;

    public OpenSearchInputComponent(String URLtemplate) {
        final OpenSearchService service = new OpenSearchService(URLtemplate);
        this.service = service;
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

    public void setQuery(String q) {
        this.query = q;
    }

    public String getName() {
        return "OpenSearch Input";
    }

    public void startProcessing(RequestContext requestContext) throws ProcessingException {
        super.startProcessing(requestContext);

        if (service == null) {
            throw new ProcessingException("OpenSearch service not set.");
        }

        try {
            requestContext.getRequestParameters().put(LocalInputComponent.PARAM_QUERY, this.query);

            if (this.query == null || "".equals(query)) {
                // empty query. just return.
                return;
            }

            final int results = Math.min(
                super.getIntFromRequestContext(requestContext, 
                    LocalInputComponent.PARAM_REQUESTED_RESULTS, 
                    this.maxResults), 
                this.maxResults);

            log.info("OpenSearch query (" + results + "):" + query);
            service.query(query, results, rawDocumentConsumer);
        } catch (Throwable e) {
            if (e instanceof ProcessingException) {
                throw (ProcessingException) e;
            }
            throw new ProcessingException("Could not process query.", e);
        }
    }
    
    /**
     * Sets the maximum number of results allowed for a single query. Note that
     * each engine will have its own limits.
     * 
     * @param maxResults Max number of results (1...N).
     */
    public void setMaxResults(int maxResults) {
        if (maxResults <= 0) {
            throw new IllegalArgumentException("Max results must be greater than 0: " + maxResults);
        }
        this.maxResults = maxResults;
    }
}
