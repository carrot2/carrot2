
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
 * Implements a dummy input component, with capabilities to send:
 * <ul>
 * <li>{@link org.carrot2.core.clustering.RawDocument}s provided in the {@link #PARAM_SOURCE_RAW_DOCUMENTS} request
 * parameter,
 * <li>{@link org.carrot2.core.clustering.RawCluster}s provided in the {@link #PARAM_SOURCE_RAW_CLUSTERS} request
 * parameter,
 * </ul>
 * to components down the processing chain.
 */
public class ArrayInputComponent extends LocalInputComponentBase implements RawDocumentsProducer
{
    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = Collections.EMPTY_SET;

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = toSet(RawDocumentsProducer.class, RawClustersProducer.class);

    /** Current query, for information only */
    private String query;

    /** {@link RawDocumentsConsumer} to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /** {@link RawClustersConsumer} to feed */
    private RawClustersConsumer rawClustersConsumer;

    /**
     * This property must be set to a {@link java.util.List} or a {@link Iterator} over a list of {@link RawDocument}s
     * to be propagated down the processing chain.
     */
    public static final String PARAM_SOURCE_RAW_DOCUMENTS = "source-docs";

    /**
     * This property must be set to a {@link java.util.List} or a {@link Iterator} over a list of {@link RawCluster}s
     * to be propagated down the processing chain.
     */
    public static final String PARAM_SOURCE_RAW_CLUSTERS = "source-clusters";

    /*
     * 
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /*
     * 
     */
    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }

    /*
     * 
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }

    /*
     * 
     */
    public void flushResources()
    {
        super.flushResources();

        query = null;
        rawDocumentConsumer = null;
        rawClustersConsumer = null;
    }

    /*
     * 
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

        if (next instanceof RawClustersConsumer)
        {
            rawClustersConsumer = (RawClustersConsumer) next;
        }
        else
        {
            rawClustersConsumer = null;
        }
    }

    /*
     * 
     */
    public void startProcessing(RequestContext requestContext) throws ProcessingException
    {
        // Initialize the subsequent components
        if (this.query != null)
        {
            requestContext.getRequestParameters().put(LocalInputComponent.PARAM_QUERY, query);
        }

        super.startProcessing(requestContext);

        pushDocuments(requestContext);
        pushClusters(requestContext);
    }

    /**
     * Pushes {@link RawDocument}s down the chain.
     */
    private void pushDocuments(RequestContext requestContext) throws ProcessingException
    {
        final Object rawDocuments = requestContext.getRequestParameters().get(PARAM_SOURCE_RAW_DOCUMENTS);
        if (rawDocuments == null)
        {
            throw new ProcessingException("The PARAM_SOURCE_RAW_DOCUMENTS request parameter must not be null.");
        }

        final Iterator iterator;
        if (rawDocuments instanceof List)
        {
            iterator = ((List) rawDocuments).iterator();
            requestContext.getRequestParameters().put(LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS,
                new Integer(((List) rawDocuments).size()));
        }
        else if (rawDocuments instanceof Iterator)
        {
            iterator = (Iterator) rawDocuments;
        }
        else
        {
            throw new ProcessingException(
                "Unrecognized type of PARAM_SOURCE_RAW_DOCUMENTS (must be a List or an Iterator).");
        }

        while (iterator.hasNext())
        {
            final RawDocument rawDocument = (RawDocument) iterator.next();
            rawDocumentConsumer.addDocument(rawDocument);
        }
    }

    /**
     * Pushes {@link RawClusters}s down the chain.
     */
    private void pushClusters(RequestContext requestContext) throws ProcessingException
    {
        final Iterator iterator;

        final Object rawClusters = requestContext.getRequestParameters().get(PARAM_SOURCE_RAW_CLUSTERS);
        if (rawClusters == null)
        {
            // If no clusters, ignore.
            return;
        }

        if (rawClusters instanceof List)
        {
            iterator = ((List) rawClusters).iterator();
        }
        else if (rawClusters instanceof Iterator)
        {
            iterator = (Iterator) rawClusters;
        }
        else
        {
            throw new ProcessingException(
                "Unrecognized type of PARAM_SOURCE_RAW_CLUSTERS (must be a List or an Iterator).");
        }

        while (iterator.hasNext())
        {
            final RawCluster rawCluster = (RawCluster) iterator.next();
            rawClustersConsumer.addCluster(rawCluster);
        }
    }
}