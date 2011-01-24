package org.carrot2.core.test.assertions;

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThatDocuments;
import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.core.Cluster;

/**
 * Checks pairwise assertions on clusters.
 * 
 * @see ClusterListAssert#correspondsTo(java.util.List, ClusterPairAssert...)
 */
public interface ClusterPairAssert
{
    public static final ClusterPairAssert.ScoreEqual SCORE_EQUAL = new ScoreEqual();
    public static final ClusterPairAssert.PhrasesEqual PHRASES_EQUAL = new PhrasesEqual();
    public static final ClusterPairAssert.LabelEqual LABEL_EQUAL = new LabelEqual();
    public static final ClusterPairAssert.AllDocumentsEquivalent ALL_DOCUMENTS_EQUIVALENT = new AllDocumentsEquivalent();

    public void check(Cluster actual, Cluster expected);

    /**
     * Asserts that clusters have equal phrase lists.
     */
    public static class PhrasesEqual implements ClusterPairAssert
    {
        @Override
        public void check(Cluster actual, Cluster expected)
        {
            assertThat(actual.getPhrases()).as("cluster phrases").isEqualTo(
                expected.getPhrases());
        }
    }

    /**
     * Asserts that clusters have equal labels.
     */
    public static class LabelEqual implements ClusterPairAssert
    {
        @Override
        public void check(Cluster actual, Cluster expected)
        {
            assertThat(actual.getLabel()).as("cluster label").isEqualTo(
                expected.getLabel());
        }
    }

    /**
     * Asserts that clusters have equal scores.
     */
    public static class ScoreEqual implements ClusterPairAssert
    {
        @Override
        public void check(Cluster actual, Cluster expected)
        {
            assertThat(actual.getScore()).as("cluster score").isEqualTo(
                expected.getScore());
        }
    }

    /**
     * Asserts that clusters have all documents equivalent.
     */
    public static class AllDocumentsEquivalent implements ClusterPairAssert
    {
        @Override
        public void check(Cluster actual, Cluster expected)
        {
            assertThatDocuments(actual.getAllDocuments()).as("cluster all documents")
                .isEquivalentTo(expected.getAllDocuments());
        }
    }
}