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
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface FeatureSelection
{
    /**
     * Defines token types that will be skipped during the selection process.
     * These are: stop words, symbols, punctuation marks and tokens of an
     * unknown type.
     * 
     * Note: we don't index numerical tokens here, but detect them in phrases.
     * This clever thick will let a number appear only accompanied by some
     * non-numeric token(s).
     */
    public static short DEFAULT_FILTER_MASK = TypedToken.TOKEN_FLAG_STOPWORD
        | TypedToken.TOKEN_TYPE_SYMBOL | TypedToken.TOKEN_TYPE_UNKNOWN
        | TypedToken.TOKEN_TYPE_NUMERIC | TypedToken.TOKEN_TYPE_PUNCTUATION;

    /**
     * Returns a list of {@link ExtendedToken}s selected by the algorithm.
     * Tokens must be sorted decreasingly according to the utility measure used
     * by the selection algorithm.
     * 
     * @param context data source for the algorithm
     * @return list of selected tokens
     */
    public List getSelectedFeatures(ModelBuilderContext context);
}