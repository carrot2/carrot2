
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

package org.carrot2.text.suffixtrees2;

import java.util.*;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link SuffixTree}.
 */
public class GeneralizedSuffixTreeTest
{
    @Test
    public void testLeafNodes()
    {
        final String sequence1 = "abcabc";
        final String sequence2 = "defabcabc";
        final String sequence3 = "deabcfdef";

        final GeneralizedSuffixTree<BitSetNode> gst = new GeneralizedSuffixTree<BitSetNode>(
            new BitSetNodeFactory());

        gst.build(new CharacterSequence(sequence1), new CharacterSequence(sequence2),
            new CharacterSequence(sequence3));

        final List<String> leaves = new ArrayList<String>();
        for (BitSetNode n : gst)
        {
            if (!n.isLeaf()) continue;

            final ISequence s = gst.getSequenceToRoot(n);
            leaves.add(SequenceFormatter.asString(s, CharacterSequence.FORMATTER));
        }

        final String [] sequences =
        {
            sequence1, sequence2, sequence3
        };

        final List<String> correct = new ArrayList<String>();
        for (int i = 0; i < sequences.length; i++) 
        {
            final String s = sequences[i];
            for (int j = 0; j <= s.length(); j++)
            {
                final String leaf = s.substring(j) + "$" + i;
                correct.add(leaf);
            }
        }

        Collections.sort(leaves);
        Collections.sort(correct);
        
        Assert.assertEquals(correct, leaves);
    }
}
