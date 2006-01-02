
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.filter.stc.local;

import java.io.*;
import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.core.local.profiling.*;
import com.dawidweiss.carrot.filter.stc.*;
import com.dawidweiss.carrot.filter.stc.algorithm.*;
import com.dawidweiss.carrot.filter.stc.algorithm.Cluster;
import com.dawidweiss.carrot.util.tokenizer.languages.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * A local interface to the STC clustering algorithms. Parts copied & pasted
 * from the remote interface.
 * 
 * @author Stanislaw Osinski
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
        { RawClustersProducer.class }));

    /** Raw clusters consumer */
    private RawClustersConsumer rawClustersConsumer;

    /** A map of lazily initialized tokenizers for different languages */
    private Map tokenizers;

    /** Generic tokenizer */
    private WordBasedParserBase genericTokenizer;

    /** Tokenizer buffer size */
    private static final int TOKEN_BUFFER_SIZE = 64;

    /** Stop words */
    private Set stopWords;

    /** Stems */
    private Map stems;

    /**
     *  
     */
    public STCLocalFilterComponent()
    {
    }

    /**
     * @param parameters
     */
    public STCLocalFilterComponent(Map parameters)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer#addDocument(com.dawidweiss.carrot.core.local.clustering.RawDocument)
     */
    public void addDocument(RawDocument doc) throws ProcessingException
    {
        startTimer();

        rawDocuments.add(doc);

        DocReference documentReference = new DocReference((String) doc
            .getProperty(RawDocument.PROPERTY_URL), doc.getTitle(), (doc
            .getSnippet() == null) ? java.util.Collections.EMPTY_LIST
            : Processor.splitIntoSentences(doc.getSnippet()), doc.getSnippet());
        documentReferences.add(documentReference);

        // Add to stems and stop words
        addStemsAndStopWords(doc);

        stopTimer();
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
        tokenizers = new HashMap();
        rawDocuments = new ArrayList();
        documentReferences = new ArrayList();
        stopWords = new HashSet();
        stems = new HashMap();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getRequiredPredecessorCapabilities()
     */
    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalFilterComponent#setNext(com.dawidweiss.carrot.core.local.LocalComponent)
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        rawClustersConsumer = (RawClustersConsumer) next;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        returnTokenizers();

        documentReferences.clear();
        rawDocuments.clear();
        stems.clear();
        stopWords.clear();
        rawClustersConsumer = null;
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#endProcessing()
     */
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
        stcEngine.createBaseClusters(2.0f, 1, 1.0f, 300, 2);

        stcEngine.createMergedClusters(0.6f);

        List clusters = stcEngine.getClusters();
        int max = 15;

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

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getName()
     */
    public String getName()
    {
        return "STC";
    }

    /**
     *  
     */
    private void returnTokenizers()
    {
        // Return all language tokenizers
        for (Iterator iter = tokenizers.keySet().iterator(); iter.hasNext();)
        {
            String lang = (String) iter.next();
            Language language = AllKnownLanguages.getLanguageForIsoCode(lang);
            LanguageTokenizer tokenizer = (LanguageTokenizer) tokenizers
                .get(lang);
            if (language != null)
            {
                tokenizer.reuse();
                language.returnTokenizer(tokenizer);
            }
        }
        tokenizers.clear();

        // Reuse and return the generic tokenizer
        if (genericTokenizer != null)
        {
            genericTokenizer.reuse();
            WordBasedParserFactory.Default.returnParser(genericTokenizer);
        }
    }

    /**
     * @param rawText
     * @param languageTokenizer
     * @return
     */
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

    /**
     * @param lang
     * @return
     */
    protected LanguageTokenizer getLanguageTokenizer(String lang)
    {
        if (lang == null)
        {
            // We don't need to be thread-safe here, do we?
            if (genericTokenizer == null)
            {
                genericTokenizer = WordBasedParserFactory.Default.borrowParser();
            }

            return genericTokenizer;
        }
        else
        {
            if (!tokenizers.containsKey(lang))
            {
                Language language = AllKnownLanguages
                    .getLanguageForIsoCode(lang);

                if (language == null)
                {
                    return getLanguageTokenizer(null);
                }
                else
                {
                    tokenizers.put(lang, language.borrowTokenizer());
                }
            }

            return (LanguageTokenizer) tokenizers.get(lang);
        }
    }

    /**
     * @param rawDocument
     */
    private void addStemsAndStopWords(RawDocument rawDocument)
    {
        LanguageTokenizer tokenizer = getLanguageTokenizer((String) rawDocument
            .getProperty(RawDocument.PROPERTY_LANGUAGE));

        addStemsAndStopWords(rawDocument.getTitle(), tokenizer);
        addStemsAndStopWords(rawDocument.getSnippet(), tokenizer);
    }

    /**
     * @param tokenSequence
     */
    private void addStemsAndStopWords(String rawText,
        LanguageTokenizer tokenizer)
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