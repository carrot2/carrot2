
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

package com.kgolembniak.carrot.filter.haogfi.local;


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
import com.kgolembniak.carrot.filter.fi.FIParameters;
import com.kgolembniak.carrot.filter.fi.algorithm.AprioriEngine;
import com.kgolembniak.carrot.filter.haog.algorithm.FIGroupper;

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
        documents = new ArrayList();
        aprioriEngine = new AprioriEngine();
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

        documents.clear();
        aprioriEngine.flushResources();
        
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

        //FI Part
        FIParameters params = FIParameters.fromMap(
        		this.requestContext.getRequestParameters());
        List clusters = aprioriEngine.getClusters(params);
        
        //HAOG Part
        FIGroupper groupper = 
        	new FIGroupper(clusters, documents, params, rawClustersConsumer);
        groupper.process();

        stopTimer();

        super.endProcessing();
    }
	
}