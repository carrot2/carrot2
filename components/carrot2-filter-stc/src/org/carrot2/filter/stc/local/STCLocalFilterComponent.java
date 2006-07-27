
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

package org.carrot2.filter.stc.local;

import java.util.*;

import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.profiling.ProfiledLocalFilterComponentBase;
import org.carrot2.filter.stc.StcParameters;
import org.carrot2.filter.stc.algorithm.*;
import org.carrot2.filter.stc.algorithm.MergedCluster;

/**
 * A local interface to the STC clustering algorithms. Parts copied & pasted
 * from the remote interface.
 * 
 * @author Stanislaw Osinski
 * @author Dawid Weiss
 * @version $Revision$
 */
public class STCLocalFilterComponent extends ProfiledLocalFilterComponentBase
    implements TokenizedDocumentsConsumer, RawClustersProducer, LocalFilterComponent
{
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
     * Implements {@link TokenizedDocumentsConsumer#addDocument(TokenizedDocument)}.
     */
    public void addDocument(TokenizedDocument doc) throws ProcessingException
    {
        startTimer();

        documents.add(doc);

        final DocReference documentReference = new DocReference(doc);
        documentReferences.add(documentReference);

        stopTimer();
    }

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

        final STCEngine stcEngine = new STCEngine(documentReferences);
        stcEngine.createSuffixTree();

        final StcParameters params = StcParameters.fromMap(
                this.requestContext.getRequestParameters());

        stcEngine.createBaseClusters(params);
        stcEngine.createMergedClusters(params);

        final List clusters = stcEngine.getClusters();
        int max = params.getMaxClusters();

        // Convert STC's clusters to the format required by local interfaces.
        final List rawClusters = new ArrayList();
        for (Iterator i = clusters.iterator(); i.hasNext() && (max > 0); max--)
        {
            final MergedCluster b = (MergedCluster) i.next();
            final RawClusterBase rawCluster = new RawClusterBase();

            int maxPhr = 3; // TODO: This should be a configuration parameter moved to STCEngine perhaps.
            final List phrases = b.getDescriptionPhrases();
            for (Iterator j = phrases.iterator(); j.hasNext() && (maxPhr > 0); maxPhr--)
            {
                Phrase p = (Phrase) j.next();
                rawCluster.addLabel(p.userFriendlyTerms().trim());
            }

            for (Iterator j = b.getDocuments().iterator(); j.hasNext();)
            {
                final int docIndex = ((Integer) j.next()).intValue();
                final TokenizedDocument tokenizedDoc = (TokenizedDocument) documents.get(docIndex);
                final RawDocument rawDoc = (RawDocument) tokenizedDoc.getProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT);
                rawCluster.addDocument(rawDoc);
            }

            rawClusters.add(rawCluster);
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
        return "STC";
    }
}