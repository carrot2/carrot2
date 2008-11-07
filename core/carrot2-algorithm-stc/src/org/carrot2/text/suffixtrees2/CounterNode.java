
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

/**
 * A {@link Node} with an associated counter.
 */
public class CounterNode extends Node
{
    public int count;

    protected CounterNode(SuffixTree<?> container)
    {
        super(container);
    }

    /**
     * Calculate the number of leaves reachable from each internal node.
     */
    @SuppressWarnings("unchecked")
    public static <T extends CounterNode> void leafCount(SuffixTree<T> tree)
    {
        for (T n : tree)
        {
            if (n.isLeaf())
            {
                n.count = 1;
            }

            T parent = (T) n.getParentNode();
            if (parent != null)
            {
                parent.count += n.count;
            }
        }
    }
}
