/*
 * TdMatrixBuildingStrategy.java Created on 2004-05-14
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

import cern.colt.matrix.*;

/**
 * Defines the interface of an algorithm that creates a term-document matrix
 * based on a list of {@link TokenizedDocument}s.
 * 
 * @author stachoo
 */
public interface TdMatrixBuildingStrategy
{

    /**
     * Based on a list of
     * {@link com.dawidweiss.carrot.core.local.clustering.TokenizedDocument}s
     * and a list of selected features builds a term-document matrix.
     * 
     * @param tokenizedDocuments
     * @param selectedFeatures
     * @return term-document matrix
     */
    public DoubleMatrix2D getTdMatrix(List tokenizedDocuments,
            List selectedFeatures);
}