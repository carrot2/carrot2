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

import cern.colt.matrix.*;

/**
 * Defines the interface of an algorithm that creates a term-document matrix
 * based on a list of {@link TokenizedDocument}s.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public interface TdMatrixBuilding
{
    /**
     * TdMatrixBuildingStrategy can set a non-null value of this property for a
     * document that has been omitted during creating the term-document matrix.
     * A reason for omitting a document can be e.g. that the document would not
     * have any non-zero elements in its corresponding column in the matrix.
     */
    public static final String PROPERTY_DOCUMENT_OMITTED = "tmbs-omitted";

    /**
     * Based on a list of
     * {@link com.dawidweiss.carrot.core.local.clustering.TokenizedDocument}s
     * and a list of selected features builds a term-document matrix.
     * 
     * @param context data source for the algorithm
     * @return term-document matrix
     */
    public DoubleMatrix2D getTdMatrix(ModelBuilderContext context);
}