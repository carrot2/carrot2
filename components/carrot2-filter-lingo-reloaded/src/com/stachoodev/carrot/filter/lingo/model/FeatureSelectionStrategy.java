/*
 * FeatureSelectionStrategy.java Created on 2004-05-14
 */
package com.stachoodev.carrot.filter.lingo.model;

import java.util.*;

/**
 * Defines the interface of an algorithm performing feature selection.
 * 
 * @author stachoo
 */
public interface FeatureSelectionStrategy
{
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