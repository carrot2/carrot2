/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.normalizer;

import java.util.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.common.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * A simple implementation of the
 * {@link com.stachoodev.carrot.filter.normalizer.CaseNormalizer}interface. It
 * either brings all tokens to lower case, or capitalizes non-stop-words, and
 * brings stop-words to lower case.
 * 
 * All input tokens must be subclasses of
 * {@link com.dawidweiss.carrot.util.tokenizer.parser.StringTypedToken}
 * interface. The input documents will get <b>modified </b>--their tokens will
 * get overwritten with case-normalized versions. Token types will be preserved.
 * No support is provided for the full text of documents. This class is <b>not
 * </b> thread-safe.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SimpleCaseNormalizer implements CaseNormalizer
{
    /**
     * Defines token types that will be skipped during the selection process.
     * These are: symbols, punctuation marks and tokens of an unknown type.
     */
    public static short DEFAULT_FILTER_MASK = TypedToken.TOKEN_TYPE_SYMBOL
        | TypedToken.TOKEN_TYPE_UNKNOWN | TypedToken.TOKEN_TYPE_PUNCTUATION;

    /** Stores the original documents */
    private List documents;

    /** Set to true after the normalization has been finished */
    private boolean normalizationFinished;

    /** Do we capitalize non-stop-words? */
    private boolean capitalizeNonStopWords;
    
    /**
     * Creates a new case normalizer with non-stop-words capitalization.
     */
    public SimpleCaseNormalizer()
    {
        this.capitalizeNonStopWords = true;
        documents = new ArrayList();
    }

    /**
     * Creates a new case normalizer with given non-stop-words capitalization
     * property.
     * 
     * @param capitalizeNonStopWords set to true to enable capitalization of
     *            non-stop-words. Set to false to bring all tokens to lower
     *            case.
     */
    public SimpleCaseNormalizer(boolean capitalizeNonStopWords)
    {
        this();
        this.capitalizeNonStopWords = capitalizeNonStopWords;
    }

    /**
     * Clears this instance so that it can be reused with another set of
     * documents.
     */
    public void clear()
    {
        documents.clear();
        normalizationFinished = false;
    }

    /**
     * Adds a document to the normalization engine.
     * 
     * @throws IllegalStateException when an attempt is made to add documents
     *             after the {@link #getNormalizedDocuments()}has been called.
     * @param document
     */
    public void addDocument(TokenizedDocument document)
    {
        if (normalizationFinished)
        {
            throw new IllegalStateException(
                "Can't add new documents: normalization has been finished.");
        }

        normalizeTokenizedDocument(document);
        documents.add(document);
    }

    /**
     * Returns a List of case normalized documents. After a successful call to
     * this method, no documents can be added until this case normalizer is
     * cleared using the {@link #clear()}method. Note: it is in this method
     * that document's tokenks get modified.
     * 
     * @return a List of case normalized documents
     */
    public List getNormalizedDocuments()
    {
        return documents;
    }

    /**
     * @param document
     */
    private void normalizeTokenizedDocument(TokenizedDocument document)
    {
        Locale locale;
        String documentLanguage = (String) document
            .getProperty(RawDocument.PROPERTY_LANGUAGE);
        if (documentLanguage != null)
        {
            locale = new Locale(documentLanguage);
        }
        else
        {
            locale = Locale.getDefault();
        }
        normalizeTokenSequence((MutableTokenSequence) document.getTitle(),
            locale);
        normalizeTokenSequence((MutableTokenSequence) document.getSnippet(),
            locale);
    }

    /**
     * @param normalizedTokens
     */
    private void normalizeTokenSequence(MutableTokenSequence tokenSequence,
        Locale locale)
    {
        for (int t = 0; t < tokenSequence.getLength(); t++)
        {
            StringTypedToken token = (StringTypedToken) tokenSequence
                .getTokenAt(t);
            if ((token.getType() & DEFAULT_FILTER_MASK) != 0)
            {
                continue;
            }

            if (capitalizeNonStopWords)
            {
                if ((token.getType() & TypedToken.TOKEN_FLAG_STOPWORD) != 0)
                {
                    token.assign(token.getImage().toLowerCase(locale), token
                        .getType());
                }
                else
                {
                    token.assign(StringUtils.capitalize(token.getImage(),
                        locale), token.getType());
                }
            }
            else
            {
                token.assign(token.getImage().toLowerCase(locale), token
                    .getType());
            }
        }
    }
}