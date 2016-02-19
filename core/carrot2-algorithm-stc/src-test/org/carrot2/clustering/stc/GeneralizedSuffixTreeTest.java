
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

package org.carrot2.clustering.stc;

import java.util.ArrayList;
import java.util.Collections;

import org.carrot2.clustering.stc.GeneralizedSuffixTree.SequenceBuilder;
import org.carrot2.text.suffixtree.SuffixTree;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

import com.carrotsearch.hppc.BitSet;
import com.carrotsearch.hppc.IntStack;

/**
 * Tests running GST-functionality on top of a {@link SuffixTree}. This is for demonstration
 * purposes mostly.
 */
public class GeneralizedSuffixTreeTest extends CarrotTestCase
{
    static class TestGST extends GeneralizedSuffixTree.Visitor
    {
        public final ArrayList<String> nodes = new ArrayList<String>();

        public TestGST(SequenceBuilder sb)
        {
            super(sb, 2);
        }

        protected void visit(int state, int card, BitSet bset, IntStack edges)
        {
            final StringBuilder b = new StringBuilder();
            for (int i = 0; i < edges.size(); i += 2)
                for (int j = edges.get(i); j <= edges.get(i + 1); j++)
                    b.append(sb.input.get(j) + " ");

            nodes.add(b.toString() + "[" + card + "]");
        }
    }
    
    @Test
    public void testMultiphraseGST()
    {
        final SequenceBuilder sb = new SequenceBuilder();
        sb.addPhrase(0, 1, 2, 3);
        sb.addPhrase(0, 1, 2, 3);
        sb.endDocument();
        sb.addPhrase(4, 1, 2, 3);
        sb.endDocument();
        sb.addPhrase(4, 2, 3, 5);
        sb.endDocument();

        sb.buildSuffixTree();

        TestGST gst = new TestGST(sb);
        gst.visit();

        Collections.sort(gst.nodes);
        assertArrayEquals(new Object [] {
            "1 2 3 [2]",
            "2 3 [3]",
            "3 [3]",
            "4 [2]",
        }, gst.nodes.toArray());        
    }

    /**
     * 
     */
    @Test
    public void testSinglephraseGST()
    {
        final SequenceBuilder sb = new SequenceBuilder();
        sb.addPhrase(0, 1, 2, 3);
        sb.endDocument();
        sb.addPhrase(0, 1, 2, 3);
        sb.endDocument();
        sb.addPhrase(4, 1, 2, 3);
        sb.endDocument();
        sb.addPhrase(4, 2, 3, 5);
        sb.endDocument();

        sb.buildSuffixTree();

        TestGST gst = new TestGST(sb);
        gst.visit();

        Collections.sort(gst.nodes);
        assertArrayEquals(new Object [] {
            "0 1 2 3 [2]",
            "1 2 3 [3]",
            "2 3 [4]",
            "3 [4]",
            "4 [2]",
        }, gst.nodes.toArray());        
    }

    /**
     * 
     */
    @Test
    public void testEmptyGST()
    {
        final SequenceBuilder sb = new SequenceBuilder();
        sb.endDocument();

        sb.buildSuffixTree();

        TestGST gst = new TestGST(sb);
        gst.visit();
    }
}
