/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.core.test.assertions;

import static org.carrot2.core.test.assertions.Carrot2CoreAssertions.assertThat;
import static org.fest.assertions.Assertions.assertThat;

import org.carrot2.core.Cluster;
import org.fest.assertions.AssertExtension;

/**
 * Assertions on {@link Cluster}s.
 */
public class ClusterAssertion implements AssertExtension
{
    /** The actual cluster */
    private final Cluster actualCluster;

    ClusterAssertion(Cluster actual)
    {
        this.actualCluster = actual;
    }

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
        assertThat(actualCluster.getPhrases()).isEqualTo(expectedCluster.getPhrases());
        if (checkDocuments)
        {
            assertThat(actualCluster.getDocuments()).as(
                "cluster: " + actualCluster.getLabel()).isEquivalentTo(
                expectedCluster.getDocuments());
        }
        assertThat(actualCluster.getAttributes()).isEqualTo(
            expectedCluster.getAttributes());
        assertThat(actualCluster.getSubclusters()).isEquivalentTo(
            expectedCluster.getSubclusters(), checkDocuments);

        return this;
    }
}
