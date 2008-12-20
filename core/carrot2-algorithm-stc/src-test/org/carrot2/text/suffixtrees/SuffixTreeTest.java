
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

package org.carrot2.text.suffixtrees;

import java.util.*;

import org.junit.*;

import com.google.common.collect.Iterators;

/**
 * Tests {@link SuffixTree}.
 */
public class SuffixTreeTest
{
    /**
     * Test if all the leaf nodes are in place.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testLeafNodes()
    {
        final ISuffixableElement sequence = new SuffixableCharacterSequence("mississippi");
        final SuffixTree t = new SuffixTree();
        t.add(sequence);

        final List<String> actual = new ArrayList<String>();
        final Stack<Node> nodes = new Stack<Node>();
        nodes.push(t.getRootNode());
        while (!nodes.isEmpty())
        {
            final Node n = nodes.pop();

            for (Edge e : Iterators.newArray(n.getEdgesIterator(), Edge.class))
            {
                if (e.endNode.isLeaf())
                {
                    List<Object> p = e.endNode.getPhrase();
                    actual.add(toString(p));
                }
                else 
                {
                    nodes.push(e.endNode);
                }
            }
        }

        final List<String> expected = new ArrayList<String>(Arrays.asList(
            new String [] {
                "mississippi",
                "ippi",
                "ississippi",
                "issippi",
                "ppi",
                "pi",
                "sissippi",
                "sippi",
                "ssissippi",
                "ssippi",
            }));

        Collections.sort(expected);
        Collections.sort(actual);

        Assert.assertEquals(expected, actual);
    }

    /**
     * Appends all elements of a list to a single string.
     */
    private String toString(List<?> p)
    {
        final StringBuilder b = new StringBuilder();
        for (Object o : p)
        {
            b.append(o.toString());
        }
        return b.toString();
    }
}
