
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
 * An edge between two {@link Node}s in a {@link SuffixTree}.
 */
public final class Edge
{
    /** Edge's start index. */
    protected int firstElementIndex;

    /** Edge's end index (inclusive). */
    protected int lastElementIndex;

    /** A {@link Node} from which the edge starts. */
    protected Node startNode;

    /** A {@link Node} where this edge ends */
    protected Node endNode;

    /** 
     * 
     */
    Edge(int firstElementIndex, int lastElementIndex, Node startNode, Node endNode)
    {
        this.firstElementIndex = firstElementIndex;
        this.lastElementIndex = lastElementIndex;
        this.startNode = startNode;
        this.endNode = endNode;
    }

    /**
     * This method splits an Edge on a given suffix, creates a new {@link Node} and makes
     * a fork in the tree.
     */
    final Node splitEdge(Suffix s)
    {
        startNode.removeEdge(this);

        final Edge newEdge = s.originNode.createChildNode(s.firstElementIndex,
            s.lastElementIndex);

        newEdge.endNode.suffixNode = s.originNode;

        this.firstElementIndex += s.lastElementIndex - s.firstElementIndex + 1;
        startNode = newEdge.endNode;
        startNode.addEdge(this);

        return newEdge.endNode;
    }

    /** Returns the start node of this edge. */
    public Node getStartNode()
    {
        return this.startNode;
    }

    /** Returns the end node of this edge. */
    public Node getEndNode()
    {
        return this.endNode;
    }

    /** Returns the starting element index. */
    public int getStartIndex()
    {
        return this.firstElementIndex;
    }

    /** Returns the ending element index. */
    public int getEndIndex()
    {
        return this.lastElementIndex;
    }

    /**
     * Returns the length of this edge, meaning the number of compressed nodes this edge
     * represents. Equals
     * <code>{@link #getEndIndex()} - {@link #getStartIndex()} + 1</code>.
     */
    public int length()
    {
        return this.lastElementIndex - this.firstElementIndex + 1;
    }
}
