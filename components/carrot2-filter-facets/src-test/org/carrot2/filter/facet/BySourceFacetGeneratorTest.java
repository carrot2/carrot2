
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

package org.carrot2.filter.facet;

import java.util.*;

import junit.framework.TestCase;

import org.carrot2.core.clustering.*;

/**
 * Test cases for the {@link BySourceFacetGenerator} class.
 *
 * @author Stanislaw Osinski
 */
public class BySourceFacetGeneratorTest extends TestCase
{
    public void testEmpty()
    {
        List rawDocuments = Collections.EMPTY_LIST;
        List expectedFacets = Collections.EMPTY_LIST;

        checkAsserts(rawDocuments, expectedFacets);
    }

    public void testNoSourceInformation()
    {
        List rawDocuments = new ArrayList();
        final RawDocumentSnippet doc1 = new RawDocumentSnippet("d1", null);
        rawDocuments.add(doc1);
        final RawDocumentSnippet doc2 = new RawDocumentSnippet("d2", null);
        rawDocuments.add(doc2);

        List expectedFacets = new ArrayList();
        RawClusterBase facet1 = new RawClusterBase();
        facet1.addLabel("Unknown");
        facet1.addDocument(doc1);
        facet1.addDocument(doc2);
        expectedFacets.add(facet1);

        checkAsserts(rawDocuments, expectedFacets);
    }

    public void testOneSource()
    {
        final String [] sources = new String []
        {
            "src"
        };

        List rawDocuments = new ArrayList();
        final RawDocumentSnippet doc1 = new RawDocumentSnippet("d1", null);
        doc1.setProperty(RawDocument.PROPERTY_SOURCES, sources);
        rawDocuments.add(doc1);
        final RawDocumentSnippet doc2 = new RawDocumentSnippet("d2", null);
        doc2.setProperty(RawDocument.PROPERTY_SOURCES, sources);
        rawDocuments.add(doc2);

        List expectedFacets = new ArrayList();
        final RawClusterBase facet1 = new RawClusterBase();
        facet1.addLabel("src");
        facet1.addDocument(doc1);
        facet1.addDocument(doc2);
        expectedFacets.add(facet1);

        checkAsserts(rawDocuments, expectedFacets);
    }

    public void testTwoSourcesAndUnknown()
    {
        final String [] src1 = new String []
        {
            "src1"
        };
        final String [] src2 = new String []
        {
            "src2"
        };
        final String [] src3 = new String []
        {
            "src3"
        };
        final String [] src12 = new String []
        {
            "src1", "src2"
        };

        List rawDocuments = new ArrayList();
        final RawDocumentSnippet doc5 = new RawDocumentSnippet("d5", null);
        doc5.setProperty(RawDocument.PROPERTY_SOURCES, src3);
        rawDocuments.add(doc5);
        final RawDocumentSnippet doc1 = new RawDocumentSnippet("d1", null);
        doc1.setProperty(RawDocument.PROPERTY_SOURCES, src1);
        rawDocuments.add(doc1);
        final RawDocumentSnippet doc2 = new RawDocumentSnippet("d2", null);
        doc2.setProperty(RawDocument.PROPERTY_SOURCES, src2);
        rawDocuments.add(doc2);
        final RawDocumentSnippet doc3 = new RawDocumentSnippet("d3", null);
        doc3.setProperty(RawDocument.PROPERTY_SOURCES, src12);
        rawDocuments.add(doc3);
        final RawDocumentSnippet doc4 = new RawDocumentSnippet("d4", null);
        rawDocuments.add(doc4);

        List expectedFacets = new ArrayList();
        final RawClusterBase facet1 = new RawClusterBase();
        facet1.addLabel("src1");
        facet1.addDocument(doc1);
        facet1.addDocument(doc3);
        expectedFacets.add(facet1);
        final RawClusterBase facet2 = new RawClusterBase();
        facet2.addLabel("src2");
        facet2.addDocument(doc2);
        facet2.addDocument(doc3);
        expectedFacets.add(facet2);
        final RawClusterBase facet3 = new RawClusterBase();
        facet3.addLabel("src3");
        facet3.addDocument(doc5);
        expectedFacets.add(facet3);
        final RawClusterBase facet4 = new RawClusterBase();
        facet4.addLabel("Unknown");
        facet4.addDocument(doc4);
        expectedFacets.add(facet4);

        checkAsserts(rawDocuments, expectedFacets);
    }

    private void checkAsserts(List rawDocuments, List expectedFacets)
    {
        final List actualFacets = BySourceFacetGenerator.INSTANCE
            .generateFacets(rawDocuments);
        assertNotNull(actualFacets);
        assertEquals(expectedFacets.size(), actualFacets.size());
        for (int i = 0; i < actualFacets.size(); i++)
        {
            assertEquals((RawCluster) expectedFacets.get(i), (RawCluster) actualFacets
                .get(i));
        }
    }

    /**
     * Does not compare properties!
     */
    private void assertEquals(RawCluster expected, RawCluster actual)
    {
        if (expected == null)
        {
            assertNull(actual);
            return;
        }

        assertNotNull(actual);

        assertEquals(expected.getDocuments(), actual.getDocuments());
        assertEquals(expected.getClusterDescription(), actual.getClusterDescription());

        assertEquals(expected.getSubclusters(), actual.getSubclusters());
    }
}
