/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.chilang.carrot.filter.cluster.local;

import java.util.*;

import com.chilang.carrot.filter.cluster.rough.clustering.*;
import com.chilang.carrot.filter.cluster.rough.data.*;
import com.chilang.carrot.filter.cluster.rough.measure.*;
import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.profiling.*;

/**
 * A local interface to the RoughKMeans clustering algorithm. Parts copied &
 * pasted from the remote interface.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class RoughKMeansLocalFilterComponent extends
    ProfiledLocalFilterComponentBase implements RawDocumentsConsumer,
    RawClustersProducer, LocalFilterComponent
{
    /**
     * Set for each clustered {@link RawDocument}instance to a {@link Double}
     * equal to the score of the document as a member of a cluster.
     */
    public static final String PROPERTY_CLUSTER_MEMBER_SCORE = "mscore";

    /** Documents to be clustered */
    private List rawDocuments;

    /** The algorithm's document references */
    private List documentReferences;

    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsProducer.class }));

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsConsumer.class, RawClustersProducer.class }));

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object []
        { RawClustersProducer.class }));

    /** Raw clusters consumer */
    private RawClustersConsumer rawClustersConsumer;

    /**
     *  
     */
    public RoughKMeansLocalFilterComponent()
    {
    }

    /**
     * @param parameters
     */
    public RoughKMeansLocalFilterComponent(Map parameters)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer#addDocument(com.dawidweiss.carrot.core.local.clustering.RawDocument)
     */
    public void addDocument(RawDocument doc) throws ProcessingException
    {
        startTimer();

        // Make up some fake id
        String id = Integer.toString(rawDocuments.size());

        SnippetDocument snippetDocument = new SnippetDocument(id);
        snippetDocument.setTitle(doc.getTitle());
        snippetDocument.setUrl(doc.getUrl());
        snippetDocument.setDescription(doc.getSnippet());
        documentReferences.add(snippetDocument);

        rawDocuments.add(doc);

        stopTimer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#init(com.dawidweiss.carrot.core.local.LocalControllerContext)
     */
    public void init(LocalControllerContext context)
        throws InstantiationException
    {
        super.init(context);
        rawDocuments = new ArrayList();
        documentReferences = new ArrayList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalFilterComponent#setNext(com.dawidweiss.carrot.core.local.LocalComponent)
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        rawClustersConsumer = (RawClustersConsumer) next;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();

        documentReferences.clear();
        rawDocuments.clear();
        rawClustersConsumer = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#startProcessing(com.dawidweiss.carrot.core.local.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#endProcessing()
     */
    public void endProcessing() throws ProcessingException
    {
        startTimer();

        IRContext context = new WebIRContext(" ", documentReferences);

        // Parameter values taken from the remote descriptor
        // carrot2.process.trc.phrase-rough-kmeans-google
        int numberOfClusters = 15;
        double membershipThreshold = 0.3;
        int cooccurrenceThreshold = 5;
        boolean usePhrase = true;

        // Cluster
        Clusterer clusterer = new RoughClusterer(numberOfClusters,
            membershipThreshold, SimilarityFactory.getCosine(),
            cooccurrenceThreshold, usePhrase);
        clusterer.setContext(context);
        clusterer.cluster();

        // Convert the results to the local filter format
        XCluster [] xClusters = clusterer.getClusters();
        List rawClusters = new ArrayList(xClusters.length);
        for (int i = 0; i < xClusters.length; i++)
        {
            RawClusterBase rawCluster = new RawClusterBase();

            String [] labels = xClusters[i].getLabel();
            for (int j = 0; j < labels.length; j++)
            {
                rawCluster.addLabel(labels[j]);
            }

            XCluster.Member [] members = xClusters[i].getMembers();
            for (int j = 0; j < members.length; j++)
            {
                int index = Integer.parseInt(members[j].getSnippet().getId());
                RawDocument rawDocument = (RawDocument) rawDocuments.get(index);
                rawDocument.setProperty(PROPERTY_CLUSTER_MEMBER_SCORE,
                    new Double(members[j].getMembership()));
                rawCluster.addDocument(rawDocument);
            }

            rawClusters.add(rawCluster);
        }

        // Check for the junk clusters
        RawClusterBase junk = (RawClusterBase) rawClusters.get(rawClusters
            .size() - 1);
        if (junk.getClusterDescription().get(0).toString().equalsIgnoreCase(
            "other"))
        {
            junk.setProperty(RawCluster.PROPERTY_JUNK_CLUSTER, Boolean.TRUE);
        }

        // Don't want to time the following components, so stop here
        stopTimer();

        for (Iterator iter = rawClusters.iterator(); iter.hasNext();)
        {
            RawCluster rawCluster = (RawCluster) iter.next();
            rawClustersConsumer.addCluster(rawCluster);
        }

        super.endProcessing();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getName()
     */
    public String getName()
    {
        return "RoughKMeans";
    }
}