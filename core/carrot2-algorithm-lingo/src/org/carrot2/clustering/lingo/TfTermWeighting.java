package org.carrot2.clustering.lingo;

/**
 * Calculates term-document matrix element values based on Log Inverse Term Frequency.
 */
public class TfTermWeighting implements TermWeighting
{
    public double calculateTermWeight(int termFrequency, int documentFrequency,
        int documentCount)
    {
        return termFrequency;
    }
}
