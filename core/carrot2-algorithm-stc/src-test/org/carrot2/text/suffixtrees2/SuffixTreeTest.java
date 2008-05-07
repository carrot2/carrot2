package org.carrot2.text.suffixtrees2;

import java.util.*;

import org.junit.Assert;
import org.junit.Test;

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
        final List<Character> sequence = asCharacterList("mississippi");
        final SuffixTree t = new SuffixTree();
        final Node rootNode = t.build(sequence);

        final List<String> actual = new ArrayList<String>();
        final Stack<Node> nodes = new Stack<Node>();
        nodes.push(rootNode);
        while (!nodes.isEmpty())
        {
            final Node n = nodes.pop();
            final Iterator<Edge> i = n.getEdgesIterator();
            
            while (i.hasNext())
            {
                final Edge e = i.next();
                if (e.endNode.isLeaf())
                {
                    List<Character> p = sequence.subList(e.endNode.getSuffixStartIndex(), e.endNode.getSuffixEndIndex() + 1);
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

    /*
     * 
     */
    private List<Character> asCharacterList(String string)
    {
        final char [] characters = string.toCharArray();
        final Character [] charList = new Character [characters.length];
        
        for (int i = 0; i < characters.length; i++)
        {
            charList[i] = characters[i];
        }

        return Arrays.asList(charList);
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
