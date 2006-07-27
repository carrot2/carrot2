
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

package org.carrot2.filter.trc.carrot.filter.cluster.local;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering.Clusterer;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering.RoughClusterer;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.clustering.XCluster;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.data.IRContext;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.data.SnippetDocument;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.data.WebIRContext;
import org.carrot2.filter.trc.carrot.filter.cluster.rough.measure.SimilarityFactory;
import org.carrot2.core.LocalComponent;
import org.carrot2.core.LocalControllerContext;
import org.carrot2.core.LocalFilterComponent;
import org.carrot2.core.LocalInputComponent;
import org.carrot2.core.ProcessingException;
import org.carrot2.core.RequestContext;
import org.carrot2.core.clustering.RawCluster;
import org.carrot2.core.clustering.RawClusterBase;
import org.carrot2.core.clustering.RawClustersConsumer;
import org.carrot2.core.clustering.RawClustersProducer;
import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.clustering.RawDocumentsConsumer;
import org.carrot2.core.clustering.RawDocumentsProducer;
import org.carrot2.core.profiling.ProfiledLocalFilterComponentBase;

/**
 * A local interface to the RoughKMeans clustering algorithm. Parts copied &
 * pasted from the remote interface {@link RoughClusterer}.
 *
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class RoughKMeansLocalFilterComponent extends
    ProfiledLocalFilterComponentBase implements RawDocumentsConsumer,
    RawClustersProducer, LocalFilterComponent
{
    /**
     * Set for each clustered {@link RawDocument} instance to a {@link Double}
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
        { RawDocumentsConsumer.class, RawClustersProducer.class, RawDocumentsProducer.class }));

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object []
        { RawClustersConsumer.class }));

    /** Raw clusters consumer */
    private RawClustersConsumer rawClustersConsumer;

    /** Documents consumer. */
    private RawDocumentsConsumer rawDocumentsConsumer;

    private String query;

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
        if (rawDocumentsConsumer != null) {
            rawDocumentsConsumer.addDocument(doc);
        }

        rawDocuments.add(doc);

        stopTimer();
    }

    public void init(LocalControllerContext context)
        throws InstantiationException
    {
        super.init(context);
        rawDocuments = new ArrayList();
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
        if (next instanceof RawDocumentsConsumer) {
            this.rawDocumentsConsumer = (RawDocumentsConsumer) next;
        }
    }

    public void flushResources()
    {
        super.flushResources();

        documentReferences.clear();
        rawDocuments.clear();
        rawClustersConsumer = null;
        rawDocumentsConsumer = null;
    }

    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);
        
        String query = (String) requestContext.getRequestParameters().get(
                LocalInputComponent.PARAM_QUERY);
        if (query == null || "".equals(query.trim())) {
            query = " ";
        }
        this.query = query;
    }

    public void endProcessing() throws ProcessingException
    {
        startTimer();

        final IRContext context = new WebIRContext(query, documentReferences);

        // Parameter values taken from the remote descriptor
        // carrot2.process.trc.phrase-rough-kmeans-google
        // TODO: Add parameters as configuration options via request context.
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
        RawClusterBase junk = (RawClusterBase) rawClusters.get(rawClusters.size() - 1);
        if (junk.getClusterDescription().get(0).toString().equalsIgnoreCase("other"))
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

    public String getName()
    {
        return "RoughKMeans";
    }
}