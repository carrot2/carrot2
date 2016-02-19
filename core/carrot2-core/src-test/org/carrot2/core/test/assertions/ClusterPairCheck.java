
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.test.assertions;

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThatDocuments;
import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.core.Cluster;
import org.fest.assertions.Delta;

/**
 * Checks pairwise assertions on clusters.
 * 
 * @see ClusterListAssertion#passRecursively(java.util.List, ClusterPairCheck...)
 */
public interface ClusterPairCheck
{
    public static final ClusterPairCheck.ScoreEqual SCORE_EQUAL = new ScoreEqual();
    public static final ClusterPairCheck.PhrasesEqual PHRASES_EQUAL = new PhrasesEqual();
    public static final ClusterPairCheck.LabelEqual LABEL_EQUAL = new LabelEqual();
    public static final ClusterPairCheck.AllDocumentsEquivalent ALL_DOCUMENTS_EQUIVALENT = new AllDocumentsEquivalent();

    public void check(Cluster actual, Cluster expected);

    /**
     * Asserts that clusters have equal phrase lists.
     */
    public static class PhrasesEqual implements ClusterPairCheck
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
    public static class LabelEqual implements ClusterPairCheck
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
    public static class ScoreEqual implements ClusterPairCheck
    {
        @Override
        public void check(Cluster actual, Cluster expected)
        {
            assertThat(actual.getScore()).as("cluster score").isEqualTo(
                expected.getScore(), Delta.delta(0.001));
        }
    }

    /**
     * Asserts that clusters have all documents equivalent.
     */
    public static class AllDocumentsEquivalent implements ClusterPairCheck
    {
        @Override
        public void check(Cluster actual, Cluster expected)
        {
            assertThatDocuments(actual.getAllDocuments()).as("cluster all documents")
                .isEquivalentTo(expected.getAllDocuments());
        }
    }
}
