package org.carrot2.text.suffixtrees;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A node in a {@link SuffixTree}.
 */
public class Node
{
    /**
     * Edges leaving this node. Please note that this way of storing edges may reduce
     * performance of the algorithm. This is a generic implementation, if faster storage
     * is needed, subclass this class.
     */
    public final HashMap<Object, Edge> edges = new HashMap<Object, Edge>();

    /** This node's owner tree */
    protected final SuffixTree container;

    /** Pointer to the next smaller suffix. */
    protected Node suffixNode;

    /** For informational reasons only */
    public Node getSuffixNode()
    {
        return suffixNode;
    }

    /**
     * The edge from parent node to this node. This is to speed up later management of
     * this suffix tree.
     */
    protected Edge edgeToParent;

    /**
     * Phrase collection returned by getPhrase method.
     */
    public final class Phrase extends AbstractList<Object>
    {
        final int start;
        final int end;

        public Phrase()
        {
            start = Node.this.getSuffixStartIndex();

            int end = Node.this.getSuffixEndIndex();
            if (Node.this.getSuffixableElement().get(end) == SuffixableElement.END_OF_SUFFIX)
            {
                // skip EOS marker
                end--;
            }
            this.end = end;
        }

        public int size()
        {
            return end - start + 1;
        }

        public Object get(int index)
        {
            return Node.this.getSuffixableElement().get(index + start);
        }
    }

    /**
     * Every node knows its container tree. Nobody except subclasses and package-wide
     * classes can instantiate a Node.
     */
    protected Node(SuffixTree container)
    {
        this.container = container;
    }

    /**
     * Finds an edge starting in this node and labeled with the particular element from
     * current container's {@link SuffixableElement}. <code>null</code> is returned if
     * no edge has been found.
     */
    protected Edge findEdgeMatchingFirstElement(int index)
    {
        return findEdgeMatchingFirstElement(container.getCurrentElement().get(index));
    }

    /**
     * Returns an iterator object of edges leaving this node.
     */
    public Iterator<Edge> getEdgesIterator()
    {
        return this.edges.values().iterator();
    }

    /**
     * Returns an Edge object, which matches passed argument. The passed argument must be
     * valid part of some SuffixableElement, e.g. must implement equals method. Method
     * returns null if no matching edge has been found.
     */
    public Edge findEdgeMatchingFirstElement(Object key)
    {
        return (Edge) edges.get(key);
    }

    /**
     * Finds an edge <b>entirely </b> matching the SuffixableElement at some index. E.g.,
     * if the {@link Edge} holds three elements, the three of them are matched against
     * elements of {@link SuffixableElement} at <code>index, index+1 and index+3</code>.
     * The method returns <code>null</code> if matching failed.
     */
    public Edge findEdgeMatchingEntirely(SuffixableElement t, int startIndex)
    {
        final Edge matchingEdge = findEdgeMatchingFirstElement(t.get(startIndex));
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

                if (matchingEdge.getEndNode().getSuffixableElement().get(
                    matchingEdge.getStartIndex() + i).equals(t.get(startIndex)) == false)
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
     * Creates a child note and returns an edge to it. You need to provide the element on
     * which the edge is created.
     */
    protected Edge createChildNode(int firstElement, int lastElement)
    {
        final Node child = container.createNode();
        final Edge link = new Edge(firstElement, lastElement, this, child);

        child.setEdgeToParent(link);
        this.addEdge(link);

        return link;
    }

    /**
     * Returns a {@link SuffixableElement} all outgoing edges point to.
     */
    public SuffixableElement getSuffixableElement()
    {
        // In case of a single-element suffix tree, this doesn't matter.
        return container.getCurrentElement();
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
        if (getEdgeToParent() == null)
        {
            return 0;
        }

        int start = getEdgeToParent().getStartIndex();
        for (Edge ei = getEdgeToParent(); ei.getStartNode().getEdgeToParent() != null;)
        {
            ei = ei.getStartNode().getEdgeToParent();
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
        if (getEdgeToParent() == null)
        {
            return -1;
        }

        return this.getEdgeToParent().getEndIndex();
    }

    /**
     * Creates a {@link List} which encapsulates the phrase ending at this node.
     */
    public final Phrase getPhrase()
    {
        return new Phrase();
    }

    /**
     * Returns true if this {@link Node} is a leaf node (has no outgoing edges)
     */
    public boolean isLeaf()
    {
        return this.edges.size() == 0;
    }

    /**
     * Adds an edge to this node.
     */
    protected void addEdge(Edge edge)
    {
        edges.put(edge.getEndNode().getSuffixableElement().get(edge.firstElementIndex),
            edge);
    }

    /**
     * Removes an edge from this node.
     */
    protected void removeEdge(Edge edge)
    {
        edges
            .remove(edge.getEndNode().getSuffixableElement().get(edge.firstElementIndex));
    }

    /** Accessor to edgeToParent field */
    protected void setEdgeToParent(Edge edgeToParent)
    {
        this.edgeToParent = edgeToParent;
    }

    /** Accessor to edgeToParent field */
    public Edge getEdgeToParent()
    {
        return edgeToParent;
    }
}
