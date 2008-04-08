package org.carrot2.text.suffixtrees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.junit.Assert;
import org.junit.Test;

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
        final SuffixableElement sequence = new SuffixableCharacterSequence("mississippi");
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

        final List<String> expected = Arrays.asList(
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
            });

        Assert.assertEquals(expected, actual);
    }

    /**
     * Appends all elements of a list to a single string.
     */
    private String toString(List p)
    {
        final StringBuilder b = new StringBuilder();
        for (Object o : p)
        {
            b.append(o.toString());
        }
        return b.toString();
    }
}
