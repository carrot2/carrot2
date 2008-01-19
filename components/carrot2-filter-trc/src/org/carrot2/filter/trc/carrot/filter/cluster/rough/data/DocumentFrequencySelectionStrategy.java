
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

package org.carrot2.filter.trc.carrot.filter.cluster.rough.data;

/**
 * Select index term based on its document frequency in the whole collection
 */
public class DocumentFrequencySelectionStrategy implements TermSelectionStrategy{
    protected int minimunFrequency;

    /**
     * Construct a term selection strategy based on
     * term's global document frequency.
     * Terms with document frequency greater or equal to minFrequency are selected
     * @param minFrequency
     */
    public DocumentFrequencySelectionStrategy(int minFrequency) {
        minimunFrequency = minFrequency;
    }

    public boolean accept(Term term) {
        return term.getTfMap().size() >= minimunFrequency;
    }
}
