/*
 * RawClustersConsumerLocalOutputComponent.java
 * 
 * Created on 2004-06-29
 */
package com.dawidweiss.carrot.core.local.impl;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;

/**
 * Collects {@link com.dawidweiss.carrot.core.local.clustering.RawCluster}s
 * producesd by its predecessor and returns them as a {@link java.util.List}.
 * 
 * @author stachoo
 */
public class RawClustersConsumerLocalOutputComponent extends
    LocalOutputComponentBase implements RawClustersConsumer
{
    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsProducer.class }));

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsConsumer.class }));

    /** Raw clusters */
    private List rawClusters;

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawClustersConsumer#addCluster(com.dawidweiss.carrot.core.local.clustering.RawCluster)
     */
    public void addCluster(RawCluster cluster) throws ProcessingException
    {
        rawClusters.add(cluster);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalOutputComponent#getResult()
     */
    public Object getResult()
    {
        return new ArrayList(rawClusters);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        rawClusters.clear();
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
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
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
        rawClusters = new ArrayList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getName()
     */
    public String getName()
    {
        return "Output";
    }
}