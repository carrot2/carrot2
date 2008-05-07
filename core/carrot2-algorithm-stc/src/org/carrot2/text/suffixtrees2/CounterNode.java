package org.carrot2.text.suffixtrees2;

/**
 * A {@link Node} with an associated counter.
 */
public class CounterNode extends Node
{
    public int count;

    protected CounterNode(SuffixTree container)
    {
        super(container);
    }

    /**
     * Calculate the number of leaves reachable from each internal node.
     */
    public static void leafCount(SuffixTree tree)
    {
        for (Node n : tree)
        {
            final CounterNode me = (CounterNode) n;

            if (n.isLeaf())
            {
                me.count = 1;
            }

            CounterNode parent = ((CounterNode) n.getParentNode());
            if (parent != null)
            {
                parent.count += me.count;
            }
        }
    }
}
