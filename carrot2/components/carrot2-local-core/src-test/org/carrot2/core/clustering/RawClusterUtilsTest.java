
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

package org.carrot2.core.clustering;

import junit.framework.TestCase;

/**
 * Test cases for the {@link RawClusterUtils} class.
 *
 * @author Stanislaw Osinski
 */
public class RawClusterUtilsTest extends TestCase
{
    public void testZeroClusterSize()
    {
        RawClusterBase rawCluster = new RawClusterBase();
        assertEquals(0, RawClusterUtils.calculateSize(rawCluster));
    }

    public void testFlatClusterSize()
    {
        RawClusterBase rawCluster = new RawClusterBase();
        rawCluster.addDocument(new RawDocumentSnippet("d1", null, null, null, 0.0f));
        rawCluster.addDocument(new RawDocumentSnippet("d2", null, null, null, 0.0f));
        rawCluster.addDocument(new RawDocumentSnippet("d3", null, null, null, 0.0f));

        assertEquals(3, RawClusterUtils.calculateSize(rawCluster));
    }

    public void testHierarchicalClusterSizeWithOverlap()
    {
        RawClusterBase rawCluster = new RawClusterBase();
        final RawDocumentSnippet d1 = new RawDocumentSnippet("d1", null, null, null, 0.0f);
        final RawDocumentSnippet d2 = new RawDocumentSnippet("d2", null, null, null, 0.0f);
        rawCluster.addDocument(d1);
        rawCluster.addDocument(d2);
        rawCluster.addDocument(new RawDocumentSnippet("d3", null, null, null, 0.0f));

        RawClusterBase subcluster = new RawClusterBase();
        subcluster.addDocument(d1);
        subcluster.addDocument(d2);
        rawCluster.addSubcluster(subcluster);

        assertEquals(3, RawClusterUtils.calculateSize(rawCluster));
    }

    public void testHierarchicalClusterSizeWithoutOverlap()
    {
        RawClusterBase rawCluster = new RawClusterBase();
        rawCluster.addDocument(new RawDocumentSnippet("d1", null, null, null, 0.0f));
        rawCluster.addDocument(new RawDocumentSnippet("d2", null, null, null, 0.0f));
        rawCluster.addDocument(new RawDocumentSnippet("d3", null, null, null, 0.0f));

        RawClusterBase subcluster = new RawClusterBase();
        subcluster.addDocument(new RawDocumentSnippet("d4", null, null, null, 0.0f));
        subcluster.addDocument(new RawDocumentSnippet("d5", null, null, null, 0.0f));
        rawCluster.addSubcluster(subcluster);

        assertEquals(5, RawClusterUtils.calculateSize(rawCluster));
    }
}
