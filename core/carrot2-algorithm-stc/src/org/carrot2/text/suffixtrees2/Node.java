package org.carrot2.text.suffixtrees2;

import java.util.*;

/**
 * A node in a {@link SuffixTree}.
 */
public class Node implements Iterable<Edge>
{
    /** This node's container tree. */
    protected final SuffixTree container;

    /** Pointer to the next smaller suffix. */
    Node suffixNode;

    /**
     * The edge from parent node to this node. This is to speed up later management of
     * this suffix tree.
     */
    Edge edgeToParent;

    /**
     * First outgoing edge from this node or <code>null</code> if none.
     */
    private NodeEdge firstEdge;

    /**
     * 
     */
    protected Node(SuffixTree container)
    {
        this.container = container;
    }

    /**
     * Returns an iterator over the edges leaving this node.
     */
    public final Iterator<Edge> getEdges()
    {
        if (firstEdge == null)
        {
            final List<Edge> l = Collections.emptyList();
            return l.iterator();
        }

        return new Iterator<Edge>()
        {
            NodeEdge current = firstEdge;

            public boolean hasNext()
            {
                return current != null;
            }

            public Edge next()
            {
                final NodeEdge result = current;
                current = result.next;
                return container.edges.get(result);
            }

            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Returns an {@link Edge} object, which matches the element code at index
     * <code>index</code>. Method returns null if no matching edge has been found.
     */
    public final Edge getEdge(int objectCode)
    {
        return this.container.getEdge(this, objectCode);
    }

    /**
     * Returns the start index of this node's suffix. Full path from root to this node:
     * 
     * <pre>
     * for (int i = node.getSuffixStartIndex(); i &lt;= node.getSuffixEndIndex(); i++)
     * {
     *     System.out.print(inputSequence.get(i));
     * }
     * </pre>
     */
    public int getSuffixStartIndex()
    {
        if (edgeToParent == null)
        {
            return 0;
        }

        int start = edgeToParent.getStartIndex();
        for (Edge ei = edgeToParent; ei.getStartNode().edgeToParent != null;)
        {
            ei = ei.getStartNode().edgeToParent;
            start -= ei.length();
        }

        return start;
    }

    /**
     * Returns the index of the last element of this node's suffix (inclusive).
     * 
     * @see #getSuffixStartIndex()
     */
    public int getSuffixEndIndex()
    {
        if (edgeToParent == null)
        {
            return -1;
        }

        return this.edgeToParent.getEndIndex();
    }

    /**
     * Returns true if this {@link Node} is a leaf node (has no outgoing edges).
     */
    public boolean isLeaf()
    {
        return firstEdge == null;
    }

    /**
     * 
     */
    public Iterator<Edge> iterator()
    {
        return getEdges();
    }

    /**
     * @return Parent node or <code>null</code> if there is no parent node.
     */
    public final Node getParentNode()
    {
        if (this.edgeToParent == null)
            return null;

        return edgeToParent.startNode;
    }

    /**
     * Creates a child node and returns an edge to it.
     */
    Edge createChildNode(int firstElement, int lastElement)
    {
        final Node child = container.createNode();
        final Edge link = new Edge(firstElement, lastElement, this, child);

        child.edgeToParent = link;
        this.addEdge(link);

        return link;
    }

    /**
     * Adds an edge to this node.
     */
    void addEdge(Edge edge)
    {
        final NodeEdge newEdge = new NodeEdge(this, container.input
            .objectAt(edge.firstElementIndex));
        container.edges.put(newEdge, edge);

        newEdge.next = this.firstEdge;
        this.firstEdge = newEdge;
    }

    /**
     * Removes an edge from this node.
     */
    void removeEdge(Edge edge)
    {
        final NodeEdge nodeEdge = container.removeEdge(this, container.input
            .objectAt(edge.firstElementIndex));

        if (nodeEdge.equals(this.firstEdge))
        {
            this.firstEdge = this.firstEdge.next;
        }
        else
        {
            NodeEdge c = this.firstEdge;
            while (!c.next.equals(nodeEdge))
            {
                c = c.next;
            }
            c.next = c.next.next;
        }
    }
}
