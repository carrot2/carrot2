
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

package org.carrot2.input.pubmed;

import java.util.*;

import org.apache.log4j.*;
import org.carrot2.core.*;
import org.carrot2.core.clustering.*;


/**
 * An input component for fetching search results from the PubMed database.
 * 
 * @author Stanislaw Osinski
 */
public class PubMedInputComponent
    extends LocalInputComponentBase
    implements RawDocumentsProducer
{

    private final static int MAXIMUM_RESULTS = 1000;
    private final static String [] SOURCES = new String [] { "PubMed" };

    private static Logger log = Logger.getLogger(PubMedInputComponent.class);

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(Arrays
            .asList(new Object[] { RawDocumentsConsumer.class }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(Arrays
            .asList(new Object[] { RawDocumentsProducer.class }));

    /** Current "query". See the docs for query formats. */
    private String query;

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /**
     * PubMed search service wrapper.
     */
    private PubMedSearchService service;


    /**
     * Create an input component with the default service descriptor.
     */
    public PubMedInputComponent()
    {
        this.service = new PubMedSearchService();
    }


    public void setQuery(String query)
    {
        this.query = query;
    }


    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }


    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }


    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        rawDocumentConsumer = (RawDocumentsConsumer)next;
    }


    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {

        try {
            requestContext.getRequestParameters().put(
                    LocalInputComponent.PARAM_QUERY, this.query);
            super.startProcessing(requestContext);

            if (this.query == null || "".equals(query)) {
                // empty query. just return.
                return;
            }

            final int resultsRequested = super.getIntFromRequestContext(
                    requestContext,
                    LocalInputComponent.PARAM_REQUESTED_RESULTS, 100);

            int results = Math.min(resultsRequested, MAXIMUM_RESULTS);

            log.info("PubMed query (" + results + "):" + query);
            final PubMedSearchResultConsumer consumer = new PubMedSearchResultConsumer() {
                int id = 0;

                public void add(final PubMedSearchResult result)
                    throws ProcessingException
                {
                    RawDocumentSnippet snippet = new RawDocumentSnippet(Integer
                            .toString(id), result.title, result.summary,
                            result.url, id);
                    snippet.setProperty(RawDocument.PROPERTY_SOURCES, SOURCES);
                    rawDocumentConsumer.addDocument(snippet);
                    id++;
                }
            };
            service.query(query, results, consumer);
        }
        catch (Throwable e) {
            if (e instanceof ProcessingException) {
                throw (ProcessingException)e;
            }
            throw new ProcessingException("Could not process query.", e);
        }
    }


    public String getName()
    {
        return "PubMed Input";
    }
}
