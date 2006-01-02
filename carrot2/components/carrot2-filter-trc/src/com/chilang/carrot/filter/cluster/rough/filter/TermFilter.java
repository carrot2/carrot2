package com.chilang.carrot.filter.cluster.rough.filter;

/**
 * Interface for a term filter
 */
public interface TermFilter {

    /**
     * Test if term is accepted regarding this filter policy
     * @param term
     * @return <code>true</code> if term is accepted; <code>false</code> otherwise
     */
    public boolean accept(String term);
}
