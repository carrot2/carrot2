
/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 *
 * Sponsored by: CCG, Inc.
 */

package com.dawidweiss.carrot.core.local.impl;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.LocalOutputComponent;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.clustering.RawClustersConsumer;
import com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer;

import java.util.*;
import java.util.ArrayList;
import java.util.List;


/**
 * A utility implementation of an output component that implements {@link
 * RawClustersConsumer} and {@link RawDocumentsConsumer} interfaces and
 * collects {@link RawCluster} objects and {@link RawDocument} objects to two
 * arrays returned as the result of a process.
 *
 * @author Dawid Weiss
 * @version $Revision$
 */
public class ClustersConsumerOutputComponent extends LocalOutputComponentBase
    implements LocalOutputComponent, RawClustersConsumer, RawDocumentsConsumer {
    /**
     * Capabilities exposed by this component.
     */
    private static final Set CAPABILITIES_COMPONENT = new HashSet(Arrays.asList(
                new Object[] {
                    RawClustersConsumer.class, RawDocumentsConsumer.class
                }));

    /**
     * Capabilities required of the predecessor component. Only {@link
     * RawClustersProducer} is required, because we can thus neglect the fact
     * that document references are not passed. Clusters have references to
     * these documents anyway.
     */
    private static final Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays.asList(
                new Object[] { RawClustersProducer.class }));

    /**
     * An array where clusters received from the predecessor component are
     * stored.
     */
    private ArrayList clusters = new ArrayList();

    /**
     * An array where documents received from the predecessor component are
     * stored.
     */
    private ArrayList documents = new ArrayList();

    /** Context parameters for the current query, or <code>null</code>. */
    private Map contextParams = null;

    /**
     * An class that stores documents and clusters collected during the
     * processing of a query. The result returned by  {@link
     * com.dawidweiss.carrot.core.local.LocalProcess#query(com.dawidweiss.carrot.core.local.RequestContext,String
     * )} is of this type.
     */
    public static class Result {
        /**
         * An array where clusters received from the predecessor component are
         * stored.
         */
        public List clusters = new ArrayList();

        /**
         * An array where documents received from the predecessor component are
         * stored.
         */
        public List documents = new ArrayList();

        /** A shallow copy of the context parameters. */
        public Map context = null;

        /**
         * Creates a new Result object.
         *
         * @param clusters An array of clusters
         * @param documents An array of documents.
         */
        private Result(ArrayList clusters, ArrayList documents, Map contextParams) {
            Result.this.clusters = Collections.unmodifiableList(new ArrayList(
                        clusters));
            Result.this.documents = Collections.unmodifiableList(new ArrayList(
                        documents));
            Result.this.context = new HashMap(contextParams);
            
        }
    }

    /**
     * @return Returns an instance of {@link Result} with documents and
     *         clusters collected during the query execution.
     */
    public Object getResult() {
        return new Result(this.clusters, this.documents, this.contextParams);
    }

    /**
     * Provides an empty implementation.
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException {
    	contextParams = requestContext.getRequestParameters();
    }

    /**
     * Provides an empty implementation
     */
    public void endProcessing() throws ProcessingException {
    }

    /**
     * Adds a cluster to the list of clusters to be returned as the result.
     */
    public void addCluster(RawCluster cluster) throws ProcessingException {
        clusters.add(cluster);
    }

    /**
     * Adds a document to the list of documents to be returned as the result.
     */
    public void addDocument(RawDocument doc) throws ProcessingException {
        documents.add(doc);
    }

    /**
     * Clears clusters and documents lists and prepares the component for
     * reuse.
     */
    public void flushResources() {
        super.flushResources();
        clusters.clear();
        documents.clear();
        contextParams = null;
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities() {
        return CAPABILITIES_COMPONENT;
    }

    /**
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities() {
        return CAPABILITIES_PREDECESSOR;
    }
}
