/*
 * LingoLocalFilterComponent.java
 * 
 * Created on 2004-06-29
 */
package com.stachoodev.carrot.filter.lingo.local;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.profiling.*;
import com.stachoodev.carrot.filter.lingo.algorithm.*;

/**
 * @author stachoo
 */
public class LingoLocalFilterComponent extends ProfiledLocalFilterComponentBase
    implements TokenizedDocumentsConsumer, RawClustersProducer,
    LocalFilterComponent
{
    /** Document to be clustered */
    private List tokenizedDocuments;

    /** And instance of Lingo algorithm */
    private Lingo lingo;

    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        { TokenizedDocumentsProducer.class }));

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        { TokenizedDocumentsConsumer.class }));

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object []
        { RawClustersProducer.class }));

    /** Raw clusters consumer */
    private RawClustersConsumer rawClustersConsumer;

    /**
     * 
     */
    public LingoLocalFilterComponent()
    {
        lingo = new Lingo();
    }

    /**
     * @param parameters
     */
    public LingoLocalFilterComponent(Map parameters)
    {
        lingo = new Lingo(parameters);
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
        tokenizedDocuments = new ArrayList();
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

        tokenizedDocuments.clear();
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
     * @see com.dawidweiss.carrot.core.local.clustering.TokenizedDocumentsConsumer#addDocument(com.dawidweiss.carrot.core.local.clustering.TokenizedDocument)
     */
    public void addDocument(TokenizedDocument doc) throws ProcessingException
    {
        tokenizedDocuments.add(doc);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#endProcessing()
     */
    public void endProcessing() throws ProcessingException
    {
        // Unfortunately, Lingo does not support incremental processing, so we
        // need to wait for all documents to come before we start processing
        startTimer();
        List clusters = lingo.cluster(tokenizedDocuments);
        for (Iterator iter = clusters.iterator(); iter.hasNext();)
        {
            RawCluster cluster = (RawCluster) iter.next();
            rawClustersConsumer.addCluster(cluster);
        }
        stopTimer();

        super.endProcessing();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getName()
     */
    public String getName()
    {
        return "Lingo";
    }
}