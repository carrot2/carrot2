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
package com.stachoodev.carrot.output.local.metrics;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.impl.*;
import com.stachoodev.carrot.metrics.*;

/**
 * An output component that computes all known cluster metrics for the clusters
 * provided by the prevoius component in the chain.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class RawClustersMetricsLocalOutputComponent extends
    RawClustersConsumerLocalOutputComponent
{
    /** Values of metrics gathered by this component */
    private Map metricsValues;

    /** Request context */
    private RequestContext requestContext;

    /**
     * Returns the result of processing. A two-element {@link List}is returned,
     * the first element (index 0) of which is a {@link List}of clusters, and
     * the second is a {@link Map}of the computed cluster metrics.
     * 
     * @return the result of processing
     */
    public Object getResult()
    {
        return Arrays.asList(new Object []
        { new ArrayList(rawClusters), new HashMap(metricsValues) });
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
        metricsValues = new HashMap();
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

        this.requestContext = requestContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#endProcessing()
     */
    public void endProcessing() throws ProcessingException
    {
        startTimer();

        List originalRawClusters = (List) requestContext.getRequestParameters()
            .get(RawDocumentsProducer.PARAM_ORIGINAL_RAW_CLUSTERS);

        // Compute all known metrics
        RawClustersMetric [] metrics = AllKnownRawClustersMetrics
            .getAllRawClustersMetrics();
        for (int i = 0; i < metrics.length; i++)
        {
            Map metricValues = metrics[i].compute(rawClusters,
                originalRawClusters);
            if (metricValues != null)
            {
                metricsValues.putAll(metricValues);
            }
        }
        
        stopTimer();

        super.endProcessing();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        metricsValues.clear();
        requestContext = null;
    }
}