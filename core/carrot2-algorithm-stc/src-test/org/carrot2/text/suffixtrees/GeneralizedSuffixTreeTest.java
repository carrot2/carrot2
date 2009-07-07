/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.suffixtrees;

import static org.junit.Assert.*;
import java.util.*;

import org.carrot2.clustering.stc.PhraseNode;
import org.carrot2.clustering.stc.STCTree;
import org.junit.Test;

import com.google.common.collect.Iterators;

/**
 * Tests internal document counts inside {@link GeneralizedSuffixTree}.
 */
public class GeneralizedSuffixTreeTest
{
    @Test
    public void testInternalDocs()
    {
        STCTree gst = new STCTree();

        /*
         * Document 1 (two identical sentences).
         */
        gst.add(new SuffixableTermSequence(new Object []
        {
            "cat", "ate", "cheese", "too", ISuffixableElement.END_OF_SUFFIX
        }));
        gst.add(new SuffixableTermSequence(new Object []
        {
            "cat", "ate", "cheese", "too", ISuffixableElement.END_OF_SUFFIX
        }));

        /*
         * Document 2.
         */
        gst.nextDocument();
        gst.add(new SuffixableTermSequence(new Object []
        {
            "mouse", "ate", "cheese", "too", ISuffixableElement.END_OF_SUFFIX
        }));

        /*
         * Document 3.
         */
        gst.nextDocument();
        gst.add(new SuffixableTermSequence(new Object []
        {
            "mouse", "cheese", "too", "boo", ISuffixableElement.END_OF_SUFFIX
        }));

        /*
         * Collect node counts.
         */
        final HashSet<String> asText = new HashSet<String>();
        final Stack<Node> nodes = new Stack<Node>();
        nodes.push(gst.getRootNode());
        while (!nodes.isEmpty())
        {
            final Node n = nodes.pop();

            for (Edge e : Iterators.toArray(n.getEdgesIterator(), Edge.class))
            {
                if (e.endNode.isLeaf())
                {
                    PhraseNode pn = (PhraseNode) e.endNode;
                    List<Object> p = pn.getPhrase();
                    asText.add(Arrays.toString(p.toArray()) + " "
                        + pn.getSuffixedDocumentsCount());
                }
                else
                {
                    PhraseNode pn = (PhraseNode) e.endNode;
                    List<Object> p = pn.getPhrase();
                    asText.add(Arrays.toString(p.toArray()) + " "
                        + pn.getSuffixedDocumentsCount() + " [I]");

                    nodes.push(e.endNode);
                }
            }
        }

        // Single end-of-sentence "too" in document 1 and 2.
        assertTrue(asText.contains("[too] 2"));
        // Three documents with "too" in general (internal node).
        assertTrue(asText.contains("[too] 3 [I]"));

        // Remaining cases.
        assertTrue(asText.contains("[cheese, too] 3 [I]"));
        assertTrue(asText.contains("[ate, cheese, too] 2"));
        assertTrue(asText.contains("[cheese, too, boo] 1"));
        assertTrue(asText.contains("[cat, ate, cheese, too] 1"));
        assertTrue(asText.contains("[mouse] 2 [I]"));
        assertTrue(asText.contains("[mouse, ate, cheese, too] 1"));
        assertTrue(asText.contains("[mouse, cheese, too, boo] 1"));
        assertTrue(asText.contains("[cheese, too] 2"));
        assertTrue(asText.contains("[boo] 1"));
        assertTrue(asText.contains("[too, boo] 1"));
    }
}
