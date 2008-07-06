package org.carrot2.clustering.lingo;

/**
 * Calculates term weights for the term-document matrix.
 * 
 * @see TermDocumentMatrixBuilder
 */
public interface TermWeighting
{
    /**
     * Calculates the weight of a term for a single document.
     * 
     * @param termFrequency frequency of the term in the document
     * @param documentFrequency the number of documents containing the term
     * @param documentCount total number of documents
     */
    public double calculateTermWeight(int termFrequency, int documentFrequency,
        int documentCount);
}
