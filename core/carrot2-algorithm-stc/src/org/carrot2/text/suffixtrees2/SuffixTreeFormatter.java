package org.carrot2.text.suffixtrees2;

import java.util.IdentityHashMap;

/**
 * Utilities for converting {@Link SuffixTree}s to textual formats.
 */
public class SuffixTreeFormatter
{
    /**
     * Convert to GraphViz's DOT language.
     */
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

                if (tree instanceof GeneralizedSuffixTree<?>)
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
