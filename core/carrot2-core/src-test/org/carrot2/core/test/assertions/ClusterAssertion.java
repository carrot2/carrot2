
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

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThatClusters;
import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThatDocuments;
import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.core.Cluster;
import org.fest.assertions.AssertExtension;
import org.fest.assertions.GenericAssert;

/**
 * Assertions on {@link Cluster}s.
 */
public class ClusterAssertion 
    extends GenericAssert<ClusterAssertion, Cluster> 
    implements AssertExtension
{
    ClusterAssertion(Cluster actual)
    {
        super(ClusterAssertion.class, actual);
    }

    /** Description of the assertion */
    private String description;

    /**
     * Asserts that the cluster is equivalent to the provided cluster. Two clusters are
     * equivalent if their {@link Cluster#getPhrases()} and
     * {@link Cluster#getAttributes()} are equal, and their
     * {@link Cluster#getSubclusters()} and {@link Cluster#getDocuments()} are equivalent
     * 
     * @see DocumentAssertion#isEquivalentTo(org.carrot2.core.Document)
     * @param expectedCluster the expected cluster
     * @return this assertion for convenience
     */
    public ClusterAssertion isEquivalentTo(Cluster expectedCluster)
    {
        return isEquivalentTo(expectedCluster, true);
    }

    /**
     * Asserts that the cluster is equivalent to the provided cluster. Two clusters are
     * equivalent if their {@link Cluster#getPhrases()} and
     * {@link Cluster#getAttributes()} are equal, and their
     * {@link Cluster#getSubclusters()} and {@link Cluster#getDocuments()} are equivalent
     * 
     * @see DocumentAssertion#isEquivalentTo(org.carrot2.core.Document)
     * @param expectedCluster the expected cluster
     * @param checkDocuments if <code>false</code>, cluster's document references will not
     *            be checked
     * @return this assertion for convenience
     */
    public ClusterAssertion isEquivalentTo(Cluster expectedCluster, boolean checkDocuments)
    {
        assertThat(actual.getPhrases()).isEqualTo(expectedCluster.getPhrases());
        if (checkDocuments)
        {
            assertThatDocuments(actual.getDocuments()).as(
                description + ": " + "cluster: " + actual.getLabel()).isEquivalentTo(
                expectedCluster.getDocuments());
        }
        assertThat(actual.getAttributes()).isEqualTo(expectedCluster.getAttributes());
        assertThatClusters(actual.getSubclusters()).isEquivalentTo(
            expectedCluster.getSubclusters(), checkDocuments);

        return this;
    }

    /**
     * Asserts that the cluster's Other Topics flag is set to the required state.
     */
    public ClusterAssertion isOtherTopics(boolean isOtherTopics, String otherTopicsLabel)
    {
        assertThat(actual.isOtherTopics()).isEqualTo(isOtherTopics);
        if (isOtherTopics)
        {
            assertThat(actual.getPhrases()).contains(otherTopicsLabel);
        }
        else
        {
            assertThat(actual.getPhrases()).excludes(otherTopicsLabel);
        }
        return this;
    }

    /**
     * Asserts that the cluster's Other Topics flag is set to the required state.
     */
    public ClusterAssertion isOtherTopics(boolean isOtherTopics)
    {
        return isOtherTopics(isOtherTopics, "Other Topics");
    }

    /**
     * Assert that the cluster's label is equal to <code>expectedLabel</code>.
     */
    public ClusterAssertion hasLabel(String expectedLabel)
    {
        assertThat(actual.getLabel()).isEqualTo(expectedLabel);
        return this;
    }

    /**
     * Assert that the number of unique documents in the cluster and its subclusters is
     * equal to <code>expectedClusterSize</code>.
     */
    public ClusterAssertion hasSize(int expectedClusterSize)
    {
        assertThat(actual.getAllDocuments().size()).isEqualTo(expectedClusterSize);
        return this;
    }
}
