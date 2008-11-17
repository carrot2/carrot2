
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

import java.util.IdentityHashMap;

/**
 * Utilities for converting {@link SuffixTree}s to textual formats.
 */
public class SuffixTreeFormatter
{
    /**
     * Convert to GraphViz's DOT language.
     */
    @SuppressWarnings("unchecked")
    public String toDot(SuffixTree<Node> tree)
    {
        StringBuilder gv = new StringBuilder();
        IdentityHashMap<Node, String> ids = new IdentityHashMap<Node, String>();

        gv.append("digraph gst {\n" + " ");

        for (Node n : tree)
        {
            if (!ids.containsKey(n))
            {
                final String image = SequenceFormatter.asString(tree
                    .getSequenceToParent(n), CharacterSequence.FORMATTER);

                ids.put(n, "node" + ids.size());

                if (tree instanceof GeneralizedSuffixTree)
                {
                    BitSetNode bn = (BitSetNode) n;
                    final int docs = bn.bitset.cardinality();
                    final int subnodes = bn.count;

                    gv.append(ids.get(n) + " [" + "shape=" + (n.isLeaf() ? "box" : "circle")
                        + ",label=\"" + image
                        + (n.isLeaf() ? "" : "\\ncard=" + docs + "\\nleaves=" + subnodes)
                        + "\"" + " ];\n");
                }
                else
                {
                    gv.append(ids.get(n) + " [" + "shape=" + (n.isLeaf() ? "box" : "circle")
                        + ",label=\"" + image + "\"" + " ];\n");
                }
            }
        }

        for (Node n : tree)
        {
            if (n.getParentNode() == null)
            {
                continue;
            }

            final String id = ids.get(n);
            final String parentId = ids.get(n.getParentNode());
            gv.append(parentId + " -> " + id + " ;\n");
        }

        gv.append("}\n");

        return gv.toString();
    }
}
