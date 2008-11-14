
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

package org.carrot2.filter.normalizer;

import java.util.*;

import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.clustering.TokenizedDocument;
import org.carrot2.core.linguistic.tokens.*;
import org.carrot2.util.StringUtils;
import org.carrot2.util.tokenizer.languages.MutableStemmedToken;
import org.carrot2.util.tokenizer.parser.StringTypedToken;

/**
 * A simple implementation of the
 * {@link org.carrot2.filter.normalizer.CaseNormalizer}interface. It
 * either brings all tokens to lower case, or capitalizes non-stop-words, and
 * brings stop-words to lower case.
 * 
 * All input tokens must be subclasses of
 * {@link org.carrot2.util.tokenizer.parser.StringTypedToken}
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

            String image = token.getImage();
            String stem = null;
            if (token instanceof StemmedToken)
            {
                stem = ((StemmedToken) token).getStem();
            }

            if (capitalizeNonStopWords)
            {
                if ((token.getType() & TypedToken.TOKEN_FLAG_STOPWORD) != 0)
                {
                    token.assign(image.toLowerCase(locale), token.getType());
                }
                else
                {
                    token.assign(StringUtils.capitalize(image, locale), token
                        .getType());
                }
            }
            else
            {
                token.assign(image.toLowerCase(locale), token.getType());
            }

            if (stem != null && token instanceof MutableStemmedToken)
            {
                ((MutableStemmedToken) token).setStem(stem);
            }
        }
    }
}