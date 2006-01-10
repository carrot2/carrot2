
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.chilang.carrot.filter.cluster.rough.filter;


public interface TermSelectionStrategy {
    /**
     * Select terms based on term frequencies array
     * @param termFreqencies array of term frequencies indexed by term's id
     * @return array of selected term's ids
     */
    public int[] select(int[] termFreqencies);
}
