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

package com.dawidweiss.carrot.core.local.impl;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;

/**
 * Sends only one {@link RawDocument} to the components down the chain. This
 * component would be useful mainly for printing out error messages etc.
 * 
 * @author Stanislaw Osinski
 * @version $Revision: 927 $
 */
public class DummyLocalInputComponent extends LocalInputComponentBase implements
    RawDocumentsProducer
{
    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsConsumer.class
        }));

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsProducer.class
        }));

    /** Current query, for information only */
    private String query;

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /** */
    private RawDocument rawDocument;

    public DummyLocalInputComponent()
    {
        
    }
    
    /**
     * Creates an instance of this input component
     * 
     * @param rawDocument the {@link RawDocument} to feed
     * @param query the query to feed
     */
    public DummyLocalInputComponent(RawDocument rawDocument, String query)
    {
        this.rawDocument = rawDocument;
        this.query = query;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalInputComponent#setQuery(java.lang.String)
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
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
     * @see com.dawidweiss.carrot.core.local.LocalInputComponent#setNext(com.dawidweiss.carrot.core.local.LocalComponent)
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
     * @see com.dawidweiss.carrot.core.local.LocalComponent#startProcessing(com.dawidweiss.carrot.core.local.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        // Pass the query for the following components
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_QUERY, query);

        super.startProcessing(requestContext);

        // Pass the actual document count
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS, new Integer(1));

        rawDocumentConsumer.addDocument(rawDocument);
    }
}