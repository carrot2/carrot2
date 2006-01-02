package com.chilang.carrot.filter.cluster.rough.data;

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
