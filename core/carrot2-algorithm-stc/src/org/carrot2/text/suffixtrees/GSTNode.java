
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.suffixtrees;

/**
 * A node in a Generalized Suffix Tree (multi-sequence {@link SuffixTree}).
 */
public class GSTNode extends Node
{
    /**
     * A bitset of suffixed sentences.
     */
    protected final ExtendedBitSet elementsInNode;

    /**
     * The SuffixableElement from which this node will take indices for edges.
     */
    protected final int suffixableElementIndex;

    /**
     * Protected constructor binds this object to the container GeneralizedSuffixTree
     * object
     */
    public GSTNode(GeneralizedSuffixTree container)
    {
        super(container);

        elementsInNode = new ExtendedBitSet(getContainer().getCurrentElementNumber() + 1);
        elementsInNode.set(getContainer().getCurrentElementNumber());

        suffixableElementIndex = getContainer().getCurrentElementNumber();
    }

    /** adds a new indexed element to this node */
    public void addIndexedElement(int elementIndex)
    {
        elementsInNode.set(elementIndex);
        propagateIndexedElementUp(elementIndex);
    }

    /**
     * Creates a child node of this node or marks existing subnode as part of currently
     * inserted element's tree.
     */
    protected Edge createChildNode(int firstElement, int lastElement)
    {
        Node child = container.createNode();
        Edge link = new Edge(firstElement, lastElement, this, child);

        child.setEdgeToParent(link);
        addEdge(link);

        return link;
    }

    /**
     * Adds an edge to this object's subnodes links and propagates subnodes' indexed
     * elements up the tree.
     */
    protected void addEdge(Edge edge)
    {
        super.addEdge(edge);
        ((GSTNode) edge.getEndNode()).propagateIndexedElementUp();
    }

    /**
     * Propagates this node's element up the nodes' hierarchy.
     */
    protected void propagateIndexedElementUp()
    {
        propagateIndexedElementUp(suffixableElementIndex);
    }

    /**
     * Propagates the element of some index up the nodes' hierarchy.
     */
    protected void propagateIndexedElementUp(int elementIndex)
    {
        Edge edge;
        GSTNode parent = this;

        while ((edge = parent.getEdgeToParent()) != null)
        {
            parent = (GSTNode) edge.getStartNode();

            if (parent.elementsInNode.get(elementIndex) == true) break;
            else parent.elementsInNode.or(elementsInNode);
        }
    }

    /**
     * Returns the {@link ISuffixableElement} which is indexed by outgoing edges.
     */
    public ISuffixableElement getSuffixableElement()
    {
        return getContainer().getElementByIndex(suffixableElementIndex);
    }

    /**
     * Performs a cast only to clean the code.
     */
    protected GeneralizedSuffixTree getContainer()
    {
        return (GeneralizedSuffixTree) container;
    }

    /**
     * Every suffix inserted in a GST creates a path from root to a leaf. This function
     * returns the number of elements which have paths going through this node.
     */
    public int getSuffixedElementsCount()
    {
        return elementsInNode.numberOfSetBits();
    }

    /**
     * Returns the internal representation of suffixed elements (an {@link ExtendedBitSet}).
     */
    public ExtendedBitSet getInternalSuffixedElementsRepresentation()
    {
        return elementsInNode;
    }

    /** Returns a String representation of this object */
    public String toString()
    {
        return "[:s(" + elementsInNode.toString() + ")]";
    }
}
