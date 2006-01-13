
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

import java.io.StringReader;
import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.Language;
import com.dawidweiss.carrot.core.local.linguistic.LanguageTokenizer;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.core.local.profiling.ProfiledLocalFilterComponentBase;
import com.dawidweiss.carrot.filter.stc.Processor;
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
    implements RawDocumentsConsumer, RawClustersProducer, LocalFilterComponent
{
    /** Documents to be clustered */
    private List rawDocuments;

    /** STC's document references */
    private List documentReferences;

    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsProducer.class }));

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsConsumer.class, RawClustersProducer.class }));

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object []
        { RawClustersConsumer.class }));

    /** Raw clusters consumer */
    private RawClustersConsumer rawClustersConsumer;

    /** Tokenizer buffer size */
    private static final int TOKEN_BUFFER_SIZE = 64;

    /** Stop words */
    private Set stopWords;

    /** Stems */
    private Map stems;

    /**
     * The default language (used to acquire a tokenizer instance).
     */
    private Language defaultLanguage;

    /**
     * The current process language (either default or overriden).
     */
    private Language language;

    /**
     * The current tokenizer.
     */
    private LanguageTokenizer tokenizer;

    /**
     * Current request's context.
     */
    private RequestContext requestContext;

    /**
     * Create the component configured to process documents written
     * in the given language.
     */
    public STCLocalFilterComponent(Language defaultLanguage) {
        this.defaultLanguage = defaultLanguage; 
    }
    
    public void addDocument(RawDocument doc) throws ProcessingException
    {
        startTimer();

        rawDocuments.add(doc);

        DocReference documentReference = new DocReference(
                (String) doc.getProperty(RawDocument.PROPERTY_URL),
                doc.getTitle(),
                (doc.getSnippet() == null)
                        ? java.util.Collections.EMPTY_LIST
                        : Processor.splitIntoSentences(doc.getSnippet()), doc.getSnippet());
        documentReferences.add(documentReference);

        addStemsAndStopWords(doc.getTitle(), tokenizer);
        addStemsAndStopWords(doc.getSnippet(), tokenizer);
        
        stopTimer();
    }

    public void init(LocalControllerContext context)
        throws InstantiationException
    {
        super.init(context);
        rawDocuments = new ArrayList();
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

        language.returnTokenizer(tokenizer);
        language = null;
        tokenizer = null;

        documentReferences.clear();
        rawDocuments.clear();
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
        
        this.language = defaultLanguage;
        this.tokenizer = language.borrowTokenizer();
    }

    public void endProcessing() throws ProcessingException
    {
        startTimer();

        STCEngine stcEngine = new STCEngine(documentReferences);
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
                rawCluster
                    .addDocument((RawDocument) rawDocuments.get(docIndex));
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

    protected TokenSequence tokenize(String rawText,
        LanguageTokenizer languageTokenizer)
    {
        int tokenCount = 0;
        com.dawidweiss.carrot.core.local.linguistic.tokens.Token [] tokens = new com.dawidweiss.carrot.core.local.linguistic.tokens.Token [TOKEN_BUFFER_SIZE];
        MutableTokenSequence tokenSequence = new MutableTokenSequence();

        // Build the TokenSequence
        languageTokenizer.restartTokenizationOn(new StringReader(rawText));
        while ((tokenCount = languageTokenizer.getNextTokens(tokens, 0)) != 0)
        {
            for (int t = 0; t < tokenCount; t++)
            {
                tokenSequence.addToken(tokens[t]);
            }
        }

        return tokenSequence;
    }

    private void addStemsAndStopWords(String rawText, LanguageTokenizer tokenizer)
    {
        if (rawText == null)
        {
            return;
        }

        TokenSequence tokenSequence = tokenize(rawText, tokenizer);
        for (int t = 0; t < tokenSequence.getLength(); t++)
        {
            TypedToken token = (TypedToken) tokenSequence.getTokenAt(t);
            if ((token.getType() & TypedToken.TOKEN_FLAG_STOPWORD) != 0)
            {
                stopWords.add(token.getImage());
            }

            if (token instanceof StemmedToken)
            {
                String stem = ((StemmedToken) token).getStem();
                if (stem != null)
                {
                    stems.put(token.getImage(), stem);
                }
            }
        }
    }
}