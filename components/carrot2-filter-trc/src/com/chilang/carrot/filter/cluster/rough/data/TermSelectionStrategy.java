
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

package com.chilang.carrot.filter.cluster.rough.data;

/**
 * Strategy for selecting index terms
 */
interface TermSelectionStrategy {
    /**
     * Check if given term is accepted as index terms in current selection strategy
     * @param term
     * @return <code>true</code> if term is accepted; <code>false</code> otherwise
     */
    public boolean accept(Term term);
}
