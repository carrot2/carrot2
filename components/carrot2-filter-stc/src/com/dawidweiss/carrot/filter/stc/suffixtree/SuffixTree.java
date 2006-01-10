
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.filter.stc.suffixtree;

/**
 * This class implements a generic algorithm for creating a SuffixTree. The
 * algorithm has been derived from the C-implementation by Mark Nelson:
 * 
 * <PRE>
 * 
 * <B>Fast String Searching With Suffix Trees <I>Mark Nelson </i> </B> Dr Dobb's
 * Journal, August 1996 http://softlab.od.ua/algo/data/suftrees/suffixt.htm
 * (2000)
 * 
 * </PRE>
 * 
 * The algorithm itself was proposed by Esko Ukkonnen.
 * 
 * This class can build suffix trees from any objects which implement
 * <code>SuffixableElement</code> interface and object's elements must
 * implement <code>java.lang.Comparable</code> interface.
 * <code>hashCode()</code> and <code>equals()</code> functions <B>may </B>
 * be applied to container's elements.
 * 
 * It is obvious that the performance of this class will <B>highly </B> depend
 * on the type of collection passed. In general, collection objects will be
 * accessed randomly, so a linked list is perhaps not the best choice. An
 * <code>ArrayList</code> object would be a better solution (or a custom
 * wrapper for an array if needed). Also, the time needed for casts and RTTI may
 * be significant - if case a faster implementation of this class is needed, it
 * can always be subclassed to a specific type/ JNI implementation.
 * 
 * @author Dawid Weiss
 */
public class SuffixTree {

    /**
     * This is the root node of the tree.
     */
    protected Node rootNode = null;

    /**
     * Nodes factory. Required if this class is subclassed and new type of Node
     * is required.
     */
    protected Node createNode() {
        return new Node(this);
    }

    /**
     * Currently processed SuffixableElement object.
     */
    private SuffixableElement currentSuffixableElement = null;

    /**
     * Returns currently active SuffixableElement. In case of single-collection
     * suffix trees, the element will always be the same.
     */
    protected SuffixableElement getCurrentElement() {
        return this.currentSuffixableElement;
    }

    /**
     * Adds a single SuffixableElement to the tree.
     */
    public synchronized Node add(SuffixableElement element) {
        if (this.currentSuffixableElement != null)
            throw new RuntimeException(
                    "This class doesn't implement generalized SuffixTrees.");

        this.currentSuffixableElement = element;

        // create root node and active point.
        rootNode = this.createNode();

        rootNode.setEdgeToParent(null);

        Suffix activePoint = new Suffix(this, rootNode, 0, -1);

        // loop through all prefixes.
        for (int i = 0; i < currentSuffixableElement.size(); i++)
            insertPrefix(activePoint, i);

        return rootNode;
    }

    /**
     * The heart of Ukkonnen's algorithm. Inserts a single prefix to a
     * SuffixTree while retaining its structure.
     * 
     * The source code to this function is commented with what Mark Nelson had
     * in his excellent paper published in Dr Dobb's journal.
     * 
     * @param active
     *            Active point in the suffix tree
     * @param lastElementIndex
     *            The index of currently inserted element from
     *            getCurrentElement().
     * @return current endpoint (becomes active point if more elements are to be
     *         inserted).
     */
    protected Suffix insertPrefix(Suffix active, int lastElementIndex) {
        Node parent_node = null;
        Node last_parent_node = null;

        while (true) {
            Edge edge;

            parent_node = active.originNode;

            //
            // Step 1 is to try and find a matching edge for the given node.
            // If a matching edge exists, we are done adding edges, so we break
            // out of this big loop.
            //
            if (active.isExplicit()) {

                // explicit node, check if it has an edge starting with current
                // element
                edge = active.originNode
                        .findEdgeMatchingFirstElement(lastElementIndex);

                // if it does, do nothing (path compression - an implicit node
                // is created).
                if (edge != null)
                    break;
            } else {

                // implicit node, a little more complicated because we have to
                // split the
                // edge and create a new node IF the implicit node doesn't match
                // inserted element.
                edge = active.originNode
                        .findEdgeMatchingFirstElement(active.firstElementIndex);

                int span = active.lastElementIndex - active.firstElementIndex;

                if (edge.getEndNode().getSuffixableElement().get(
                        edge.firstElementIndex + span + 1).equals(
                        getCurrentElement().get(lastElementIndex))) {
                    break;
                }

                // we must split the edge. The newly created node becomes parent
                // node.
                parent_node = edge.splitEdge(active);
            }

            //
            // We didn't find a matching edge, so we create a new one, add
            // it to the tree at the parent node position, and insert it
            // into the hash table. When we create a new node, it also
            // means we need to create a suffix link to the new node from
            // the last node we visited.
            //
            parent_node.createChildNode(lastElementIndex, getCurrentElement()
                    .size() - 1);

            if (last_parent_node != null && last_parent_node != getRootNode()) {
                last_parent_node.suffixNode = parent_node;

                if (parent_node == null) {
                    throw new RuntimeException();
                }
            }

            last_parent_node = parent_node;

            //
            // This final step is where we move to the next smaller suffix
            //
            if (active.originNode == getRootNode())
                active.firstElementIndex++;
            else
                active.originNode = active.originNode.suffixNode;

            if (active.originNode == null)
                throw new RuntimeException();

            active.canonize();
        }

        if (last_parent_node != null && last_parent_node != getRootNode()) {
            last_parent_node.suffixNode = parent_node;

            if (last_parent_node.suffixNode == null) {
                throw new RuntimeException();
            }
        }

        active.lastElementIndex++; // Now the endpoint is the next active point

        active.canonize();

        return active;
    }

    /**
     * Returns the root node of this tree. Please beware that this is the actual
     * structure on which this object operates - any changes will reflect in the
     * state of the SuffixTree.
     */
    public Node getRootNode() {
        return this.rootNode;
    }

    /**
     * Converts this SuffixTree to a string.
     */
    public String toString() {
        return rootNode.toString();
    }
}

