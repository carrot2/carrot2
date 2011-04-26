/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2011, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.test.assertions;

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThat;
import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThatClusters;
import static org.fest.assertions.Assertions.assertThat;

import java.util.Iterator;
import java.util.List;

import org.carrot2.core.Cluster;

/**
 * Assertions on lists of {@link Cluster}s.
 */
public class ClusterListAssertion extends
    GenericListAssertion<ClusterListAssertion, Cluster>
{
    ClusterListAssertion(List<Cluster> actualClusterList)
    {
        super(ClusterListAssertion.class, actualClusterList);
    }

    /**
     * Asserts that the cluster list is equivalent to the provided cluster list. Two lists
     * of clusters are equivalent if they have the same size, and the clusters on the
     * corresponding positions on the lists are equivalent (see
     * {@link ClusterAssertion#isEquivalentTo(Cluster)}.
     * 
     * @param expectedClusterList the expected cluster list
     * @return this assertion for convenience
     */
    public ClusterListAssertion isEquivalentTo(List<Cluster> expectedClusterList)
    {
        return isEquivalentTo(expectedClusterList, true);
    }

    /**
     * Asserts that the cluster list is equivalent to the provided cluster list. Two lists
     * of clusters are equivalent if they have the same size, and the clusters on the
     * corresponding positions on the lists are equivalent (see
     * {@link ClusterAssertion#isEquivalentTo(Cluster, boolean)}.
     * 
     * @param expectedClusterList the expected cluster list
     * @param checkDocuments if <code>false</code>, cluster's document references will not
     *            be checked
     * @return this assertion for convenience
     */
    public ClusterListAssertion isEquivalentTo(List<Cluster> expectedClusterList,
        boolean checkDocuments)
    {
        assertThat(actual).hasSize(expectedClusterList.size());
        for (int i = 0; i < actual.size(); i++)
        {
            assertThat(actual.get(i)).isEquivalentTo(expectedClusterList.get(i),
                checkDocuments);
        }
        return this;
    }

    /**
     * Asserts that there are no Other Topics clusters on the provided list.
     * 
     * @return this assertion for convenience
     */
    public ClusterListAssertion doesNotContainOtherTopicsClusters()
    {
        for (int i = 0; i < actual.size(); i++)
        {
            assertThat(actual.get(i)).isOtherTopics(false);
        }
        return this;
    }

    /**
     * Recursively runs pairwise cluster checks on the actual clusters vs the provided
     * expected clusters hierarchy.
     */
    public ClusterListAssertion passRecursively(List<Cluster> expected,
        ClusterPairCheck... clusterAsserts)
    {
        if (expected == null)
        {
            assertThat(actual).isNull();
        }
        else
        {
            assertThat(actual).isNotNull();
        }

        assertThat(actual.size()).as(description() + ": list size").isEqualTo(
            expected.size());

        Iterator<Cluster> expectedIt = expected.iterator();
        Iterator<Cluster> actualIt = actual.iterator();
        while (expectedIt.hasNext() && actualIt.hasNext())
        {
            final Cluster actualCluster = actualIt.next();
            final Cluster expectedCluster = expectedIt.next();
            for (ClusterPairCheck clusterPairAssert : clusterAsserts)
            {
                clusterPairAssert.check(actualCluster, expectedCluster);
            }

            assertThatClusters(actualCluster.getSubclusters()).as(
                description() + ": subclusters of \"" + actualCluster.getLabel() + "\"")
                .passRecursively(expectedCluster.getSubclusters(), clusterAsserts);
        }

        return this;
    }

    /**
     * Asserts that the provided checks pass for all clusters on the list, including their
     * subclusters.
     */
    public ClusterListAssertion passRecursively(ClusterCheck... checks)
    {
        for (Cluster cluster : actual)
        {
            for (ClusterCheck clusterCheck : checks)
            {
                clusterCheck.check(cluster);
                assertThatClusters(cluster.getSubclusters()).passRecursively(checks);
            }
        }
        return this;
    }
}
