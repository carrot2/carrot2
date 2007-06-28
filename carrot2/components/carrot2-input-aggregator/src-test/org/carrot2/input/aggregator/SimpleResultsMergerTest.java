/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.aggregator;

import java.util.*;

import org.carrot2.core.clustering.RawDocument;
import org.carrot2.core.clustering.RawDocumentSnippet;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;
import junitx.framework.ListAssert;

/**
 * @author Stanislaw Osinski
 */
public class SimpleResultsMergerTest extends TestCase
{
    /** Merger under tests */
    private SimpleResultsMerger resultsMerger = new SimpleResultsMerger();

    private static final AggregatorInput [] INPUTS = new AggregatorInput []
    {
        new AggregatorInput("a", null, 1.0),
        new AggregatorInput("b", null, 1.0),
        new AggregatorInput("c", null, 1.0)
    };

    public void testEmpty()
    {
        Map resultSets = new HashMap();

        List merged = resultsMerger.mergeResults(resultSets, INPUTS);
        assertNotNull(merged);
        assertEquals(0, merged.size());
    }

    public void testEmptyDocumentLists()
    {
        Map resultSets = new HashMap();
        resultSets.put("id1", Collections.EMPTY_LIST);
        resultSets.put("id2", Collections.EMPTY_LIST);

        List merged = resultsMerger.mergeResults(resultSets, INPUTS);
        assertNotNull(merged);
        assertEquals(0, merged.size());
    }

    public void testNoMerge1()
    {
        Map resultSets = new HashMap();

        List documentListA = new ArrayList();
        resultSets.put("a", documentListA);
        final RawDocumentSnippet docA = new RawDocumentSnippet("a", "a", "a",
            "a", 0.0f);
        documentListA.add(docA);

        List documentListB = new ArrayList();
        resultSets.put("b", documentListB);
        final RawDocumentSnippet docB = new RawDocumentSnippet("b", "b", "b",
            "b", 0.0f);
        documentListB.add(docB);

        List documentListC = new ArrayList();
        resultSets.put("c", documentListC);
        final RawDocumentSnippet docC = new RawDocumentSnippet("c", "c", "c",
            "c", 0.0f);
        documentListC.add(docC);

        List merged = resultsMerger.mergeResults(resultSets, INPUTS);
        assertNotNull(merged);
        assertEquals(3, merged.size());
        ListAssert.assertContains(merged, docA);
        ListAssert.assertContains(merged, docB);
        ListAssert.assertContains(merged, docC);
    }

    public void testNoMerge2()
    {
        Map resultSets = new HashMap();

        List documentListA = new ArrayList();
        resultSets.put("a", documentListA);
        final RawDocumentSnippet docA = new RawDocumentSnippet("a", "a", "a",
            "a", 0.0f);
        final RawDocumentSnippet docB = new RawDocumentSnippet("b", "a", "a",
            "b", 0.0f);
        final RawDocumentSnippet docC = new RawDocumentSnippet("c", "a", "a",
            "c", 0.0f);
        documentListA.add(docA);
        documentListA.add(docB);
        documentListA.add(docC);

        List merged = resultsMerger.mergeResults(resultSets, INPUTS);
        assertNotNull(merged);
        assertEquals(3, merged.size());
        ListAssert.assertContains(merged, docA);
        ListAssert.assertContains(merged, docB);
        ListAssert.assertContains(merged, docC);
    }

    public void testMergeByUrl()
    {
        Map resultSets = new HashMap();

        List documentListA = new ArrayList();
        resultSets.put("a", documentListA);
        final RawDocumentSnippet docA = new RawDocumentSnippet("a", "aa", "aa",
            "a", 0.0f);
        documentListA.add(docA);

        List documentListB = new ArrayList();
        resultSets.put("b", documentListB);
        final RawDocumentSnippet docB = new RawDocumentSnippet("b", "b", "b",
            "a", 0.0f);
        documentListB.add(docB);

        List documentListC = new ArrayList();
        resultSets.put("c", documentListC);
        final RawDocumentSnippet docC = new RawDocumentSnippet("c", "c", "c",
            "c", 0.0f);
        documentListC.add(docC);

        List merged = resultsMerger.mergeResults(resultSets, INPUTS);
        assertNotNull(merged);
        assertEquals(2, merged.size());
        ListAssert.assertContains(merged, docA);
        ArrayAssert.assertEquals(new String []
        {
            "a", "b"
        }, (String []) docA.getProperty(RawDocument.PROPERTY_SOURCES));
        ListAssert.assertContains(merged, docC);
    }

    public void testMergeByContent()
    {
        Map resultSets = new HashMap();

        List documentListA = new ArrayList();
        resultSets.put("a", documentListA);
        final RawDocumentSnippet docA = new RawDocumentSnippet("a", "aa", "aa",
            "a", 0.0f);
        documentListA.add(docA);

        List documentListB = new ArrayList();
        resultSets.put("b", documentListB);
        final RawDocumentSnippet docB = new RawDocumentSnippet("b", "aa", "aa",
            "b", 0.0f);
        documentListB.add(docB);

        List documentListC = new ArrayList();
        resultSets.put("c", documentListC);
        final RawDocumentSnippet docC = new RawDocumentSnippet("c", "c", "c",
            "c", 0.0f);
        documentListC.add(docC);

        List merged = resultsMerger.mergeResults(resultSets, INPUTS);
        assertNotNull(merged);
        assertEquals(2, merged.size());
        ListAssert.assertContains(merged, docA);
        ArrayAssert.assertEquals(new String []
        {
            "a", "b"
        }, (String []) docA.getProperty(RawDocument.PROPERTY_SOURCES));
        ListAssert.assertContains(merged, docC);
    }

    public void testMergeMany()
    {
        Map resultSets = new HashMap();

        List documentListA = new ArrayList();
        resultSets.put("a", documentListA);
        final RawDocumentSnippet docA = new RawDocumentSnippet("a", "aa", "aa",
            "a", 0.0f);
        documentListA.add(docA);

        List documentListB = new ArrayList();
        resultSets.put("b", documentListB);
        final RawDocumentSnippet docB = new RawDocumentSnippet("b", "b", "b",
            "a", 0.0f);
        documentListB.add(docB);

        List documentListC = new ArrayList();
        resultSets.put("c", documentListC);
        final RawDocumentSnippet docC = new RawDocumentSnippet("c", "c", "c",
            "a", 0.0f);
        documentListC.add(docC);

        List merged = resultsMerger.mergeResults(resultSets, INPUTS);
        assertNotNull(merged);
        assertEquals(1, merged.size());
        ListAssert.assertContains(merged, docA);
        ArrayAssert.assertEquals(new String []
        {
            "a", "b", "c"
        }, (String []) docA.getProperty(RawDocument.PROPERTY_SOURCES));
    }

    public void testRanking()
    {
        Map resultSets = new HashMap();

        List documentListA = new ArrayList();
        resultSets.put("a", documentListA);
        final RawDocumentSnippet docA = new RawDocumentSnippet("a", "aa", "aa",
            "a", 0.0f);
        documentListA.add(docA);

        List documentListB = new ArrayList();
        resultSets.put("b", documentListB);
        final RawDocumentSnippet docB = new RawDocumentSnippet("b", "b", "b",
            "a", 0.0f);
        final RawDocumentSnippet docD = new RawDocumentSnippet("d", "d", "d",
            "d", 0.0f);
        documentListB.add(docB);
        documentListB.add(docD);

        List documentListC = new ArrayList();
        resultSets.put("c", documentListC);
        final RawDocumentSnippet docC = new RawDocumentSnippet("c", "c", "c",
            "c", 0.0f);
        documentListC.add(docC);

        List merged = resultsMerger.mergeResults(resultSets, INPUTS);
        assertNotNull(merged);
        assertEquals(3, merged.size());
        assertEquals(0, merged.indexOf(docA));
        assertEquals(1, merged.indexOf(docC));
        assertEquals(2, merged.indexOf(docD));
    }
}
