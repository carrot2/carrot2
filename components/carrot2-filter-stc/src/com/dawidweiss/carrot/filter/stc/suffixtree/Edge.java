package com.dawidweiss.carrot.filter.stc.suffixtree;


/**
 * An edge between two nodes in the SuffixTree.
 *
 * A single edge may represent more than one element of SuffixableElement collection (otherwise
 * it would be a trie not a suffix tree).
 *
 * @author Dawid Weiss
 */
public class Edge
{

    /** Index of the collection's element from which this edge starts */
    protected int firstElementIndex;

    /** Index at which the edge ends (inclusive) */
    protected int lastElementIndex;

    /** The node from which the Edge starts */
    protected Node startNode;

    /** The node where this Edge ends */
    protected Node endNode;

    /** Simple protected constructor */
    protected Edge(int firstElementIndex, int lastElementIndex, Node startNode, Node endNode)
    {
        this.firstElementIndex = firstElementIndex;
        this.lastElementIndex  = lastElementIndex;
        this.startNode         = startNode;
        this.endNode           = endNode;
    }


    /**
     * This method splits an Edge on a given suffix, creates a new Node
     * and makes a fork in the tree.
     */
    protected Node splitEdge(Suffix s)
    {
        startNode.removeEdge(this);

        Edge newEdge = s.originNode.createChildNode(s.firstElementIndex, s.lastElementIndex);

        newEdge.endNode.suffixNode = s.originNode;

        if (newEdge.endNode.suffixNode == null) throw new RuntimeException();

        this.firstElementIndex += s.lastElementIndex - s.firstElementIndex + 1;
        startNode              = newEdge.endNode;

        startNode.addEdge(this);

        return newEdge.endNode;
    }


    /** Returns the start node of this edge */
    public Node getStartNode()
    {
        return this.startNode;
    }


    /** Returns the end node of this edge */
    public Node getEndNode()
    {
        return this.endNode;
    }


    /** Returns the starting element index */
    public int getStartIndex()
    {
        return this.firstElementIndex;
    }


    /** Returns the ending element index */
    public int getEndIndex()
    {
        return this.lastElementIndex;
    }


    /**
     * Returns the length of this Edge meaning number of compressed nodes this
     *  edge represents. Equals getEndIndex - getStartIndex + 1.
     */
    public int length()
    {
        return getEndIndex() - getStartIndex() + 1;
    }


    /** Returns a string with readable information about this Edge object */
    public String toString()
    {
        return getEndNode().toString(firstElementIndex, lastElementIndex) + " <"
               + this.firstElementIndex + '-' + this.lastElementIndex + '>' + " ("
               + getEndNode().toString() + ")";
    }
}



