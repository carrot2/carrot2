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
package com.dawidweiss.carrot.util.tokenizer;

import java.io.*;
import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.core.local.profiling.*;
import com.dawidweiss.carrot.util.tokenizer.languages.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * Note: there is no support for tokenizing document content yet.
 * 
 * TODO: refactor SnippetTokenizer and delegate calls from this filter to it
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SnippetTokenizerLocalFilterComponent extends
    ProfiledLocalFilterComponentBase implements RawDocumentsConsumer,
    TokenizedDocumentsProducer, LocalFilterComponent
{
    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsProducer.class }));

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        { RawDocumentsConsumer.class, TokenizedDocumentsProducer.class }));

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object []
        { TokenizedDocumentsConsumer.class }));

    /** Tokenized documents consumer */
    private TokenizedDocumentsConsumer tokenizedDocumentsConsumer;

    /** A map of lazily initialized tokenizers for different languages */
    private Map tokenizers;

    /** Generic tokenizer */
    private WordBasedParser genericTokenizer;

    /** Tokenizer buffer size */
    private static final int TOKEN_BUFFER_SIZE = 64;

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
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.clustering.RawDocumentsConsumer#addDocument(com.dawidweiss.carrot.core.local.clustering.RawDocument)
     */
    public void addDocument(RawDocument doc) throws ProcessingException
    {
        startTimer();
        TokenizedDocument tokenizedDocument = tokenize(doc);
        stopTimer();

        tokenizedDocumentsConsumer.addDocument(tokenizedDocument);
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
        tokenizedDocumentsConsumer = (TokenizedDocumentsConsumer) next;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        tokenizedDocumentsConsumer = null;
        profile = null;

        returnTokenizers();
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
            WordBasedParserFactory.returnParser(genericTokenizer);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.dawidweiss.carrot.core.local.LocalComponent#getName()
     */
    public String getName()
    {
        return "Tokenizer";
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
                genericTokenizer = WordBasedParserFactory.borrowParser();
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
     * Tokenizes a single {@link RawDocument}into a
     * {@link TokenizedDocumentSnippet}.
     * 
     * @param rawDocument
     * @return
     */
    protected TokenizedDocument tokenize(RawDocument rawDocument)
    {
        // Get tokenizer
        LanguageTokenizer languageTokenizer = getLanguageTokenizer((String) rawDocument
            .getProperty(RawDocument.PROPERTY_LANGUAGE));

        // Tokenize
        TokenSequence titleTokenSequence = tokenize(rawDocument.getTitle(),
            languageTokenizer);
        TokenSequence snippetTokenSequence = tokenize(rawDocument.getSnippet(),
            languageTokenizer);

        TokenizedDocumentSnippet tokenizedDocumentSnippet = new TokenizedDocumentSnippet(
            rawDocument.getId(), titleTokenSequence, snippetTokenSequence,
            rawDocument.getUrl(), rawDocument.getScore());

        // Set reference to the original raw document
        tokenizedDocumentSnippet.setProperty(
            TokenizedDocument.PROPERTY_RAW_DOCUMENT, rawDocument);

        // Set some properties (all should be copied!)
        tokenizedDocumentSnippet.setProperty(TokenizedDocument.PROPERTY_URL,
            rawDocument.getProperty(RawDocument.PROPERTY_URL));
        tokenizedDocumentSnippet.setProperty(
            TokenizedDocument.PROPERTY_LANGUAGE, rawDocument
                .getProperty(RawDocument.PROPERTY_LANGUAGE));

        return tokenizedDocumentSnippet;
    }

    /**
     * @param rawText
     * @param languageTokenizer
     * @return
     */
    protected TokenSequence tokenize(String rawText,
        LanguageTokenizer languageTokenizer)
    {
        if (rawText == null)
        {
            return new MutableTokenSequence();
        }

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
}