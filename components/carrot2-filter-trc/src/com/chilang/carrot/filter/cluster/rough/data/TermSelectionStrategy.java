/**
 * 
 * @author chilang
 * Created 2003-12-16, 22:23:42.
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
