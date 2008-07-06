package org.carrot2.clustering.lingo;

/**
 * Calculates term-document matrix element values based on Linear Inverse Term Frequency.
 */
public class LinearTfIdfTermWeighting implements TermWeighting
{
    public double calculateTermWeight(int termFrequency, int documentFrequency,
        int documentCount)
    {
        return termFrequency
            * ((documentCount - documentFrequency) / (double) (documentFrequency - 1));
    }
}
