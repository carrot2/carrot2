package org.carrot2.clustering.lingo;

/**
 * Calculates term-document matrix element values based on Term Frequency.
 */
public class LogTfIdfTermWeighting implements TermWeighting
{
    public double calculateTermWeight(int termFrequency, int documentFrequency,
        int documentCount)
    {
        if (documentFrequency > 0)
        {
            return termFrequency * Math.log(documentCount / (double) documentFrequency);
        }
        else
        {
            return 0;
        }
    }
}
