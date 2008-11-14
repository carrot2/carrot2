
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.haog.haogfi.local;


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
import org.carrot2.filter.haog.fi.FIParameters;
import org.carrot2.filter.haog.fi.algorithm.AprioriEngine;
import org.carrot2.filter.haog.haog.algorithm.FIGroupper;
import org.carrot2.filter.haog.haog.measure.Statistics;

/**
 * Implementation of Hierarchical Arrangement of Overlapping Groups based
 * on Apriori algorithm.
 * @author Karol Gołembniak
 */
public class HAOGFILocalFilterComponent extends ProfiledLocalFilterComponentBase
    implements TokenizedDocumentsConsumer, RawClustersProducer, LocalFilterComponent
{
	public String getName()
    {
        return "haog-fi";
    }

	/** Documents to be clustered */
	private List documents;
	
	private AprioriEngine aprioriEngine;
	
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
        aprioriEngine.addTokenizedDocument(doc);
        stopTimer();
	}

    public void flushResources()
    {
        super.flushResources();

        documents = null;
        aprioriEngine = null;
        
        rawClustersConsumer = null;
        requestContext = null;
    }

    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);
        this.requestContext = requestContext;
        aprioriEngine = new AprioriEngine();
        documents = new ArrayList();        
    }

    public void endProcessing() throws ProcessingException
    {
    	//Uncomment this to get statistics
    	//Statistics.getInstance().enable();
        Statistics.getInstance().startMeasure();
        startTimer();

        Statistics.getInstance().startTimer();
        //FI Part
        FIParameters params = FIParameters.fromMap(
        		this.requestContext.getRequestParameters());
        Statistics.getInstance().startTimer();
        List clusters = aprioriEngine.getClusters(params);
        Statistics.getInstance().endTimer("Cluster Creation Time");
        
        //HAOG Part
        FIGroupper groupper = 
        	new FIGroupper(clusters, documents, params, rawClustersConsumer);
        groupper.process();
        Statistics.getInstance().endTimer("Processing Time");

        stopTimer();
        Statistics.getInstance().endMeasure();

        super.endProcessing();
    }
	
}