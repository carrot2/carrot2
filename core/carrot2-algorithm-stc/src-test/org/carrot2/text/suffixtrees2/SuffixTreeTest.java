package org.carrot2.text.suffixtrees2;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

/**
 * Tests {@link SuffixTree}.
 */
public class SuffixTreeTest
{
    @Test
    public void testLeafNodesMississipi()
    {
        final String sequence = "mississippi";

        final List<String> expected = new ArrayList<String>(Arrays.asList(new String []
        {
            "mississippi", "ippi", "ississippi", "issippi", "ppi", "pi", "sissippi",
            "sippi", "ssissippi", "ssippi"
        }));

        assertLeafNodes(sequence, expected);
    }

    @Test
    public void testNodeIterator()
    {
        final String seq = "banana$";
        final SuffixTree<Node> t = SuffixTree.newSuffixTree();
        t.build(new CharacterSequence(seq));

        final List<String> expected = new ArrayList<String>(Arrays.asList(new String []
        {
            "leaf: true: banana$", "leaf: true: nana$", "leaf: true: na$",
            "leaf: false: na", "leaf: true: anana$", "leaf: true: ana$",
            "leaf: false: ana", "leaf: true: a$", "leaf: false: a", "leaf: true: $",
            "leaf: false: ",
        }));

        for (Node n : t)
        {
            assertNotNull(expected.remove("leaf: " + n.isLeaf() + ": "
                + seq.substring(n.getSuffixStartIndex(), n.getSuffixEndIndex() + 1)));
        }
        assertEquals(0, expected.size());
    }

    @Test
    public void testCounterNode()
    {
        final String seq = "banana$";
        final SuffixTree<CounterNode> t = SuffixTree
            .newSuffixTree(new CounterNodeFactory());
        t.build(new CharacterSequence(seq));
        CounterNode.leafCount(t);

        final List<String> expected = new ArrayList<String>(Arrays.asList(new String []
        {
            "na, count: 2", "ana, count: 2", "a, count: 3", ", count: 7",
        }));

        for (CounterNode n : t)
        {
            if (n.count == 1) continue;

            assertNotNull(expected.remove(seq.substring(n.getSuffixStartIndex(), n
                .getSuffixEndIndex() + 1)
                + ", count: " + n.count));
        }
        assertEquals(0, expected.size());
    }

    @Test
    public void testLeafNodesBanana()
    {
        final String sequence = "banana$";

        final List<String> expected = new ArrayList<String>(Arrays.asList(new String []
        {
            "banana$", "a$", "ana$", "anana$", "na$", "nana$", "$",
        }));

        assertLeafNodes(sequence, expected);
        assertAllSuffixes(new CharacterSequence(sequence));
    }

    @Ignore
    @Test
    public void testRandomInput()
    {
        final int [] randomData = generate(0x11223344, 1024 * 1024, 0xff);
        randomData[randomData.length - 1] = -1;

        final Sequence seq = new IntSequence(randomData);
        final SuffixTree<Node> t = SuffixTree.newSuffixTree();
        t.build(seq);
    }

    /*
     * 
     */
    private int [] generate(int seed, int size, int dictionarySize)
    {
        final Random rnd = new Random(seed);

        final int [] elements = new int [size];
        for (int i = 0; i < size; i++)
        {
            elements[i] = rnd.nextInt(dictionarySize);
        }

        return elements;
    }

    /**
     * 
     */
    private void assertAllSuffixes(Sequence sequence)
    {
        final SuffixTree<Node> t = SuffixTree.newSuffixTree();
        t.build(sequence);

        final int [] codes = new int [sequence.size()];
        for (int i = 0; i < codes.length; i++)
        {
            codes[i] = sequence.objectAt(i);
        }

        for (int i = 0; i < codes.length; i++)
        {
            final Sequence subsequence = new IntSequence(codes, i, codes.length - i);
            assertTrue(t.hasSuffix(subsequence));
        }
    }

    /**
     * 
     */
    private void assertLeafNodes(String sequence, List<String> expected)
    {
        final SuffixTree<Node> t = SuffixTree.newSuffixTree();
        t.build(new CharacterSequence(sequence));

        final List<String> actual = new ArrayList<String>();
        for (Node n : t)
        {
            if (!n.isLeaf())
            {
                continue;
            }

            actual.add(sequence.substring(n.getSuffixStartIndex(),
                n.getSuffixEndIndex() + 1));
        }

        Collections.sort(expected);
        Collections.sort(actual);

        Assert.assertEquals(expected, actual);
    }
}
