
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
 * This class implements a generic Esko Ukkonnen's algorithm for creating a suffix tree.
 * The implementation has been derived from the C-version by Mark Nelson:
 * <p>
 * <b>Fast String Searching With Suffix Trees <i>Mark Nelson </i></b> Dr Dobb's Journal,
 * August 1996 http://softlab.od.ua/algo/data/suftrees/suffixt.htm (2000)
 * <p>
 * This class can build suffix trees from any sequences of {@link Comparable} objects. The
 * sequence should implement {@link ISuffixableElement} interface.
 * <p>
 * It is obvious that the performance of this class will <b>highly</b> depend on the type
 * of sequence collection.
 */
public class SuffixTree
{
    /**
     * This is the root node of the tree.
     */
    protected Node rootNode = null;

    /**
     * Nodes factory. Required if this class is subclassed and new type of {@link Node} is
     * required.
     */
    protected Node createNode()
    {
        return new Node(this);
    }

    /**
     * Currently processed {@link ISuffixableElement} object.
     */
    private ISuffixableElement currentSuffixableElement = null;

    /**
     * Returns currently active SuffixableElement. In case of single-collection suffix
     * trees, the element will always be the same.
     */
    protected ISuffixableElement getCurrentElement()
    {
        return this.currentSuffixableElement;
    }

    /**
     * Adds a single {@link ISuffixableElement} to the tree.
     */
    public Node add(ISuffixableElement element)
    {
        if (this.currentSuffixableElement != null) throw new RuntimeException(
            "This class doesn't implement generalized SuffixTrees.");

        this.currentSuffixableElement = element;

        // create root node and active point.
        rootNode = this.createNode();

        rootNode.setEdgeToParent(null);

        Suffix activePoint = new Suffix(this, rootNode, 0, -1);

        // Loop through all prefixes.
        for (int i = 0; i < currentSuffixableElement.size(); i++)
            insertPrefix(activePoint, i);

        return rootNode;
    }

    /**
     * The heart of Ukkonnen's algorithm. Inserts a single prefix to the tree while
     * retaining its structure. The source code to this function has comments from Mark
     * Nelson's excellent paper published in Dr Dobb's journal.
     * 
     * @param active Active point in the suffix tree.
     * @param lastElementIndex The index of currently inserted element from
     *            getCurrentElement().
     * @return current endpoint (becomes active point if more elements are to be
     *         inserted).
     */
    protected Suffix insertPrefix(Suffix active, int lastElementIndex)
    {
        Node parent_node = null;
        Node last_parent_node = null;

        while (true)
        {
            Edge edge;
            parent_node = active.originNode;

            /*
             * Step 1 is to try and find a matching edge for the given node. If a matching
             * edge exists, we are done adding edges, so we break out of this big loop.
             */
            if (active.isExplicit())
            {
                /*
                 * Explicit node, check if it has an edge starting with current element.
                 */
                edge = active.originNode.findEdgeMatchingFirstElement(lastElementIndex);

                /*
                 * If it does, do nothing (path compression - an implicit node is
                 * created).
                 */
                if (edge != null) break;
            }
            else
            {
                /*
                 * implicit node, a little more complicated because we have to split the
                 * edge and create a new node IF the implicit node doesn't match inserted
                 * element.
                 */
                edge = active.originNode
                    .findEdgeMatchingFirstElement(active.firstElementIndex);

                int span = active.lastElementIndex - active.firstElementIndex;

                if (edge.getEndNode().getSuffixableElement().get(
                    edge.firstElementIndex + span + 1).equals(
                    getCurrentElement().get(lastElementIndex)))
                {
                    break;
                }

                /*
                 * We must split the edge. The newly created node becomes parent node.
                 */
                parent_node = edge.splitEdge(active);
            }

            /*
             * We didn't find a matching edge, so we create a new one, add it to the tree
             * at the parent node position, and insert it into the hash table. When we
             * create a new node, it also means we need to create a suffix link to the new
             * node from the last node we visited.
             */
            parent_node.createChildNode(lastElementIndex, getCurrentElement().size() - 1);

            if (last_parent_node != null && last_parent_node != getRootNode())
            {
                last_parent_node.suffixNode = parent_node;

                if (parent_node == null)
                {
                    throw new RuntimeException();
                }
            }

            last_parent_node = parent_node;

            /* This final step is where we move to the next smaller suffix */
            if (active.originNode == getRootNode()) active.firstElementIndex++;
            else active.originNode = active.originNode.suffixNode;

            if (active.originNode == null) throw new RuntimeException();

            active.canonize();
        }

        if (last_parent_node != null && last_parent_node != getRootNode())
        {
            last_parent_node.suffixNode = parent_node;

            if (last_parent_node.suffixNode == null)
            {
                throw new RuntimeException();
            }
        }

        /* Now the endpoint is the next active point */
        active.lastElementIndex++;

        active.canonize();

        return active;
    }

    /**
     * Returns the root node of this tree. Please beware that this is the actual structure
     * on which this object operates - any changes will reflect in the state of the
     * SuffixTree.
     */
    public Node getRootNode()
    {
        return this.rootNode;
    }

    /**
     * Converts this tree to a string.
     */
    public String toString()
    {
        return rootNode.toString();
    }
}
