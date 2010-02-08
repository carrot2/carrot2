/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.test.assertions;

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.carrot2.core.Cluster;
import org.fest.assertions.Assertions;

/**
 * Assertions on lists of {@link Cluster}s.
 */
public class ClusterListAssertion
{
    /** The actual list of clusters */
    private final List<Cluster> actualClusterList;

    ClusterListAssertion(List<Cluster> actualClusterList)
    {
        this.actualClusterList = actualClusterList;
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
        assertThat(actualClusterList).hasSize(expectedClusterList.size());
        for (int i = 0; i < actualClusterList.size(); i++)
        {
            assertThat(actualClusterList.get(i)).isEquivalentTo(
                expectedClusterList.get(i), checkDocuments);
        }
        return this;
    }

    /**
     * Asserts that the cluster list has the provided size.
     * 
     * @param expectedSize the expected list size
     * @return this assertion for convenience
     */
    public ClusterListAssertion hasSize(int expectedSize)
    {
        Assertions.assertThat(actualClusterList).hasSize(expectedSize);
        return this;
    }

    /**
     * Asserts that there are no Other Topics clusters on the provided list.
     * 
     * @return this assertion for convenience
     */
    public ClusterListAssertion doesNotContainOtherTopicsClusters()
    {
        for (int i = 0; i < actualClusterList.size(); i++)
        {
            assertThat(actualClusterList.get(i)).isOtherTopics(false);
        }
        return this;
    }
}
