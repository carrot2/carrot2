/*
 * TokenizedDocumentsIntWrapper.java Created on 2004-06-15
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import org.apache.commons.collections.*;
import org.apache.commons.collections.bidimap.*;
import org.apache.commons.collections.primitives.*;

import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.linguistic.tokens.*;
import com.stachoodev.suffixarrays.wrapper.*;

/**
 * Documents must be sequences of
 * {@link com.dawidweiss.carrot.core.local.linguistic.tokens.TypedToken}s.
 * 
 * @author stachoo
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

    /** Current maximum integer code for a token */
    private int maxTokenCode;

    /** Current maximum integer code for a sentence delimiter */
    private int maxSentenceDelimiterCode;

    /** Token types to filter out */
    private static final short TOKEN_FILTER_MASK = TypedToken.TOKEN_TYPE_SYMBOL
            | TypedToken.TOKEN_TYPE_UNKNOWN;

    /**
     * @param tokenizedDocuments
     */
    public TokenizedDocumentsIntWrapper(List tokenizedDocuments)
    {
        maxTokenCode = -MaskableIntWrapper.SECONDARY_OFFSET;
        maxSentenceDelimiterCode = -1;
        tokens = new DualHashBidiMap();
        stems = new HashMap();
        tokenStems = new HashMap();

        createIntData(tokenizedDocuments);
    }

    /**
     * Returns a token associated with given code or <code>null</code> when
     * there is no token for given code.
     * 
     * @param code
     * @return
     */
    public TypedToken getTokenForCode(int code)
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

    /* (non-Javadoc)
     * @see com.stachoodev.suffixarrays.wrapper.TypeAwareIntWrapper#isStopWord(int)
     */
    public boolean isStopWord(int code)
    {
        return (getTokenForCode(code).getType() & TypedToken.TOKEN_FLAG_STOPWORD) != 0;
    }

    /**
     * @param tokenizedDocuments
     */
    protected void createIntData(List tokenizedDocuments)
    {
        IntList intDataList = new ArrayIntList();

        for (Iterator documents = tokenizedDocuments.iterator(); documents
                .hasNext();)
        {
            TokenizedDocument document = (TokenizedDocument) documents.next();

            TokenSequence tokenSequence = document.getTitle();
            addToDataList(intDataList, tokenSequence);

            // Document delimiter - prevents phrases from crossing document
            // boundaries
            if (tokenSequence.getLength() > 0)
            {
                maxSentenceDelimiterCode -= MaskableIntWrapper.SECONDARY_OFFSET;
                intDataList.add(maxSentenceDelimiterCode);
            }

            tokenSequence = document.getSnippet();
            addToDataList(intDataList, tokenSequence);

            // Document delimiter - prevents phrases from crossing document
            // boundaries
            if (tokenSequence.getLength() > 0)
            {
                maxSentenceDelimiterCode -= MaskableIntWrapper.SECONDARY_OFFSET;
                intDataList.add(maxSentenceDelimiterCode);
            }
        }

        // Create intData
        intData = new int[intDataList.size() + 1];
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
    private void addToDataList(IntList intDataList, TokenSequence tokenSequence)
    {
        for (int t = 0; t < tokenSequence.getLength(); t++)
        {
            int tokenCode;
            TypedToken token = (TypedToken) tokenSequence.getTokenAt(t);

            if ((token.getType() & TOKEN_FILTER_MASK) != 0)
            {
                continue;
            }

            if ((token.getType() & TypedToken.TOKEN_TYPE_PUNCTUATION) != 0)
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
                        if (tokenCode
                                % MaskableIntWrapper.SECONDARY_OFFSET == 0)
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
                tokens.put(token, new Integer(tokenCode));
            }

            intDataList.add(tokenCode);
        }
    }
}