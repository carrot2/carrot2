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

import com.dawidweiss.carrot.core.local.linguistic.tokens.*;

/**
 * Defines the interface of an algorithm performing feature selection.
 * 
 * TODO: switching to an ordered Map instead of the list would increase
 * performance in a few places
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
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