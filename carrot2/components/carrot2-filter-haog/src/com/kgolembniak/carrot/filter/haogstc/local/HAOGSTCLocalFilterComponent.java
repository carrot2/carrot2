
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

package com.kgolembniak.carrot.filter.haogstc.local;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dawidweiss.carrot.core.local.LocalComponent;
import com.dawidweiss.carrot.core.local.LocalControllerContext;
import com.dawidweiss.carrot.core.local.LocalFilterComponent;
import com.dawidweiss.carrot.core.local.ProcessingException;
import com.dawidweiss.carrot.core.local.RequestContext;
import com.dawidweiss.carrot.core.local.clustering.RawClustersConsumer;
import com.dawidweiss.carrot.core.local.clustering.RawClustersProducer;
import com.dawidweiss.carrot.core.local.clustering.TokenizedDocument;
import com.dawidweiss.carrot.core.local.clustering.TokenizedDocumentsConsumer;
import com.dawidweiss.carrot.core.local.clustering.TokenizedDocumentsProducer;
import com.dawidweiss.carrot.core.local.profiling.ProfiledLocalFilterComponentBase;
import com.dawidweiss.carrot.filter.stc.StcParameters;
import com.dawidweiss.carrot.filter.stc.algorithm.BaseCluster;
import com.dawidweiss.carrot.filter.stc.algorithm.DocReference;
import com.dawidweiss.carrot.filter.stc.algorithm.STCEngine;
import com.kgolembniak.carrot.filter.haog.algorithm.STCGroupper;

/**
 * Implementation of Hierarchical Arrangement of Overlapping Groups.
 * @author Karol Gołembniak
 */
public class HAOGSTCLocalFilterComponent extends ProfiledLocalFilterComponentBase
    implements TokenizedDocumentsConsumer, RawClustersProducer, LocalFilterComponent
{
	public String getName()
    {
        return "haog-stc";
    }

	/** Documents to be clustered */
	private List documents;
	
    /** STC's document references */
    private List documentReferences;

    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        { TokenizedDocumentsProducer.class }));

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        { TokenizedDocumentsConsumer.class, RawClustersProducer.class }));

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object []
        { RawClustersConsumer.class }));

    /** Raw clusters consumer */
    private RawClustersConsumer rawClustersConsumer;

    /**
     * Current request's context.
     */
    private RequestContext requestContext;
    
    /**
     * Parameters for cluster merging and cluster's description creation
     */
    private StcParameters params;

    public void init(LocalControllerContext context)
        throws InstantiationException
    {
        super.init(context);
        documents = new ArrayList();
        documentReferences = new ArrayList();
    }

    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        rawClustersConsumer = (RawClustersConsumer) next;
    }

	public void addDocument(TokenizedDocument doc) throws ProcessingException {
        startTimer();

        documents.add(doc);

        final DocReference documentReference = new DocReference(doc);
        documentReferences.add(documentReference);

        stopTimer();
	}

    public void flushResources()
    {
        super.flushResources();

        documentReferences.clear();
        documents.clear();
        rawClustersConsumer = null;
        this.requestContext = null;
    }

    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);
        this.requestContext = requestContext;
    }

    public void endProcessing() throws ProcessingException
    {
        startTimer();

        //STC part
        final STCEngine stcEngine = new STCEngine(documentReferences);
        stcEngine.createSuffixTree();
        params = StcParameters.fromMap(
        		this.requestContext.getRequestParameters());

        stcEngine.createBaseClusters(params);
    	final List clusters = stcEngine.getBaseClusters();

        //HAOG part
        connectBaseClusters(clusters);
        STCGroupper groupper = 
        	new STCGroupper(clusters, documents, params, rawClustersConsumer);
        groupper.process();
   	
        stopTimer();

        super.endProcessing();
    }
	
    /**
     * This method connects base clusters and creates a graph from them. 
     * A parameter MergeTreshold is used as a condition to connect clusters.
     */
    private void connectBaseClusters(List clusters){
        final StcParameters params = StcParameters.fromMap(
                this.requestContext.getRequestParameters());
    	final float CONNECT_CONDITION = params.getMergeThreshold();
    	
    	for (int i = 1; i < clusters.size(); i++){
            BaseCluster a;
            BaseCluster b;
            
            a = (BaseCluster) clusters.get(i);
            final long a_docCount = a.getNode().getSuffixedDocumentsCount();
            long b_docCount;
            
            for (int j = 0; j < i; j++){
                b = (BaseCluster) clusters.get(j);
                b_docCount = b.getNode().getSuffixedDocumentsCount();
                final double a_and_b_docCount = a.getNode()
                	.getInternalDocumentsRepresentation().numberOfSetBitsAfterAnd(
                        b.getNode().getInternalDocumentsRepresentation()
                    );

                //This processing is bidirectional, not like Zamir's STC -> we
                //have ordered graph
                if ((a_and_b_docCount / b_docCount) >= CONNECT_CONDITION){
                    a.addLink(b);
                }

                if ((a_and_b_docCount / a_docCount) >= CONNECT_CONDITION){
                    b.addLink(a);
                }
            }
        }
    }

}