
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

package org.carrot2.input.aggregator;

import java.util.List;
import java.util.Map;

import org.carrot2.core.clustering.RawDocument;

/**
 * Defines an interface of an algorithm for merging search results originating
 * from different sources.
 * 
 * @author Stanislaw Osinski
 */
public interface ResultsMerger
{
    /**
     * Merges a set of search results originating from different sources into
     * one list. Different stategies for determining and collapsing duplicates
     * can be implemented, but the following conditions must be met:
     * 
     * <ul>
     * <li>memebers of the resulting list must be of type {@link RawDocument}</li>
     * <li>each document found on the resulting list must have the
     * {@link RawDocument#PROPERTY_SOURCES} set to the array of idenfitiers of
     * sources that contributed the document</li>
     * </ul>
     * 
     * @param resultSets a mapping between search results source identifier and
     *            a {@link List} of {@link RawDocument}s returned by the source
     * @param inputs metadata about the inputs used by the aggregator
     * @return the merged search results list
     */
    public List mergeResults(Map resultSets, AggregatorInput [] inputs);
}
