/*
 * FeatureSelectionStrategy.java Created on 2004-05-14
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;

/**
 * Defines the interface of an algorithm performing feature selection.
 * 
 * @author stachoo
 */
public interface FeatureSelectionStrategy
{
    /**
     * Defines token types that will be skipped during the selection process.
     * These are: stop words, symbols, punctuation marks and tokens of an
     * unknown type.
     */
    public static short DEFAULT_FILTER_MASK = TypedToken.TOKEN_FLAG_STOPWORD
        | TypedToken.TOKEN_TYPE_SYMBOL | TypedToken.TOKEN_TYPE_UNKNOWN
        | TypedToken.TOKEN_TYPE_PUNCTUATION;

    /**
     * Returns a list of {@link ExtendedToken}s selected by the algorithm.
     * Tokens must be sorted decreasingly according to the utility measure used
     * by the selection algorithm.
     * 
     * @param tokenizedDocuments a list of
     *            {@link com.dawidweiss.carrot.core.local.clustering.TokenizedDocument}
     *            objects
     * @return list of selected tokens
     */
    public List getSelectedFeatures(List tokenizedDocuments);
}