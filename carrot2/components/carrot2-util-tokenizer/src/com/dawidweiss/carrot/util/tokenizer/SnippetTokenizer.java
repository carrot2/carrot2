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

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.tokenizer.languages.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * A utility class for tokenizing document snippets. Note: no support is
 * provided for tokenizing document content as yet. TODO: all document's
 * properties should be copied (not just the ones that are defined in
 * RawDocument)
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SnippetTokenizer
{
    /** Token buffer size */
    private static final int TOKEN_BUFFER_SIZE = 64;

    /**
     * Tokenizes a list of {@link RawDocument}s into a list of
     * {@link TokenizedDocumentSnippet}s.
     * 
     * @param rawDocuments
     * @return
     */
    public List tokenize(List rawDocuments)
    {
        // TODO: does it make sense to optimize this like this:
        // 1) create a copy of the document list
        // 2) sort the list according the the language
        // 3) tokenize each part of the list using the same instance of
        //    language tokenizer
        List tokenizedDocuments = new ArrayList();
        for (Iterator documents = rawDocuments.iterator(); documents.hasNext();)
        {
            RawDocument rawDocument = (RawDocument) documents.next();
            tokenizedDocuments.add(tokenize(rawDocument));
        }

        return tokenizedDocuments;
    }

    /**
     * Tokenizes a single {@link RawDocument}into a
     * {@link TokenizedDocumentSnippet}.
     * 
     * @param rawDocument
     * @return
     */
    public TokenizedDocument tokenize(RawDocument rawDocument)
    {
        // Borrow tokenizer
        LanguageTokenizer languageTokenizer = borrowLanguageTokenzer((String) rawDocument
            .getProperty(RawDocument.PROPERTY_LANGUAGE));

        // Tokenize
        TokenSequence titleTokenSequence = tokenize(rawDocument.getTitle(),
            languageTokenizer);
        TokenSequence snippetTokenSequence = tokenize(rawDocument.getSnippet(),
            languageTokenizer);

        // Return tokenizer
        returnLanguageTokenizer((String) rawDocument
            .getProperty(RawDocument.PROPERTY_LANGUAGE), languageTokenizer);

        TokenizedDocumentSnippet tokenizedDocumentSnippet = new TokenizedDocumentSnippet(
            titleTokenSequence, snippetTokenSequence);

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
     * Tokenizes a single {@link RawDocument}into a
     * {@link TokenizedDocumentSnippet}.
     * 
     * @param rawDocument
     * @return
     */
    public TokenizedDocument tokenize(RawDocument rawDocument,
        LanguageTokenizer languageTokenizer)
    {
        // Tokenize
        TokenSequence titleTokenSequence = tokenize(rawDocument.getTitle(),
            languageTokenizer);
        TokenSequence snippetTokenSequence = tokenize(rawDocument.getSnippet(),
            languageTokenizer);

        TokenizedDocumentSnippet tokenizedDocumentSnippet = new TokenizedDocumentSnippet(
            titleTokenSequence, snippetTokenSequence);

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
     * Tokenizes raw text into a {@link MutableTokenSequence}. If
     * <code>lang</code> is not null, an attempt will be made to find and use
     * a dedicated tokenizer.
     * 
     * @param rawText
     * @param lang
     * @return
     */
    public TokenSequence tokenize(String rawText, String lang)
    {
        LanguageTokenizer languageTokenizer = borrowLanguageTokenzer(lang);
        TokenSequence tokenSequence = tokenize(rawText, languageTokenizer);
        returnLanguageTokenizer(lang, languageTokenizer);

        return tokenSequence;
    }

    /**
     * Helps to avoid tokenizer borrow/return thrashing.
     * 
     * @param rawText
     * @param languageTokenizer
     * @return
     */
    public TokenSequence tokenize(String rawText,
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
     * @param isoCode can be <code>null</code>
     * @return
     */
    private LanguageTokenizer borrowLanguageTokenzer(String isoCode)
    {
        if (isoCode == null)
        {
            // Default to a generic tokenizer
            return WordBasedParserFactory.borrowParser();
        }
        else
        {
            Language language = AllKnownLanguages
                .getLanguageForIsoCode(isoCode);
            if (language == null)
            {
                // Default to a generic tokenizer
                return WordBasedParserFactory.borrowParser();
            }
            else
            {
                // Borrow a specific tokenizer
                return language.borrowTokenizer();
            }
        }
    }

    /**
     * @param isoCode
     * @param languageTokenizer
     */
    private void returnLanguageTokenizer(String isoCode,
        LanguageTokenizer languageTokenizer)
    {
        if (isoCode == null)
        {
            // Defaulted to a generic tokenizer
            WordBasedParserFactory
                .returnParser((WordBasedParser) languageTokenizer);
        }
        else
        {
            Language language = AllKnownLanguages
                .getLanguageForIsoCode(isoCode);
            if (language == null)
            {
                // Defaulted to a generic tokenizer
                WordBasedParserFactory
                    .returnParser((WordBasedParser) languageTokenizer);
            }
            else
            {
                // Borrowed a specific tokenizer
                language.returnTokenizer(languageTokenizer);
            }
        }
    }
}