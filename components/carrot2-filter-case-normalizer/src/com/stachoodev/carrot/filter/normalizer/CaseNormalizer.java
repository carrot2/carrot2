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
package com.stachoodev.carrot.filter.normalizer;

import java.util.*;

import org.apache.commons.collections.map.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.dawidweiss.carrot.util.common.*;
import com.dawidweiss.carrot.util.common.pools.*;
import com.dawidweiss.carrot.util.tokenizer.languages.*;
import com.dawidweiss.carrot.util.tokenizer.parser.*;

/**
 * Brings the case of all tokens in all input tokenized documents's titles and
 * snippets to one common form. This process can be thought of as 'stemming for
 * case'. A home-grown heuristic algorithm is used, which does the following:
 * 
 * <ul>
 * <li>transforms all stop words to lower case
 * <li>detects acronyms and chooses the most frequent capitalization (e.g.
 * MySQL)
 * <li>transforms all remaining non stop words to a capitalized form
 * </ul>
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
public class CaseNormalizer
{
    /**
     * Defines token types that will be skipped during the selection process.
     * These are: symbols, punctuation marks and tokens of an unknown type.
     */
    public static short DEFAULT_FILTER_MASK = TypedToken.TOKEN_TYPE_SYMBOL
        | TypedToken.TOKEN_TYPE_UNKNOWN | TypedToken.TOKEN_TYPE_PUNCTUATION;

    /**
     * Maps Locale instances (key) to Maps of tokens in the respective language.
     * Each map Maps lower case ExtendedTokens (key) to Maps of ExtendedTokens
     * with the original forms (keys) and their frequencies (values).
     */
    private Map tokensForLanguage;

    /** Stores the original documents */
    private List documents;

    /** Set to true after the normalization has been finished */
    private boolean normalizationFinished;

    /** HashMap pool (we use one hash map per token type here) */
    private SoftReusableObjectsPool hashMapPool;

    /**
     * A map of locale instances. An interesting fact: retrieving locales from
     * a HashMap instead of creating new instances _slows down_ the code by 20%
     * on the client JVM, but is faster on a server JVM (Sun/Windows)
     */
    private Map locales;

    /**
     * Creates a new case normalizer.
     */
    public CaseNormalizer()
    {
        tokensForLanguage = new HashMap();
        locales = new HashMap();
        documents = new ArrayList();

        hashMapPool = new SoftReusableObjectsPool(new ReusableObjectsFactory()
        {
            public void createNewObjects(Object [] objects)
            {
                for (int i = 0; i < objects.length; i++)
                {
                    objects[i] = new Flat3Map();
                }
            }
        }, 2500, 500);
    }

    /**
     * Clears this instance so that it can be reused with another set of
     * documents.
     */
    public void clear()
    {
        tokensForLanguage.clear();
        documents.clear();
        hashMapPool.reuse();
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

        documents.add(document);

        Locale locale = null;
        String languageCode = (String) document
            .getProperty(TokenizedDocument.PROPERTY_LANGUAGE);

        locale = (Locale) locales.get(languageCode);
        if (locale == null)
        {
            locale = new Locale((languageCode != null ? languageCode
                : "unknown"));
            locales.put(languageCode, locale);
        }

        addTokenSequence(document.getTitle(), locale);
        addTokenSequence(document.getSnippet(), locale);
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
        if (!normalizationFinished)
        {
            normalizeDocuments();
        }

        return documents;
    }

    /**
     * @return
     */
    private void normalizeDocuments()
    {
        if (normalizationFinished)
        {
            return;
        }

        // Contains maps locales (key) to normalized token maps (values)
        // Each map has tokens (from text) as keys, and their normalized
        // versions as values
        Map normalizedTokens = new HashMap();
        int variantCount = 0;
        int tokenCount = 0;
        int maxVariantCount = 0;

        // Normalize tokens first
        for (Iterator tokensIter = tokensForLanguage.keySet().iterator(); tokensIter
            .hasNext();)
        {
            Locale locale = (Locale) tokensIter.next();
            Map originalTokensForLang = (Map) tokensForLanguage.get(locale);

            for (Iterator iter = originalTokensForLang.keySet().iterator(); iter
                .hasNext();)
            {
                Token token = (Token) iter.next();

                Map normalizedTokensForLang;
                if (!normalizedTokens.containsKey(locale))
                {
                    normalizedTokensForLang = new HashMap();
                    normalizedTokens.put(locale, normalizedTokensForLang);
                }
                else
                {
                    normalizedTokensForLang = (Map) normalizedTokens
                        .get(locale);
                }

                Map originalTokens = (Map) originalTokensForLang.get(token);
                normalizeTokens((StringTypedToken) token, originalTokens,
                    locale, normalizedTokensForLang);
                tokenCount++;
                if (originalTokens.size() > maxVariantCount)
                {
                    maxVariantCount = originalTokens.size();
                }
                variantCount += originalTokens.size();
            }
        }

        for (int d = 0; d < documents.size(); d++)
        {
            TokenizedDocument document = (TokenizedDocument) documents.get(d);
            String lang = (String) document
                .getProperty(TokenizedDocument.PROPERTY_LANGUAGE);
            Map normalizedTokensForLang = (Map) normalizedTokens.get(locales
                .get(lang));

            normalizeTokenSequence((MutableTokenSequence) document.getTitle(),
                normalizedTokensForLang);
            normalizeTokenSequence(
                (MutableTokenSequence) document.getSnippet(),
                normalizedTokensForLang);
        }

        normalizationFinished = true;
    }

    /**
     * @param normalizedTokens
     */
    private void normalizeTokenSequence(MutableTokenSequence tokenSequence,
        Map normalizedTokens)
    {
        // TODO: normalizedTokens should not be null but it is for some
        // reason when clustering web content
        if (tokenSequence == null || normalizedTokens == null)
        {
            return;
        }

        for (int t = 0; t < tokenSequence.getLength(); t++)
        {
            TypedToken token = (TypedToken) tokenSequence.getTokenAt(t);

            StringTypedToken normalizedToken = (StringTypedToken) normalizedTokens
                .get(token);

            if (normalizedToken == null)
            {
                continue;
            }

            tokenSequence.setTokenAt(t, normalizedToken);
        }
    }

    /**
     * @param originalTokens
     * @return
     */
    private void normalizeTokens(StringTypedToken lowerCaseToken,
        Map originalTokens, Locale locale, Map normalizedTokens)
    {
        // If stop word, replace all variants with a lower case version
        if ((lowerCaseToken.getType() & TypedToken.TOKEN_FLAG_STOPWORD) != 0)
        {
            for (Iterator iter = originalTokens.keySet().iterator(); iter
                .hasNext();)
            {
                Object key = iter.next();
                normalizedTokens.put(key, lowerCaseToken);
            }
            return;
        }

        // Choose the most frequent variant
        int maxFreq = 0;
        StringTypedToken selectedCaseVariant = null;
        for (Iterator iter = originalTokens.keySet().iterator(); iter.hasNext();)
        {
            StringTypedToken key = (StringTypedToken) iter.next();
            if (((Integer) originalTokens.get(key)).intValue() > maxFreq)
            {
                maxFreq = ((Integer) originalTokens.get(key)).intValue();
                selectedCaseVariant = key;
            }
        }

        // If the most frequent variant is lower case or capitalized, replace it
        // with a capitalized version. Otherwise, leave the original case.
        String image = selectedCaseVariant.getImage();
        String stem = null;
        if (selectedCaseVariant instanceof StemmedToken)
        {
            stem = ((StemmedToken) selectedCaseVariant).getStem();
        }

        if (StringUtils.capitalizedRatio(image.substring(1)) == 0)
        {
            if (locale != null)
            {
                lowerCaseToken.assign(StringUtils.capitalize(image, locale),
                    lowerCaseToken.getType());
                if (stem != null
                    && lowerCaseToken instanceof MutableStemmedToken)
                {
                    ((MutableStemmedToken) lowerCaseToken).setStem(StringUtils
                        .capitalize(stem, locale));
                }
            }
            else
            {
                lowerCaseToken.assign(StringUtils.capitalize(image),
                    lowerCaseToken.getType());
                if (stem != null
                    && lowerCaseToken instanceof MutableStemmedToken)
                {
                    ((MutableStemmedToken) lowerCaseToken).setStem(StringUtils
                        .capitalize(stem));
                }
            }
        }
        else
        {
            lowerCaseToken.assign(image, lowerCaseToken.getType());
            if (stem != null && lowerCaseToken instanceof MutableStemmedToken)
            {
                ((MutableStemmedToken) lowerCaseToken).setStem(StringUtils
                    .capitalize(stem));
            }
        }

        // Replace all tokens with the normalized version
        for (Iterator iter = originalTokens.keySet().iterator(); iter.hasNext();)
        {
            Object key = iter.next();
            normalizedTokens.put(key, lowerCaseToken);
        }
        return;
    }

    /**
     * @param tokenSequence
     * @param locale
     */
    private void addTokenSequence(TokenSequence tokenSequence, Locale locale)
    {
        if (tokenSequence == null)
        {
            return;
        }

        for (int t = 0; t < tokenSequence.getLength(); t++)
        {
            TypedToken originalToken = (TypedToken) tokenSequence.getTokenAt(t);

            // Filter
            if ((originalToken.getType() & DEFAULT_FILTER_MASK) != 0)
            {
                continue;
            }

            // Create a lower case version of the token
            StringTypedToken lowerCaseToken;
            if (originalToken instanceof StemmedToken)
            {
                lowerCaseToken = new MutableStemmedToken();
            }
            else
            {
                lowerCaseToken = new StringTypedToken();
            }

            lowerCaseToken.assign(originalToken.toString().toLowerCase(locale),
                originalToken.getType());

            if (originalToken instanceof StemmedToken)
            {
                String stem = ((StemmedToken) originalToken).getStem();
                if (stem != null)
                {
                    ((MutableStemmedToken) lowerCaseToken).setStem(stem
                        .toLowerCase(locale));
                }
            }

            Map tokens = (Map) tokensForLanguage.get(locale);
            if (tokens == null)
            {
                tokens = new HashMap();
                tokensForLanguage.put(locale, tokens);
            }

            // Get the map of original forms
            Map originalTokens;
            if (tokens.containsKey(lowerCaseToken))
            {
                originalTokens = (Map) tokens.get(lowerCaseToken);
            }
            else
            {
                originalTokens = (Map) hashMapPool.acquireObject();
                originalTokens.clear();
                tokens.put(lowerCaseToken, originalTokens);
            }

            // Add/increase the frequency of the original token
            if (originalTokens.containsKey(originalToken))
            {
                originalTokens.put(originalToken,
                    new Integer(((Integer) originalTokens.get(originalToken))
                        .intValue() + 1));
            }
            else
            {
                originalTokens.put(originalToken, new Integer(1));
            }
        }
    }
}