package org.carrot2.text.suffixtrees2;

/**
 * Provides all information for constructing a {@link SuffixTree}.   
 */
public interface Sequence
{
    /**
     * Returns the number of elements in the sequence.
     */
    public int size();

    /**
     * Returns a unique integer code for object at index <code>i</code>.
     */
    public int objectAt(int i);
}
