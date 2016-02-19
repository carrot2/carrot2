
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

package org.carrot2.text.suffixtree;

import java.util.ArrayList;
import java.util.Collections;

import org.carrot2.text.suffixtree.SuffixTree.VisitorAdapter;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

import com.carrotsearch.hppc.IntArrayList;

/**
 * Sanity and validation tests for the {@link SuffixTree} class.
 */
public class SuffixTreeTest extends CarrotTestCase
{
    @Test
    public void checkMississipi()
    {
        checkAllSuffixes("mississippi$");
    }

    @Test
    public void checkBanana()
    {
        checkAllSuffixes("banana$");
    }

    @Test
    public void checkCocoa()
    {
        checkAllSuffixes("cocoa$");
    }

    @Test
    public void checkTransitionsCount()
    {
        final SuffixTree st = checkAllSuffixes("cocoa$");
        assertEquals(8, st.getTransitionsCount());
    }

    @Test
    public void checkStatesCount()
    {
        final SuffixTree st = checkAllSuffixes("cocoa$");
        assertEquals(9, st.getStatesCount());
    }

    @Test
    public void checkRandomSymbols()
    {
        final int [] input = new int [scaledRandomIntBetween(1, 25000)];
        for (int i = 0; i < input.length; i++)
        {
            input[i] = randomInt(100);
        }
        input[input.length - 1] = Integer.MAX_VALUE;

        final SuffixTree stree = SuffixTreeBuilder.from(new ISequence() {
            public int objectAt(int i)
            {
                return input[i];
            }

            public int size()
            {
                return input.length;
            }
        }).build();

        for (int i = 0; i < Math.min(5000, input.length); i++)
        {
            stree.containsSuffix(new IntegerSequence(input, i, input.length - i));
        }
    }

    @Test
    public void testContainsSuffix()
    {
        final SuffixTree stree = SuffixTreeBuilder.from(new CharacterSequence("cocoa$")).build();

        assertFalse(stree.containsSuffix(new CharacterSequence("c")));
        assertFalse(stree.containsSuffix(new CharacterSequence("co")));
        assertFalse(stree.containsSuffix(new CharacterSequence("coc")));
        assertFalse(stree.containsSuffix(new CharacterSequence("coco")));
        assertFalse(stree.containsSuffix(new CharacterSequence("cocoa")));

        assertFalse(stree.containsSuffix(new CharacterSequence("cx")));
        assertFalse(stree.containsSuffix(new CharacterSequence("cox")));
        assertFalse(stree.containsSuffix(new CharacterSequence("cocx")));
        assertFalse(stree.containsSuffix(new CharacterSequence("cocox")));
        assertFalse(stree.containsSuffix(new CharacterSequence("cocoax")));
        assertFalse(stree.containsSuffix(new CharacterSequence("cocoa$x")));

        assertFalse(stree.containsSuffix(new CharacterSequence("x")));
    }

    @Test
    public void testTreeVisitor()
    {
        final SuffixTree stree = SuffixTreeBuilder.from(new CharacterSequence("cocoa$")).build();

        class CountingVisitor extends SuffixTree.VisitorAdapter {
            int states, edges;

            public void post(int state)
            {
                states++;
            }

            public boolean edge(int fromNode, int toNode, int startIndex, int endIndex)
            {
                edges++;
                return true;
            }
        };

        final CountingVisitor v = new CountingVisitor();
        stree.visit(v);
        assertEquals(stree.getStatesCount(), v.states);
        assertEquals(stree.getTransitionsCount(), v.edges);
    }

    @Test
    public void testInternalNodes()
    {
        final ArrayList<String> nodes = new ArrayList<String>();
        final CharacterSequence seq = new CharacterSequence("cocoa$");
        final SuffixTree stree = SuffixTreeBuilder.from(seq).build();

        stree.visit(new VisitorAdapter()
        {
            final IntArrayList states = new IntArrayList();

            public void post(int state)
            {
                if (stree.getRootState() != state)
                {
                    final StringBuilder buffer = new StringBuilder();
                    for (int i = 0; i < states.size(); i += 2)
                        for (int j = states.get(i); j <= states.get(i + 1); j++)
                            buffer.append((char) seq.objectAt(j));

                    if (stree.isLeaf(state)) buffer.append(" [leaf]");
                    nodes.add(buffer.toString());

                    states.remove(states.size() - 1);
                    states.remove(states.size() - 1);
                }
            };

            public boolean edge(int fromState, int toState, int startIndex, int endIndex)
            {
                states.add(startIndex);
                states.add(endIndex);
                return true;
            }
        });

        Collections.sort(nodes);
        assertArrayEquals(new Object [] {
            "$ [leaf]",
            "a$ [leaf]",
            "co",
            "coa$ [leaf]",
            "cocoa$ [leaf]",
            "o",
            "oa$ [leaf]",
            "ocoa$ [leaf]",
        }, nodes.toArray());
    }

    /**
     * Build a suffix tree for a given sequence and check if it contains all suffixes of
     * the input sequence (ending in leaves).
     */
    private SuffixTree checkAllSuffixes(String word)
    {
        final SuffixTree stree = SuffixTreeBuilder.from(new CharacterSequence(word)).build();

        // Check all suffixes are in the suffix tree.
        for (int i = 0; i < word.length(); i++)
        {
            assertTrue(stree.containsSuffix(new CharacterSequence(word.substring(i))));
        }

        // Check that all infixes are not in the suffix set.
        for (int i = 0; i < word.length() - 1; i++)
        {
            assertFalse(stree.containsSuffix(
                new CharacterSequence(word.substring(i, word.length() - 1))));
        }
        
        return stree;
    }
}
