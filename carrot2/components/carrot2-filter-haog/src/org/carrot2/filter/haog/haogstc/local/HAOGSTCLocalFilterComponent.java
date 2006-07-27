
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

package org.carrot2.filter.haog.haogstc.local;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalControllerContext;
import org.carrot2.core.LocalFilterComponent;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.RequestContext;
import org.carrot2.core.clustering.RawClustersConsumer;
import org.carrot2.core.clustering.RawClustersProducer;
import org.carrot2.core.clustering.TokenizedDocument;
import org.carrot2.core.clustering.TokenizedDocumentsConsumer;
import org.carrot2.core.clustering.TokenizedDocumentsProducer;
import org.carrot2.core.profiling.ProfiledLocalFilterComponentBase;
import org.carrot2.filter.stc.StcParameters;
import org.carrot2.filter.stc.algorithm.BaseCluster;
import org.carrot2.filter.stc.algorithm.DocReference;
import org.carrot2.filter.stc.algorithm.STCEngine;
import org.carrot2.filter.haog.haog.algorithm.STCGroupper;
import org.carrot2.filter.haog.haog.measure.Statistics;
import org.carrot2.filter.haog.stc.STCParameters;

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
    	//Uncomment this to get statistics
    	//Statistics.getInstance().enable();
        Statistics.getInstance().startMeasure();
        startTimer();

        Statistics.getInstance().startTimer();
        //STC part
        final STCParameters params = STCParameters.fromMap(
        		this.requestContext.getRequestParameters());
        final StcParameters stcParams = StcParameters.fromMap(
        		this.requestContext.getRequestParameters());

        Statistics.getInstance().startTimer();
        final STCEngine stcEngine = new STCEngine(documentReferences);
        stcEngine.createSuffixTree();
        stcEngine.createBaseClusters(stcParams);
        final List clusters = stcEngine.getBaseClusters();
        Statistics.getInstance().endTimer("Cluster Creation Time");

        //HAOG part
        connectBaseClusters(clusters);
        STCGroupper groupper = 
        	new STCGroupper(clusters, documents, params, rawClustersConsumer);
        groupper.process();
        Statistics.getInstance().endTimer("Processing Time");
   	
        stopTimer();
        Statistics.getInstance().endMeasure();

        super.endProcessing();
    }
	
    /**
     * This method connects base clusters and creates a graph from them. 
     * A parameter MergeTreshold is used as a condition to connect clusters.
     */
    private void connectBaseClusters(List clusters){
        final STCParameters params = STCParameters.fromMap(
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