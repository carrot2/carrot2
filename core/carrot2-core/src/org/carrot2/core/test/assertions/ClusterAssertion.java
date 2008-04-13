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
     * (see {@link DocumentAssertion#isEquivalentTo(org.carrot2.core.Document)).
     * 
     * @param expectedCluster the expected cluster
     * @return this assertion for convenience
     */
    public ClusterAssertion isEquivalentTo(Cluster expectedCluster)
    {
        assertThat(actualCluster.getPhrases()).isEqualTo(expectedCluster.getPhrases());
        assertThat(actualCluster.getDocuments()).isEquivalentTo(
            expectedCluster.getDocuments());
        assertThat(actualCluster.getAttributes()).isEqualTo(
            expectedCluster.getAttributes());
        assertThat(actualCluster.getSubclusters()).isEquivalentTo(
            expectedCluster.getSubclusters());

        return this;
    }
}
