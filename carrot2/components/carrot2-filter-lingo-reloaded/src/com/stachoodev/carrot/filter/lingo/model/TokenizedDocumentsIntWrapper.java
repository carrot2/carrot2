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
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import org.apache.commons.collections.*;
import org.apache.commons.collections.bidimap.*;
import org.apache.commons.collections.primitives.*;

import cern.colt.list.*;
import cern.colt.map.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.stachoodev.suffixarrays.wrapper.*;

/**
 * Documents must be sequences of
 * {@link com.dawidweiss.carrot.core.local.linguistic.tokens.TypedToken}s.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class TokenizedDocumentsIntWrapper extends IntWrapperBase implements
    TypeAwareIntWrapper, MaskableIntWrapper
{
    /** A bidirectional map of tokens (keys) and their integer codes (values) */
    private BidiMap tokens;

    /** A map of token stems (keys) and their integer base values */
    private Map stems;

    /**
     * A map for quick looking up of token stems (values) for given integer
     * codes (keys)
     */
    private Map tokenStems;

    /** Query words */
    private String [] queryWords;

    /** Integer codes of query words */
    private OpenIntIntHashMap queryWordCodes;

    /** Current maximum integer code for a token */
    private int maxTokenCode;

    /** Current maximum integer code for a sentence delimiter */
    private int maxSentenceDelimiterCode;

    /** Token types to filter out */
    private static final short TOKEN_FILTER_MASK = TypedToken.TOKEN_TYPE_SYMBOL
        | TypedToken.TOKEN_TYPE_UNKNOWN | TypedToken.TOKEN_TYPE_PUNCTUATION;

    /** Segment codes */
    public static final short SEGMENT_TITLE = 0;
    public static final short SEGMENT_SNIPPET = 1;
    public static final short SEGMENT_DOCUMENT_DELIMITER = 2;
    public static final short SEGMENT_TERMINATOR = 3;

    /**
     * Stores the association between document segments (title, snippet) and
     * position in the internal int codes array
     */
    private ShortList segments;

    /** */
    private IntList documentIndices;

    /**
     * @param tokenizedDocuments
     */
    public TokenizedDocumentsIntWrapper(List tokenizedDocuments)
    {
        this(tokenizedDocuments, null);
    }

    /**
     * @param tokenizedDocuments
     */
    public TokenizedDocumentsIntWrapper(List tokenizedDocuments,
        String [] queryWords)
    {
        this.queryWords = queryWords;
        if (queryWords != null)
        {
            queryWordCodes = new OpenIntIntHashMap(queryWords.length);
        }

        maxTokenCode = -MaskableIntWrapper.SECONDARY_OFFSET;
        maxSentenceDelimiterCode = -1;

        tokens = new DualHashBidiMap();
        stems = new HashMap();
        tokenStems = new HashMap();
        segments = new ArrayShortList();
        documentIndices = new ArrayIntList();

        createIntData(tokenizedDocuments);
    }

    /**
     * @param intData
     */
    private TokenizedDocumentsIntWrapper(int [] intData, BidiMap tokens,
        Map tokenStems)
    {
        this.intData = intData;
        this.tokens = tokens;
        this.tokenStems = tokenStems;
    }

    /**
     * Returns the token associated with given code or <code>null</code> when
     * there is no token for given code.
     * 
     * @param code
     * @return
     */
    public final TypedToken getTokenForCode(int code)
    {
        return (TypedToken) tokens.getKey(new Integer(code));
    }

    /**
     * Returns a token stem associated with given code or <code>null</code>
     * when there is no token stem for given code.
     * 
     * @param code
     * @return
     */
    public TokenStem getTokenStemForCode(int code)
    {
        Integer maskedCode = new Integer(code
            & MaskableIntWrapper.SECONDARY_MASK);
        TokenStem tokenStem;

        if (tokenStems.containsValue(maskedCode))
        {
            tokenStem = (TokenStem) tokenStems.get(maskedCode);
        }
        else
        {
            tokenStem = new TokenStem(getTokenForCode(code));
            tokenStems.put(maskedCode, tokenStem);
        }

        return tokenStem;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.stachoodev.suffixarrays.wrapper.TypeAwareIntWrapper#isStopWord(int)
     */
    public final boolean isStopWord(int code)
    {
        return (getTokenForCode(code).getType() & TypedToken.TOKEN_FLAG_STOPWORD) != 0;
    }

    /**
     * @param code
     * @return
     */
    public final boolean isIndexableToken(int code)
    {
        return code >= 0;
    }

    /**
     * @param code
     * @return
     */
    public boolean isQueryWord(int code)
    {
        if (queryWordCodes != null)
        {
            return queryWordCodes.containsKey(code
                & MaskableIntWrapper.SECONDARY_MASK);
        }
        else
        {
            return false;
        }
    }

    /**
     * @return
     */
    public IntArrayList getQueryWordCodes()
    {
        if (queryWordCodes != null)
        {
            return queryWordCodes.keys();
        }
        else
        {
            return null;
        }
    }
    
    /**
     * @param position
     * @return
     */
    public final short getSegmentForPosition(int position)
    {
        return segments.get(position);
    }

    /**
     * @param position
     * @return
     */
    public final int getDocumentIndexForPosition(int position)
    {
        return documentIndices.get(position);
    }
    
    /**
     * @param tokenizedDocuments
     */
    protected void createIntData(List tokenizedDocuments)
    {
        // TODO: replace with Colt's int list?
        IntList intDataList = new ArrayIntList();

        int documentIndex = 0;
        for (Iterator documents = tokenizedDocuments.iterator(); documents
            .hasNext();)
        {
            TokenizedDocument document = (TokenizedDocument) documents.next();

            TokenSequence tokenSequence = document.getTitle();
            addToDataList(intDataList, tokenSequence, SEGMENT_TITLE,
                documentIndex);

            // Document delimiter - prevents phrases from crossing document
            // boundaries
            if (tokenSequence.getLength() > 0)
            {
                maxSentenceDelimiterCode -= MaskableIntWrapper.SECONDARY_OFFSET;
                intDataList.add(maxSentenceDelimiterCode);
                segments.add(SEGMENT_DOCUMENT_DELIMITER);
                documentIndices.add(documentIndex);
            }

            tokenSequence = document.getSnippet();
            addToDataList(intDataList, tokenSequence, SEGMENT_SNIPPET,
                documentIndex);

            // Document delimiter - prevents phrases from crossing document
            // boundaries
            if (tokenSequence.getLength() > 0)
            {
                maxSentenceDelimiterCode -= MaskableIntWrapper.SECONDARY_OFFSET;
                intDataList.add(maxSentenceDelimiterCode);
                segments.add(SEGMENT_DOCUMENT_DELIMITER);
                documentIndices.add(documentIndex);
            }

            documentIndex++;
        }

        segments.add(SEGMENT_TERMINATOR);
        documentIndices.add(-1);

        // Create intData
        intData = new int [intDataList.size() + 1];
        for (int i = 0; i < intDataList.size(); i++)
        {
            intData[i] = intDataList.get(i);
        }
        intData[intDataList.size()] = -1;
    }

    /**
     * @param intDataList
     * @param tokenSequence
     */
    private void addToDataList(IntList intDataList,
        TokenSequence tokenSequence, short segment, int documentIndex)
    {
        for (int t = 0; t < tokenSequence.getLength(); t++)
        {
            int tokenCode;
            TypedToken token = (TypedToken) tokenSequence.getTokenAt(t);

            if ((token.getType() & TOKEN_FILTER_MASK) != 0
                && (token.getType() & TypedToken.TOKEN_FLAG_SENTENCE_DELIM) == 0)
            {
                continue;
            }

            // Here we can use either TOKEN_FLAG_SENTENCE_DELIM or
            // TOKEN_TYPE_PUNCTUATION. The earlier may prove more robust
            // in noisy text.
            if ((token.getType() & TypedToken.TOKEN_FLAG_SENTENCE_DELIM) != 0)
            {
                // Sentence delimiter - prevents phrases from crossing
                // sentence boundaries
                maxSentenceDelimiterCode -= MaskableIntWrapper.SECONDARY_OFFSET;
                tokenCode = maxSentenceDelimiterCode;
            }
            else if (tokens.containsKey(token))
            {
                tokenCode = ((Integer) tokens.get(token)).intValue();
            }
            else
            {
                if (token instanceof StemmedToken)
                {
                    String stem = ((StemmedToken) token).getStem();
                    if (stem == null)
                    {
                        stem = token.toString();
                    }

                    if (stems.containsKey(stem))
                    {
                        tokenCode = ((Integer) stems.get(stem)).intValue() + 1;

                        // Is the slot big enough?
                        if (tokenCode % MaskableIntWrapper.SECONDARY_OFFSET == 0)
                        {
                            // More than 32 different forms for the same stem?
                            throw new RuntimeException(
                                "The value of DualLcpSuffixSortingStrategy.SECONDARY_BITS is too small");
                        }
                    }
                    else
                    {
                        maxTokenCode += MaskableIntWrapper.SECONDARY_OFFSET;
                        tokenCode = maxTokenCode;
                    }

                    stems.put(stem, new Integer(tokenCode));
                }
                else
                {
                    maxTokenCode += MaskableIntWrapper.SECONDARY_OFFSET;
                    tokenCode = maxTokenCode;
                }

                // Check if the token matches any of the query words
                if (queryWords != null)
                {
                    String image = token.toString();
                    for (int i = 0; i < queryWords.length; i++)
                    {
                        if (image.equalsIgnoreCase(queryWords[i]))
                        {
                            queryWordCodes.put(tokenCode
                                & MaskableIntWrapper.SECONDARY_MASK, tokenCode
                                & MaskableIntWrapper.SECONDARY_MASK);
                        }
                    }
                }

                tokens.put(token, new Integer(tokenCode));
            }

            intDataList.add(tokenCode);
            segments.add(segment);
            documentIndices.add(documentIndex);
        }
    }

    /**
     * @return
     */
    public TokenizedDocumentsIntWrapper shallowCopy()
    {
        TokenizedDocumentsIntWrapper clone = new TokenizedDocumentsIntWrapper(
            (int []) this.intData.clone(), this.tokens, this.tokenStems);
        return clone;
    }
}