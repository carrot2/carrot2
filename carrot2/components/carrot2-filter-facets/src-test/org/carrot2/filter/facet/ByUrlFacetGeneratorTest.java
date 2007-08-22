/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
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
import org.carrot2.util.Asserts;

/**
 * Test cases for the {@link BySourceFacetGenerator} class.
 * 
 * @author Stanislaw Osinski
 */
public class ByUrlFacetGeneratorTest extends TestCase
{
    public void testEmpty()
    {
        List rawDocuments = Collections.EMPTY_LIST;
        List expectedFacets = Collections.EMPTY_LIST;

        checkAsserts(rawDocuments, expectedFacets);
    }

    public void testUrlParsing()
    {
        RawDocument [] docs = createRawDocumentsWithUrls(new String []
        {
            "cos.pl", "http://cos.pl/cos", "cos.pl/cos", "http://", null
        });

        String [][] actualUrlParts = ByUrlFacetGenerator.INSTANCE.buildUrlParts(docs);
        String [][] expectedUrlParts = new String [] []
        {
            {
                "pl", "cos"
            },
            {
                "pl", "cos"
            },
            {
                "pl", "cos"
            }, null, null
        };

        Asserts.assertEquals("Url parts equality", expectedUrlParts, actualUrlParts);
    }

    public void testOneUrl()
    {
        RawDocument [] docs = createRawDocumentsWithUrls(new String []
        {
            "cos.pl", "http://cos.pl/cos", "cos.pl/cos"
        });

        List expectedFacets = new ArrayList();
        RawClusterBase facet1 = new RawClusterBase();
        facet1.addLabel("cos.pl");
        facet1.addDocument(docs[0]);
        facet1.addDocument(docs[1]);
        facet1.addDocument(docs[2]);
        expectedFacets.add(facet1);

        checkAsserts(Arrays.asList(docs), expectedFacets);
    }

    public void testStopPartsStripping()
    {
        RawDocument [] docs = createRawDocumentsWithUrls(new String []
        {
            "www.cos.pl", "http://cos.pl/cos", "cos.pl/cos"
        });

        List expectedFacets = new ArrayList();
        RawClusterBase facet1 = new RawClusterBase();
        facet1.addLabel("cos.pl");
        facet1.addDocument(docs[0]);
        facet1.addDocument(docs[1]);
        facet1.addDocument(docs[2]);
        expectedFacets.add(facet1);

        checkAsserts(Arrays.asList(docs), expectedFacets);
    }

    public void testOneUrlWithOneSuburl()
    {
        RawDocument [] docs = createRawDocumentsWithUrls(new String []
        {
            "mail.cos.pl", "http://cos.pl/cos", "cos.pl/cos"
        });

        List expectedFacets = new ArrayList();
        RawClusterBase facet1 = new RawClusterBase();
        facet1.addLabel("cos.pl");
        facet1.addDocument(docs[0]);
        facet1.addDocument(docs[1]);
        facet1.addDocument(docs[2]);
        expectedFacets.add(facet1);

        checkAsserts(Arrays.asList(docs), expectedFacets);
    }

    public void testOneUrlWithTwoSuburls()
    {
        RawDocument [] docs = createRawDocumentsWithUrls(new String []
        {
            "mail.cos.pl", "http://cos.pl/cos", "cos.pl/cos", "mail.cos.pl"
        });

        List expectedFacets = new ArrayList();
        RawClusterBase facet1 = new RawClusterBase();
        facet1.addLabel("cos.pl");
        RawClusterBase facet11 = new RawClusterBase();
        facet11.addLabel("mail.cos.pl");
        facet11.addDocument(docs[0]);
        facet11.addDocument(docs[3]);
        facet1.addSubcluster(facet11);
        RawClusterBase facet12 = new RawClusterBase();
        facet12.addLabel("Other Sites");
        facet12.addDocument(docs[1]);
        facet12.addDocument(docs[2]);
        facet1.addSubcluster(facet12);
        expectedFacets.add(facet1);

        checkAsserts(Arrays.asList(docs), expectedFacets);
    }

    public void testTwoUrls()
    {
        RawDocument [] docs = createRawDocumentsWithUrls(new String []
        {
            "cos.pl", "http://cos.pl/cos", "cos.com/cos", "cos.com"
        });

        List expectedFacets = new ArrayList();
        RawClusterBase facet1 = new RawClusterBase();
        facet1.addLabel("cos.pl");
        facet1.addDocument(docs[0]);
        facet1.addDocument(docs[1]);
        RawClusterBase facet2 = new RawClusterBase();
        facet2.addLabel("cos.com");
        facet2.addDocument(docs[2]);
        facet2.addDocument(docs[3]);
        expectedFacets.add(facet2);
        expectedFacets.add(facet1);

        checkAsserts(Arrays.asList(docs), expectedFacets);
    }

    private void checkAsserts(List rawDocuments, List expectedFacets)
    {
        final List actualFacets = ByUrlFacetGenerator.INSTANCE
            .generateFacets(rawDocuments);
        assertNotNull(actualFacets);
        assertEquals(expectedFacets.size(), actualFacets.size());
        for (int i = 0; i < actualFacets.size(); i++)
        {
            assertEquals((RawCluster) expectedFacets.get(i), (RawCluster) actualFacets
                .get(i));
        }
    }

    private RawDocument [] createRawDocumentsWithUrls(String [] urls)
    {
        RawDocument [] result = new RawDocument [urls.length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = new RawDocumentSnippet("d" + i, "d" + i, null, urls[i], 0.0f);
        }

        return result;
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

        assertEquals(expected.getSubclusters().size(), actual.getSubclusters().size());
        for (int i = 0; i < expected.getSubclusters().size(); i++)
        {
            assertEquals((RawCluster) expected.getSubclusters().get(i),
                (RawCluster) actual.getSubclusters().get(i));
        }
    }
}
