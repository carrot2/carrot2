
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
 * provided for tokenizing document content as yet. This class is not
 * thread-safe.
 * 
 * TODO: all document's properties should be copied (not just the ones that are
 * defined in RawDocument)
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SnippetTokenizer
{
    /** */
    private Map tokenizers;

    /** */
    private LanguageTokenizer genericTokenizer;

    /** */
    private static com.dawidweiss.carrot.core.local.linguistic.tokens.Token [] tokens;

    /** */
    private static StringBuffer stringBuffer;

    /** Token buffer size */
    private static final int TOKEN_BUFFER_SIZE = 256;

    /**
     *  
     */
    public SnippetTokenizer()
    {
        tokenizers = new HashMap();
        stringBuffer = new StringBuffer(512);
        tokens = new com.dawidweiss.carrot.core.local.linguistic.tokens.Token [TOKEN_BUFFER_SIZE];
    }

    /**
     *  
     */
    public void clear()
    {
        returnTokenizers();
    }

    /**
     * Tokenizes a list of {@link RawDocument}s into a list of
     * {@link TokenizedDocumentSnippet}s.
     * 
     * @param rawDocuments
     */
    public List tokenize(List rawDocuments)
    {
        List tokenizedDocuments = new ArrayList();
        for (Iterator documents = rawDocuments.iterator(); documents.hasNext();)
        {
            RawDocument rawDocument = (RawDocument) documents.next();
            tokenizedDocuments.add(tokenize(rawDocument));
        }

        return tokenizedDocuments;
    }

    /**
     * Internal version helps to avoid continuous re-allocation of string and
     * token buffers.
     * 
     * @param rawDocument
     */
    public TokenizedDocument tokenize(RawDocument rawDocument)
    {
        // Borrow tokenizer
        LanguageTokenizer languageTokenizer = getLanguageTokenizer((String) rawDocument
            .getProperty(RawDocument.PROPERTY_LANGUAGE));

        return tokenize(rawDocument, languageTokenizer, -1);
    }

    /**
     * Internal version helps to avoid continuous re-allocation of string and
     * token buffers.
     * 
     * @param rawDocument
     */
    public TokenizedDocument tokenize(RawDocument rawDocument, int maxLength)
    {
        // Borrow tokenizer
        LanguageTokenizer languageTokenizer = getLanguageTokenizer((String) rawDocument
            .getProperty(RawDocument.PROPERTY_LANGUAGE));
        
        return tokenize(rawDocument, languageTokenizer, maxLength);
    }
    
    /**
     * Tokenizes a single {@link RawDocument}into a
     * {@link TokenizedDocumentSnippet}.
     * 
     * @param rawDocument
     */
    public TokenizedDocument tokenizeOnePass(RawDocument rawDocument,
        LanguageTokenizer languageTokenizer)
    {
        // Tokenize
        stringBuffer.delete(0, stringBuffer.length());
        String title = (rawDocument.getTitle() != null ? rawDocument.getTitle() : "");
        stringBuffer.append(title);
        stringBuffer.append(" 4a7z2f6q3 ");
        String snippet = (rawDocument.getSnippet() != null ? rawDocument.getSnippet() : "");
        stringBuffer.append(snippet);

        int tokenCount = 0;
        languageTokenizer.restartTokenizationOn(new StringReader(stringBuffer
            .toString()));

        // Build the tokenized documents
        MutableTokenSequence titleTokenSequence = new MutableTokenSequence();
        MutableTokenSequence snippetTokenSequence = null;
        while ((tokenCount = languageTokenizer.getNextTokens(tokens, 0)) != 0)
        {
            for (int t = 0; t < tokenCount; t++)
            {
                if (tokens[t].toString().equals("4a7z2f6q3"))
                {
                    if (snippetTokenSequence == null)
                    {
                        snippetTokenSequence = new MutableTokenSequence();
                    }
                    continue;
                }

                if (snippetTokenSequence == null)
                {
                    titleTokenSequence.addToken(tokens[t]);
                }
                else
                {
                    snippetTokenSequence.addToken(tokens[t]);
                }
            }
        }

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
     */
    public TokenizedDocument tokenize(RawDocument rawDocument,
        LanguageTokenizer languageTokenizer)
    {
        return tokenize(rawDocument, languageTokenizer, -1);
    }
    
    /**
     * Tokenizes a single {@link RawDocument}into a
     * {@link TokenizedDocumentSnippet}.
     * 
     * @param rawDocument
     */
    public TokenizedDocument tokenize(RawDocument rawDocument,
        LanguageTokenizer languageTokenizer, int maxLength)
    {
        TokenSequence titleTokenSequence = tokenize(rawDocument.getTitle(), languageTokenizer, maxLength);
        TokenSequence snippetTokenSequence = tokenize(rawDocument.getSnippet(), languageTokenizer, maxLength);
        
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
     * Helps to avoid tokenizer borrow/return thrashing.
     * 
     * @param rawText
     * @param languageTokenizer
     */
    public static TokenSequence tokenize(String rawText,
        LanguageTokenizer languageTokenizer)
    {
        return tokenize(rawText, languageTokenizer, -1);
    }
    
    /**
     * Helps to avoid tokenizer borrow/return thrashing.
     * 
     * @param rawText
     * @param languageTokenizer
     */
    public static TokenSequence tokenize(String rawText,
        LanguageTokenizer languageTokenizer, int maxLength)
    {
        if (rawText == null || rawText.length() == 0)
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
                if (maxLength != -1)
                {
                    if (--maxLength == 0)
                    {
                        return tokenSequence;
                    }
                }
            }
        }

        return tokenSequence;
    }

    protected LanguageTokenizer getLanguageTokenizer(String lang)
    {
        if (lang == null)
        {
            // TODO: We don't need to be thread-safe here, do we?
            if (genericTokenizer == null)
            {
                genericTokenizer = WordBasedParserFactory.Default
                    .borrowParser();
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
            WordBasedParserFactory.Default
                .returnParser((WordBasedParserBase) genericTokenizer);
        }
    }
}