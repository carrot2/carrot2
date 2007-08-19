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

package org.carrot2.filter.facet;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.profiling.ProfiledLocalFilterComponentBase;

/**
 */
public class FacetGeneratorLocalFilterComponent extends ProfiledLocalFilterComponentBase implements
    RawDocumentsConsumer, RawClustersProducer, LocalFilterComponent
{
    /** Documents to be clustered */
    private ArrayList documents;

    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsProducer.class
        }));

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsConsumer.class, RawClustersProducer.class
        }));

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object []
        {
            RawClustersConsumer.class
        }));

    /** Raw clusters consumer */
    private RawClustersConsumer rawClustersConsumer;

    /** The actual facet generator to handle the requests */
    private FacetGenerator facetGenerator;

    public FacetGeneratorLocalFilterComponent(FacetGenerator facetGenerator)
    {
        this.facetGenerator = facetGenerator;
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

    public void startProcessing(RequestContext requestContext) throws ProcessingException
    {
        super.startProcessing(requestContext);
        this.documents = new ArrayList();
    }

    public void addDocument(RawDocument doc) throws ProcessingException
    {
        documents.add(doc);
    }

    public void endProcessing() throws ProcessingException
    {
        startTimer();
        List facets = facetGenerator.generateFacets(documents);
        stopTimer();
        
        for (Iterator it = facets.iterator(); it.hasNext();)
        {
            RawCluster facet = (RawCluster) it.next();
            rawClustersConsumer.addCluster(facet);
        }
        super.endProcessing();
    }

    public String getName()
    {
        return "Facet generator";
    }
}