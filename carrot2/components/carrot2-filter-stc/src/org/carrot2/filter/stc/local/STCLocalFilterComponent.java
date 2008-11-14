
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

package org.carrot2.filter.stc.local;

import java.io.StringReader;
import java.util.*;

import org.apache.log4j.Logger;
import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.linguistic.tokens.*;
import org.carrot2.core.profiling.ProfiledLocalFilterComponentBase;
import org.carrot2.filter.stc.StcParameters;
import org.carrot2.filter.stc.algorithm.*;
import org.carrot2.util.tokenizer.languages.MutableStemmedToken;
import org.carrot2.util.tokenizer.parser.WordBasedParserBase;
import org.carrot2.util.tokenizer.parser.jflex.JFlexWordBasedParser;

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
     * Query words for the current query.
     */
    final HashSet queryWords = new HashSet();

    /**
     * Implements {@link TokenizedDocumentsConsumer#addDocument(TokenizedDocument)}.
     */
    public void addDocument(TokenizedDocument doc) throws ProcessingException
    {
        startTimer();

        documents.add(doc);
        
        // TODO: This is a hack, we rewrite MutableTokenSequence inside the parsed 
        // TokenizedDocument to mark stopwords. This should be done perhaps at the tokenizer
        // level (marking query words inside the content stream with a separate token flag?).
        markQueryWords(doc.getTitle());
        markQueryWords(doc.getSnippet());
        
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

        queryWords.clear();
        final String query = 
            (String) requestContext.getRequestParameters().get(LocalInputComponent.PARAM_QUERY);
        if (query != null)
        {
            splitQueryWords(query);
        }
    }

    /**
     * Re-mark query words as stopwords inside the token sequence. 
     */
    private void markQueryWords(TokenSequence seq)
    {
        try
        {
            for (int i = seq.getLength() - 1; i >= 0; i--)
            {
                final MutableStemmedToken t = (MutableStemmedToken) seq.getTokenAt(i);
                if (this.queryWords.contains(t.getImage().toLowerCase()))
                {
                    t.setType((short) (t.getType() | TypedToken.TOKEN_FLAG_STOPWORD));
                }
            }
        } 
        catch (ClassCastException e)
        {
            Logger.getLogger(this.getClass().getName()).warn("MutableStemmedTokens expected.", e);
        }
    }
    
    /**
     * Splits <code>query</code> into words and places them into {@link #queryWords}.
     */
    private void splitQueryWords(String query)
    {
        final WordBasedParserBase parser = new JFlexWordBasedParser();
        parser.restartTokenizationOn(new StringReader(query));

        org.carrot2.core.linguistic.tokens.Token [] queryTokens 
            = new org.carrot2.core.linguistic.tokens.Token[5];
        int howMany;
        while ((howMany = parser.getNextTokens(queryTokens, 0)) > 0)
        {
            for (int i = 0; i < howMany; i++)
            {
                queryWords.add(queryTokens[i].getImage().toLowerCase());
            }
        }
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

        final HashSet junkDocuments = new HashSet(documentReferences.size());
        junkDocuments.addAll(documents);

        // Convert STC's clusters to the format required by local interfaces.
        final List rawClusters = new ArrayList();
        for (Iterator i = clusters.iterator(); i.hasNext() && (max > 0); max--)
        {
            final MergedCluster b = (MergedCluster) i.next();
            final RawClusterBase rawCluster = new RawClusterBase();

            // TODO: This should be a configuration parameter moved to STCEngine perhaps.
            int maxPhr = 3;
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
                junkDocuments.remove(tokenizedDoc);
            }
            
            rawClusters.add(rawCluster);
        }

        // Create the 'junk' cluster.
        if (junkDocuments.size() > 0)
        {
            final RawClusterBase junkCluster = new RawClusterBase();
            junkCluster.setProperty(RawCluster.PROPERTY_JUNK_CLUSTER, Boolean.TRUE);
            junkCluster.setScore(0.0d);
            junkCluster.addLabel("(Other)");

            for (final Iterator i = junkDocuments.iterator(); i.hasNext();)
            {
                final TokenizedDocument tokenizedDoc = (TokenizedDocument) i.next();
                final RawDocument rawDoc = (RawDocument) tokenizedDoc.getProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT);
                junkCluster.addDocument(rawDoc);
            }
            
            rawClusters.add(junkCluster);
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