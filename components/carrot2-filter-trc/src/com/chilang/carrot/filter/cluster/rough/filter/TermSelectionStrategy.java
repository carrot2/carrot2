/**
 * 
 * @author chilang
 * Created 2003-08-12, 18:25:02.
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
