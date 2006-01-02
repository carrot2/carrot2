
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.core.local.impl;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.LocalOutputComponent;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer;

import java.util.*;
import java.util.ArrayList;

/**
 * An utility implementation of an output component that implements {@link
 * RawDocumentsConsumer} interface and collects {@link RawDocument}objects to
 * an array returned at the end of processing.
 * 
 * @author Dawid Weiss
 * @version $Revision$
 */
public class DocumentsConsumerOutputComponent extends LocalOutputComponentBase
    implements LocalOutputComponent, RawDocumentsConsumer,
    TokenizedDocumentsConsumer
{
    /**
     * Capabilities exposed by this component.
     */
    private static final Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsConsumer.class, TokenizedDocumentsConsumer.class }));

    /**
     * Capabilities required of the predecessor of this component.
     */
    private static final Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsProducer.class }));

    /**
     * An array where documents received from the predecessor component are
     * stored.
     */
    private ArrayList documents = new ArrayList();

    /**
     * Returns an instance of {@link java.util.ArrayList}with references to
     * {@link RawDocument}instances.
     */
    public Object getResult()
    {
        return new ArrayList(this.documents);
    }

    /**
     * Provides an empty implementation.
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
    }

    /**
     * Provides an empty implementation
     */
    public void endProcessing() throws ProcessingException
    {
    }

    /**
     * Adds a document to the list of documents to be returned as the result.
     */
    public void addDocument(RawDocument doc) throws ProcessingException
    {
        documents.add(doc);
    }

    /**
     * Clears clusters and documents lists and prepares the component for reuse.
     */
    public void flushResources()
    {
        super.flushResources();
        documents.clear();
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.TokenizedDocumentsConsumer#addDocument(com.dawidweiss.carrot.core.local.clustering.TokenizedDocument)
     */
    public void addDocument(TokenizedDocument doc) throws ProcessingException
    {
        documents.add(doc);
    }
}