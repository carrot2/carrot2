/**
 *
 */
package org.carrot2.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 */
public class ClusterTest
{
    @Test
    public void testSizeEmptyFlat()
    {
        final Cluster flatCluster = new Cluster();
        assertEquals(0, flatCluster.size());
    }

    @Test
    public void testSizeEmptyHierarchical()
    {
        final Cluster hierarchicalCluster = new Cluster();
        final Cluster subcluster = new Cluster();

        hierarchicalCluster.addSubclusters(subcluster);
        subcluster.addSubclusters(new Cluster());

        assertEquals(0, hierarchicalCluster.size());
    }

    @Test
    public void testSizeNonEmptyFlat()
    {
        final Cluster flatCluster = new Cluster();
        flatCluster.addDocuments(new Document(), new Document());
        assertEquals(2, flatCluster.size());
    }

    @Test
    public void testSizeNonEmptyHierarchicalWithoutOverlap()
    {
        final Cluster hierarchicalCluster = new Cluster();
        final Cluster subcluster = new Cluster();

        hierarchicalCluster.addSubclusters(subcluster);

        hierarchicalCluster.addDocuments(new Document());
        subcluster.addDocuments(new Document());

        assertEquals(2, hierarchicalCluster.size());
    }

    @Test
    public void testSizeNonEmptyHierarchicalWithOverlap()
    {
        final Cluster hierarchicalCluster = new Cluster();
        final Cluster subcluster = new Cluster();
        hierarchicalCluster.addSubclusters(subcluster);

        final Document document1 = new Document();

        hierarchicalCluster.addDocuments(document1);
        hierarchicalCluster.addDocuments(new Document());
        subcluster.addDocuments(document1);
        subcluster.addDocuments(new Document());

        assertEquals(3, hierarchicalCluster.size());
    }

    @Test
    public void testByLabelComparator()
    {
        final Cluster clusterA = new Cluster();
        clusterA.addPhrases("A");

        final Cluster clusterB = new Cluster();
        clusterB.addPhrases("b");

        final Cluster clusterNull = new Cluster();

        assertEquals(0, Cluster.BY_LABEL_COMPARATOR.compare(clusterA, clusterA));
        assertTrue(Cluster.BY_LABEL_COMPARATOR.compare(clusterA, clusterB) < 0);
        assertTrue(Cluster.BY_LABEL_COMPARATOR.compare(clusterNull, clusterB) < 0);
    }

    @Test
    public void testBySizeComparator()
    {
        final Cluster clusterA = new Cluster();
        clusterA.addDocuments(new Document(), new Document());

        final Cluster clusterB = new Cluster();

        assertEquals(0, Cluster.BY_SIZE_COMPARATOR.compare(clusterA, clusterA));
        assertTrue(Cluster.BY_SIZE_COMPARATOR.compare(clusterA, clusterB) > 0);
    }

    @Test
    public void testByReversedSizeAndLabelComparator()
    {
        final Cluster clusterA = new Cluster();
        clusterA.addPhrases("A");
        clusterA.addDocuments(new Document(), new Document());

        final Cluster clusterB = new Cluster();
        clusterB.addPhrases("B");
        clusterB.addDocuments(new Document(), new Document());

        final Cluster clusterC = new Cluster();
        clusterC.addPhrases("C");
        clusterC.addDocuments(new Document(), new Document(), new Document());

        assertEquals(0, Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR.compare(clusterA,
            clusterA));
        assertTrue(Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR.compare(clusterA,
            clusterB) < 0);
        assertTrue(Cluster.BY_REVERSED_SIZE_AND_LABEL_COMPARATOR.compare(clusterA,
            clusterC) > 0);
    }
}
