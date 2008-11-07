
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

package org.carrot2.text.suffixtrees;

/**
 * <p>
 * An edge between two nodes in the SuffixTree.
 * <p>
 * A single edge may represent more than one element of SuffixableElement collection
 * (otherwise it would be a trie not a suffix tree).
 */
public final class Edge
{
    /** Index of the collection's element from which this edge starts. */
    protected int firstElementIndex;

    /** Index at which the edge ends (inclusive). */
    protected final int lastElementIndex;

    /** The node from which the Edge starts */
    protected Node startNode;

    /** The node where this Edge ends */
    protected final Node endNode;

    /** 
     * 
     */
    protected Edge(int firstElementIndex, int lastElementIndex, Node startNode,
        Node endNode)
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
    protected Node splitEdge(Suffix s)
    {
        startNode.removeEdge(this);

        final Edge newEdge = s.originNode.createChildNode(s.firstElementIndex,
            s.lastElementIndex);

        newEdge.endNode.suffixNode = s.originNode;

        // Sanity check.
        if (newEdge.endNode.suffixNode == null) throw new RuntimeException();

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
     * <code>{@link {#link getEndIndex()} - {@link #getStartIndex()} + 1</code>.
     */
    public int length()
    {
        return getEndIndex() - getStartIndex() + 1;
    }
}
