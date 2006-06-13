
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

package fuzzyAnts;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.profiling.ProfiledLocalFilterComponentBase;

/**
 * A local interface to the FuzzyAnts clustering algorithm.
 * 
 * @author Dawid Weiss
 * @version $$
 */
public class FuzzyAntsLocalFilterComponent extends ProfiledLocalFilterComponentBase
    implements TokenizedDocumentsConsumer, RawClustersProducer, LocalFilterComponent
{
    /** Documents to be clustered */
    private List documents;

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
     * Implements {@link TokenizedDocumentsConsumer#addDocument(TokenizedDocument)}.
     */
    public void addDocument(TokenizedDocument doc) throws ProcessingException
    {
        startTimer();
        documents.add(doc);
        stopTimer();
    }

    public void init(LocalControllerContext context)
        throws InstantiationException
    {
        super.init(context);
        documents = new ArrayList();
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

    public void flushResources()
    {
        super.flushResources();

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
        
        String query = (String) this.requestContext.getRequestParameters().get(
                LocalInputComponent.PARAM_QUERY);
        if ("".equals(query) || query == null) query = "";

        final FuzzyAntsParameters parameters = FuzzyAntsParameters.fromMap(
                this.requestContext.getRequestParameters());

        //obtain clusters
        DocumentClustering opl = new DocumentClustering(
                0, this.documents, query, true, Constants.BINARY, 
                parameters);
        final List rawClusters = opl.getGroups(); // <RawCluster>

        // Don't want to time the following components, so stop here
        stopTimer();

        for (Iterator iter = rawClusters.iterator(); iter.hasNext();)
        {
            RawCluster rawCluster = (RawCluster) iter.next();
            rawClustersConsumer.addCluster(rawCluster);
        }

        super.endProcessing();
    }

    public String getName()
    {
        return "FuzzyAnts";
    }
}