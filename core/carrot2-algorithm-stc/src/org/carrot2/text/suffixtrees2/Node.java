package org.carrot2.text.suffixtrees2;

import java.util.*;

import org.carrot2.text.suffixtrees.SuffixableElement;

/**
 * A node in a {@link SuffixTree}.
 */
public class Node<T>
{
    /** This node's owner tree. */
    protected final SuffixTree<T> container;

    /** Pointer to the next smaller suffix. */
    Node<T> suffixNode;

    /**
     * The edge from parent node to this node. This is to speed up later management of
     * this suffix tree.
     */
    private Edge<T> edgeToParent;

    /**
     * First outgoing edge from this node or <code>null</code> if none.
     */
    private NodeEdge firstEdge;

    /**
     * Every node knows its container tree.
     */
    protected Node(SuffixTree<T> container)
    {
        this.container = container;
    }

    /**
     * Returns an iterator object of edges leaving this node.
     */
    public Iterator<Edge<T>> getEdgesIterator()
    {
        if (firstEdge == null)
        {
            final List<Edge<T>> l = Collections.emptyList();
            return l.iterator();
        }

        return new Iterator<Edge<T>>()
        {
            NodeEdge current = firstEdge;

            public boolean hasNext()
            {
                return current != null;
            }

            public Edge<T> next()
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
     * Returns an Edge object, which matches the passed argument. The passed argument must
     * be valid part of some SuffixableElement, e.g., must implement equals method. Method
     * returns null if no matching edge has been found.
     */
    public Edge<T> findEdgeMatchingFirstElement(Object key)
    {
        return this.container.getEdge(this, key);
    }

    /**
     * Finds an edge <b>entirely</b> matching the sequence of objects at some index.
     * E.g., if the {@link Edge} holds three elements, the three of them are matched
     * against elements of {@link SuffixableElement} at
     * <code>index, index+1 and index+3</code>. The method returns <code>null</code>
     * if matching failed.
     */
    public Edge<T> findEdgeMatchingEntirely(List<T> t, int startIndex)
    {
        final Edge<T> matchingEdge = findEdgeMatchingFirstElement(t.get(startIndex));
        if (matchingEdge != null)
        {
            /*
             * Calculate the number of elements to compare. if there are less elements in
             * t than in the edge, it may be possible that suffixableElement ends
             * somewhere along the compressed path (is a prefix of the suffix represented
             * by this path).
             */
            final int maxLength = Math.min(matchingEdge.length(), t.size() - startIndex);

            // the first element matches, check all the remaining ones
            for (int i = 1; i < maxLength; i++)
            {
                startIndex++;

                if (container.sequence[matchingEdge.getStartIndex() + i].equals(t
                    .get(startIndex)) == false)
                {
                    // This path doesn't entirely match.
                    return null;
                }
            }

            // are we along the compressed path?
            if (matchingEdge.getEndIndex() > matchingEdge.getStartIndex() + maxLength - 1)
            {
                /*
                 * We are along compressed path. Return null because we need entirely
                 * matching edge.
                 */
                return null;
            }
        }

        return matchingEdge;
    }

    /**
     * Creates a child note and returns an edge to it.
     */
    protected Edge<T> createChildNode(int firstElement, int lastElement)
    {
        final Node<T> child = container.nodeFactory.createNode(container);
        final Edge<T> link = new Edge<T>(firstElement, lastElement, this, child);

        child.edgeToParent = link;
        this.addEdge(link);

        return link;
    }

    /**
     * Returns the index of the start of this node's suffix (substring actually). To
     * obtain a full path from root to this node, traverse SuffixableElement like below:
     * 
     * <pre>
     * SuffixableElement p = node.getSuffixableElement();
     * for (int i = node.getSuffixStartIndex(); i &lt;= node.getSuffixEndIndex(); i++)
     * {
     *     System.out.print(p.get(i));
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
        for (Edge<T> ei = edgeToParent; ei.getStartNode().edgeToParent != null;)
        {
            ei = ei.getStartNode().edgeToParent;
            start -= ei.length();
        }

        return start;
    }

    /**
     * Returns the index of the last element of this node's suffix. To obtain a full path
     * from root to this node, traverse SuffixableElement like below:
     * 
     * <pre>
     * SuffixableElement p = node.getSuffixableElement();
     * for (int i = node.getSuffixStartIndex(); i &lt;= node.getSuffixEndIndex(); i++)
     * {
     *     System.out.print(p.get(i));
     * }
     * </pre>
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
     * Returns true if this {@link Node} is a leaf node (has no outgoing edges)
     */
    public boolean isLeaf()
    {
        return firstEdge == null;
    }

    /**
     * Adds an edge to this node.
     */
    protected void addEdge(Edge<T> edge)
    {
        final NodeEdge newEdge = new NodeEdge(this,
            container.sequence[edge.firstElementIndex]);
        container.edges.put(newEdge, edge);

        newEdge.next = this.firstEdge;
        this.firstEdge = newEdge;
    }

    /**
     * Removes an edge from this node.
     */
    protected void removeEdge(Edge<T> edge)
    {
        final NodeEdge nodeEdge = container.removeEdge(this,
            container.sequence[edge.firstElementIndex]);

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
