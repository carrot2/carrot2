package org.carrot2.text.suffixtrees2;

import org.junit.Test;

/**
 * Tests {@link SuffixTree}.
 */
public class GeneralizedSuffixTreeTest
{
    @Test
    public void testNodeCount()
    {
        final String sequence1 = "abcabc";
        final String sequence2 = "defabcabc";

        final GeneralizedSuffixTree<BitSetNode> gst = new GeneralizedSuffixTree<BitSetNode>(
            new BitSetNodeFactory());

        gst.build(new CharacterSequence(sequence1), new CharacterSequence(sequence2));

        for (BitSetNode n : gst)
        {
            System.out.println("leaf: "
                + n.isLeaf()
                + ", card: "
                + n.bitset.cardinality()
                + ", count: "
                + n.count);
        }
    }
}
