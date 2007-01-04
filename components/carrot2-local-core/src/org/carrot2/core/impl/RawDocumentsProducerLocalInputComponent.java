
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

package org.carrot2.core.impl;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;

/**
 * Implements a dummy local input component, which sends all
 * {@link org.carrot2.core.clustering.RawDocument}s 
 * provided in the {@link #PARAM_SOURCE_RAW_DOCUMENTS} request parameter.
 * 
 * @author Stanislaw Osinski
 * @author Dawid Weiss
 * @version $Revision$
 */
public class RawDocumentsProducerLocalInputComponent extends
    LocalInputComponentBase implements RawDocumentsProducer
{
    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsConsumer.class }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsProducer.class }));

    /** Current query, for information only */
    private String query;

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /**
     * This property must be set to a {@link java.util.List} or a {@link Iterator} over a list of
     * {@link org.carrot2.core.clustering.RawDocument}s to be
     * propagated down the processing chain.
     */
    public static final String PARAM_SOURCE_RAW_DOCUMENTS = "source-docs";

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalInputComponent#setQuery(java.lang.String)
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        query = null;
        rawDocumentConsumer = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalInputComponent#setNext(org.carrot2.core.LocalComponent)
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        if (next instanceof RawDocumentsConsumer)
        {
            rawDocumentConsumer = (RawDocumentsConsumer) next;
        }
        else
        {
            rawDocumentConsumer = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#startProcessing(org.carrot2.core.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        // Initialize the subsequent components
        requestContext.getRequestParameters().put(LocalInputComponent.PARAM_QUERY, query);
        super.startProcessing(requestContext);

        final Object rawDocuments = requestContext.getRequestParameters().get(PARAM_SOURCE_RAW_DOCUMENTS);
        if (rawDocuments == null)
            throw new ProcessingException(
                "The PARAM_SOURCE_RAW_DOCUMENTS request parameter must not be null.");

        final Iterator iterator;
        if (rawDocuments instanceof List) {
            iterator = ((List) rawDocuments).iterator();
            requestContext.getRequestParameters().put(
                    LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS,
                    new Integer(((List) rawDocuments).size()));
        } else if (rawDocuments instanceof Iterator) {
            iterator = (Iterator) rawDocuments;
        } else {
            throw new ProcessingException("Unrecognized type of PARAM_SOURCE_RAW_DOCUMENTS (must be a list or an iterator)");
        }
        
        while (iterator.hasNext())
        {
            final RawDocument rawDocument = (RawDocument) iterator.next();
            rawDocumentConsumer.addDocument(rawDocument);
        }
    }
}