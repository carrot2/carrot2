
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
 * A suffix object used internally in class {@link SuffixTree}.
 */
final class Suffix
{
    /** first collection element where this suffix starts */
    protected int firstElementIndex;

    /** the collection element where this suffix ends (inclusive) */
    protected int lastElementIndex;

    /** Origin node needed for canonization operation. */
    protected Node originNode;

    /**
     * 
     */
    public Suffix(Node originNode, int firstElementIndex, int lastElementIndex)
    {
        this.originNode = originNode;
        this.firstElementIndex = firstElementIndex;
        this.lastElementIndex = lastElementIndex;
    }

    /**
     * Returns <code>true</code> when this suffix ends at an explicit node.
     */
    public boolean isExplicit()
    {
        return (firstElementIndex > lastElementIndex);
    }

    /**
     * Returns <code>true</code> when this suffix ends in an implicit node (node not
     * ending on the branch of the tree).
     */
    public boolean isImplicit()
    {
        return (firstElementIndex <= lastElementIndex);
    }

    /**
     * Performs canonization of this suffix. We slide down the tree until we reach the
     * closest node to the end of the collection.
     */
    public void canonize()
    {
        if (!isExplicit())
        {
            final Sequence input = originNode.container.input;

            Edge edge = originNode.getEdge(input.objectAt(firstElementIndex));
            int edgeSpan = edge.lastElementIndex - edge.firstElementIndex;

            while (edgeSpan <= (lastElementIndex - firstElementIndex))
            {
                firstElementIndex = firstElementIndex + edgeSpan + 1;
                originNode = edge.endNode;

                if (firstElementIndex <= lastElementIndex)
                {
                    edge = originNode.getEdge(input.objectAt(firstElementIndex));
                    edgeSpan = edge.lastElementIndex - edge.firstElementIndex;
                }
            }
        }
    }
}
