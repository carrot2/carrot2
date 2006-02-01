
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

package com.dawidweiss.carrot.filter.stc.local;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.core.local.profiling.ProfiledLocalFilterComponentBase;
import com.dawidweiss.carrot.filter.stc.StcParameters;
import com.dawidweiss.carrot.filter.stc.algorithm.*;
import com.dawidweiss.carrot.filter.stc.algorithm.Cluster;

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

    /** Stop words */
    private Set stopWords;

    /** Stems */
    private Map stems;

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

        addStemsAndStopWords(doc.getTitle());
        addStemsAndStopWords(doc.getSnippet());

        final RawDocument rawDoc = (RawDocument) doc.getProperty(TokenizedDocument.PROPERTY_RAW_DOCUMENT);
        final DocReference documentReference = new DocReference(
                doc.getUrl(),
                rawDoc.getTitle(),
                (doc.getSnippet() == null)
                        ? java.util.Collections.EMPTY_LIST
                        : splitIntoSentences(doc.getSnippet()), rawDoc.getSnippet());
        documentReferences.add(documentReference);
        
        stopTimer();
    }

    public void init(LocalControllerContext context)
        throws InstantiationException
    {
        super.init(context);
        documents = new ArrayList();
        documentReferences = new ArrayList();
        stopWords = new HashSet();
        stems = new HashMap();
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
        stems.clear();
        stopWords.clear();
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
        stcEngine.stemSnippets(new ImmediateStemmer()
        {
            public String stemWord(String word)
            {
                String x = (String) stems.get(word);

                return ((x == null) ? word : x);
            }
        }, new StopWordsDetector()
        {
            public boolean isStopWord(String word)
            {
                return stopWords.contains(word);
            }
        });

        stcEngine.createSuffixTree();

        final StcParameters params = StcParameters.fromMap(
                this.requestContext.getRequestParameters());

        stcEngine.createBaseClusters(
                params.getMinBaseClusterScore(),
                params.getIgnoreWordIfInFewerDocs(),
                params.getIgnoreWordIfInHigherDocsPercent(),
                params.getMaxBaseClusters(),
                params.getMinBaseClusterSize());

        stcEngine.createMergedClusters(params.getMergeThreshold());

        final List clusters = stcEngine.getClusters();
        int max = params.getMaxClusters();

        // Convert STC's clusters to the local interfaces' format
        List rawClusters = new ArrayList();
        for (Iterator i = clusters.iterator(); i.hasNext() && (max > 0); max--)
        {
            Cluster b = (Cluster) i.next();
            RawClusterBase rawCluster = new RawClusterBase();

            List phrases = b.getPhrases();
            int maxPhr = 3;

            for (Iterator j = phrases.iterator(); j.hasNext() && (maxPhr > 0); maxPhr--)
            {
                BaseCluster.Phrase p = (BaseCluster.Phrase) j.next();
                rawCluster.addLabel(p.userFriendlyTerms().trim());
            }

            for (Iterator j = b.documents.iterator(); j.hasNext();)
            {
                int docIndex = ((Integer) j.next()).intValue();
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

    /**
     * Adds stems and stop words of <code>rawText</code> to internal data structures.
     */
    private TokenSequence addStemsAndStopWords(TokenSequence tokenSequence)
    {
        final int maxTokenIndex = tokenSequence.getLength();
        for (int t = 0; t < maxTokenIndex; t++)
        {
            final TypedToken token = (TypedToken) tokenSequence.getTokenAt(t);
            if ((token.getType() & TypedToken.TOKEN_FLAG_STOPWORD) != 0)
            {
                stopWords.add(token.getImage());
            }

            if (token instanceof StemmedToken)
            {
                final String stem = ((StemmedToken) token).getStem();
                if (stem != null)
                {
                    stems.put(token.getImage(), stem);
                }
            }
        }
        
        return tokenSequence;
    }

    /**
     * Splits a token sequence into a {@link List} of {@link List}s (sentences) of
     * {@link String} objects (words). 
     */
    private static List splitIntoSentences(final TokenSequence tokens) {
        final ArrayList sentences = new ArrayList(10);
        final ArrayList currentSentence = new ArrayList();
        currentSentence.ensureCapacity(10);

        final int maxTokenIndex = tokens.getLength();
        for (int i = 0; i < maxTokenIndex; i++) {
            final TypedToken token = (TypedToken) tokens.getTokenAt(i);
            final short tokenType = token.getType();
            if ((tokenType & TypedToken.TOKEN_FLAG_SENTENCE_DELIM) != 0) {
                if (currentSentence.size() > 0) {
                    sentences.add(new ArrayList(currentSentence));
                    currentSentence.clear();
                }
            } else {
                // Skip punctuation?
                // if ((tokenType & TypedToken.TOKEN_TYPE_PUNCTUATION) == 0)
                currentSentence.add(token.getImage());
            }
        }
        return sentences;
    }    
}