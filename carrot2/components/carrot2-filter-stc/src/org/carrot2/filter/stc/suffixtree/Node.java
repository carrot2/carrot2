
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

package org.carrot2.filter.stc.suffixtree;

import java.util.*;

/**
 * A node in a SuffixTree.
 * 
 * @author Dawid Weiss
 */
public class Node {
    static public int idc = 1;

    public int id;

    /**
     * Edges leaving this node. Please note that this way of storing edges may
     * reduce performance of the algorithm. This is a generic implementation -
     * if faster storage is needed, subclass this class.
     */
    public HashMap edges;

    /** This node's container tree */
    protected SuffixTree container;

    /** This field hold the pointer to the next smaller suffix */
    protected Node suffixNode;

    /** For informational reasons only */
    public Node getSuffixNode() {
        return suffixNode;
    }

    /**
     * This field holds the edge from parent node to this node. This is to ease
     * later management of this suffix tree.
     */
    protected Edge edgeToParent;

    /**
     * Every node knows its container tree. Nobody except subclasses and
     * package-wide classes can instantiate a Node.
     */
    protected Node(SuffixTree container) {
        edges = new HashMap(7, 0.75f);
        this.container = container;
        this.id = Node.idc++;
    }

    /**
     * Finds an edge starting in this node and labelled with the particular
     * element from current container's SuffixableElement. Null is returned if
     * no edge has been found.
     */
    protected Edge findEdgeMatchingFirstElement(int index) {
        return findEdgeMatchingFirstElement(container.getCurrentElement().get(
                index));
    }

    /**
     * Returns an iterator object of edges leaving this Node. Please notice that
     * using remove() method of this iterator may not throw an exception and
     * despite ruin suffix tree structure.
     */
    public Iterator getEdgesIterator() {
        return this.edges.values().iterator();
    }

    /**
     * Returns an Edge object, which matches passed argument. The passed
     * argument must be valid part of some SuffixableElement, e.g. must
     * implement equals method.
     * 
     * Method returns null if no matching edge has been found.
     */
    public Edge findEdgeMatchingFirstElement(Object key) {
        return (Edge) edges.get(key);
    }

    /**
     * Finds an edge <B>entirely </b> matching the SuffixableElement at some
     * index. E.g. if the Edge holds three elements, the three of them are
     * matched against elements of SuffixableElement at index, index+1 and
     * index+3.
     * 
     * The method returns null if matching failed.
     */
    public Edge findEdgeMatchingEntirely(SuffixableElement t, int startIndex) {
        Edge matchingEdge = findEdgeMatchingFirstElement(t.get(startIndex));

        if (matchingEdge != null) {

            // calculate the number of elements to compare. if there are less
            // elements in t
            // than in the edge, it may be possible that suffixableElement ends
            // somewhere
            // along the compressed path (is a prefix of the suffix represented
            // by this path).
            int maxLength = matchingEdge.length();

            if (maxLength > t.size() - startIndex) {
                maxLength = t.size() - startIndex;
            }

            // the first element matches, check all the remaining ones
            for (int i = 1; i < maxLength; i++) {
                startIndex++;

                if (matchingEdge.getEndNode().getSuffixableElement().get(
                        matchingEdge.getStartIndex() + i).equals(
                        t.get(startIndex)) == false) {

                    // this path doesn't entirely match.
                    return null;
                }
            }

            // are we along the compressed path?
            if (matchingEdge.getEndIndex() > matchingEdge.getStartIndex()
                    + maxLength - 1) {

                // we are along compressed path. Return null because we need
                // entirely matching
                // edge.
                return null;
            }
        }
        ;

        return matchingEdge;
    }

    /**
     * Creates a child note and returns an edge to it. You need to provide the
     * element on which the edge is created.
     */
    protected Edge createChildNode(int firstElement, int lastElement) {
        Node child = container.createNode();
        Edge link = new Edge(firstElement, lastElement, this, child);

        child.setEdgeToParent(link);
        this.addEdge(link);

        return link;
    }

    /**
     * Returns a SuffixableElement all outgoing edges indices point to.
     */
    public SuffixableElement getSuffixableElement() {

        // in case of a single-element suffix tree, this doesn't matter.
        return container.getCurrentElement();
    }

    /**
     * Returns the index of the start of this node's suffix (substring
     * actually). To obtain a full path from root to this node, traverse
     * SuffixableElement like below:
     * 
     * <CODE>SuffixableElement p = node.getSuffixableElement(); for (int
     * i=node.getSuffixStartIndex(); i&lt;= node.getSuffixEndIndex; i++) {
     * System.out.print( p.get(i) ); }</code>
     *  
     */
    public int getSuffixStartIndex() {
        if (getEdgeToParent() == null) {
            return 0;
        }

        int start = getEdgeToParent().getStartIndex();

        for (Edge ei = getEdgeToParent(); ei.getStartNode().getEdgeToParent() != null;) {
            ei = ei.getStartNode().getEdgeToParent();
            start -= ei.length();
        }

        return start;
    }

    /**
     * Returns the index of the last element of this node's suffix. To obtain a
     * full path from root to this node, traverse SuffixableElement like below:
     * 
     * <CODE>SuffixableElement p = node.getSuffixableElement(); for (int
     * i=node.getSuffixStartIndex(); i&lt;= node.getSuffixEndIndex; i++) {
     * System.out.print( p.get(i) ); }</code>
     */
    public int getSuffixEndIndex() {
        if (getEdgeToParent() == null) {
            return -1;
        }

        return this.getEdgeToParent().getEndIndex();
    }

    /**
     * Phrase collection returned by getPhrase method.
     */
    protected class Phrase extends AbstractList {
        int start;

        int end;

        public Phrase() {
            super();

            start = Node.this.getSuffixStartIndex();
            end = Node.this.getSuffixEndIndex();

            if (Node.this.getSuffixableElement().get(end) == SuffixableElement.END_OF_SUFFIX) {
                // skip EOS marker
                end--;
            }
        }

        public int size() {
            return end - start + 1;
        }

        public Object get(int index) {
            return Node.this.getSuffixableElement().get(index + start);
        }
    }

    /**
     * Creates a Collection object which encapsulates the phrase ending at this
     * node.
     */
    public List getPhrase() {
        return new Phrase();
    }

    /**
     * Returns true if this Node is a leaf node (has no outgoing edges)
     */
    public boolean isLeaf() {
        return this.edges.size() == 0;
    }

    /**
     * Adds an edge to this object's subnodes links.
     */
    protected void addEdge(Edge edge) {
        edges.put(edge.getEndNode().getSuffixableElement().get(
                edge.firstElementIndex), edge);
    }

    /**
     * Removes an edge from this object's subnodes links.
     */
    protected void removeEdge(Edge edge) {
        edges.remove(edge.getEndNode().getSuffixableElement().get(
                edge.firstElementIndex));
    }

    /** Accessor to edgeToParent field */
    protected void setEdgeToParent(Edge edgeToParent) {
        this.edgeToParent = edgeToParent;
    }

    /** Accessor to edgeToParent field */
    public Edge getEdgeToParent() {
        return edgeToParent;
    }

    /** Returns a String representation of this object */
    public String toString() {
        return "[" + this.id + "]";
    }

    public String bpath() {
        if (this.suffixNode == null)
            return this.toString() + " [end]";
        else
            return this.toString() + "-->" + this.suffixNode.bpath();
    }

    /** Returns a string with collection elements from startindex to endIndex */
    public String toString(int startIndex, int endIndex) {
        StringBuffer p = new StringBuffer(20);

        for (int i = startIndex; i <= endIndex; i++) {
            p.append(this.getSuffixableElement().get(i).toString());
        }

        return p.toString();
    }

    /**
     * Appends contents of this Node and subnodes to a stringbuffer (with
     * certain indent)
     */
    public StringBuffer toStringBuffer(StringBuffer sb, int indent) {
        StringBuffer t = new StringBuffer();

        for (int i = 0; i < indent; i++)
            t.append(' ');

        for (Iterator p = edges.values().iterator(); p.hasNext();) {
            Edge edge = (Edge) p.next();

            sb.append(t);
            sb.append(edge.toString());
            sb.append('\n');
            edge.endNode.toStringBuffer(sb, indent + 2);
        }

        return sb;
    }
}

