
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

package com.digitalpebble.carrot.input.opensearch;

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

/**
 * Local input component using OpenSearch API.
 * 
 * @author Julien Nioche
 */
public class OpenSearchInputComponent extends LocalInputComponentBase implements RawDocumentsProducer {

    /**
     * Max. number of results acquired from an open search source.
     */
    private final static int MAXIMUM_RESULTS = 1000;

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

    // Method from LocalInputComponent
    public void setQuery(String q) {
        this.query = q;
    }

    public String getName() {
        return "OpenSearch Input";
    }

    public void startProcessing(RequestContext requestContext) throws ProcessingException {
        if (service == null) {
            throw new ProcessingException("OpenSearch service not set.");
        }
        try {
            requestContext.getRequestParameters().put(LocalInputComponent.PARAM_QUERY, this.query);
            super.startProcessing(requestContext);
            if (this.query == null || "".equals(query)) {
                // empty query. just return.
                return;
            }
            final int resultsRequested = super.getIntFromRequestContext(requestContext,
                    LocalInputComponent.PARAM_REQUESTED_RESULTS, 100);
            int results = Math.min(resultsRequested, MAXIMUM_RESULTS);
            log.info("OpenSearch query (" + results + "):" + query);
            final OpenSearchResult[] docs = service.query(query, results);
            for (int i = 0; i < docs.length; i++) {
                final int id = i;
                rawDocumentConsumer.addDocument(new RawDocumentBase(docs[i].url, 
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
}
