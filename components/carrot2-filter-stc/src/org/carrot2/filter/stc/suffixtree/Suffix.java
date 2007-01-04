
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.stc.suffixtree;

/**
 * A suffix object which is used ONLY internally in class SuffixTree.
 * 
 * @author Dawid Weiss
 */
public class Suffix {

    /** This Suffix's container tree */
    protected SuffixTree container;

    /** first collection element where this suffix starts */
    protected int firstElementIndex;

    /** the collection element where this suffix ends (inclusive) */
    protected int lastElementIndex;

    /** origin node needed for canonization */
    protected Node originNode;

    /**
     * Public constructor.
     */
    public Suffix(SuffixTree container, Node originNode, int firstElementIndex,
            int lastElementIndex) {
        this.container = container;
        this.originNode = originNode;
        this.firstElementIndex = firstElementIndex;
        this.lastElementIndex = lastElementIndex;
    }

    /**
     * Returns true when this suffix ends in an explicit node.
     */
    public boolean isExplicit() {
        return (firstElementIndex > lastElementIndex) ? true : false;
    }

    /**
     * Returns true when this suffix ends in an implicit node (see: path
     * compression)
     */
    public boolean isImplicit() {
        return (firstElementIndex <= lastElementIndex) ? true : false;
    }

    /**
     * Performs canonization of this suffix - we slide down the tree until we're
     * in the closest node to the end of the collection.
     */
    public void canonize() {
        if (!isExplicit()) {
            Edge edge = originNode.findEdgeMatchingFirstElement(container
                    .getCurrentElement().get(firstElementIndex));
            int edgeSpan = edge.lastElementIndex - edge.firstElementIndex;

            while (edgeSpan <= (lastElementIndex - firstElementIndex)) {
                firstElementIndex = firstElementIndex + edgeSpan + 1;
                originNode = edge.endNode;

                if (firstElementIndex <= lastElementIndex) {
                    edge = originNode.findEdgeMatchingFirstElement(container
                            .getCurrentElement().get(firstElementIndex));
                    edgeSpan = edge.lastElementIndex - edge.firstElementIndex;
                }
                ;
            }
        }
    }
}

