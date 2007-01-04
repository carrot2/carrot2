
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

import java.util.ArrayList;

/**
 * This class implements a Generalized Suffix Tree data structure. GST is a
 * suffix tree capable of storing suffixes for more than one {@link SuffixableElement}.
 * 
 * A specialized {@link Node} type is used in this subclass called simply
 * {@link GSTNode}.
 * 
 * @author Dawid Weiss
 */
public class GeneralizedSuffixTree extends SuffixTree {

    /**
     * An ArrayList of SuffixableElement elements inserted in this SuffixTree.
     */
    private ArrayList allElements = new ArrayList();

    /**
     * Creates a new GSTNode.
     */
    protected Node createNode() {
        return new GSTNode(this);
    }

    /**
     * Adds a single SuffixableElement to the tree.
     */
    public Node add(SuffixableElement element) {
        Suffix activePoint;

        // add a new SuffixableElement to the array of stored elements
        allElements.add(element);

        if (rootNode == null) {
            rootNode = createNode();

            rootNode.setEdgeToParent(null);

            // start inserting from first prefix and root node.
            activePoint = new Suffix(this, rootNode, 0, -1);

            // loop through all prefixes.
            for (int i = 0; i < getCurrentElement().size(); i++)
                insertPrefix(activePoint, i);
        } else {

            // find out at which node the longest matching prefix of current
            // SuffixableElement
            // ends - this will be the active node we'll resume inserting.
            Node lastPrefixNode = rootNode;
            Edge follow = null;
            int startIndex;
            int endIndex = 0;

            for (startIndex = 0; startIndex < getCurrentElement().size();) {

                // is there any Edge we can follow?
                follow = lastPrefixNode.findEdgeMatchingEntirely(
                        getCurrentElement(), startIndex);

                if (follow == null) {
                    endIndex = startIndex - 1;

                    // check if it maybe was an implicit node somewhere along
                    // the edge
                    follow = lastPrefixNode
                            .findEdgeMatchingFirstElement(startIndex);

                    if (follow == null)
                        break;

                    // advance index to that implicit node and break the loop
                    for (int i = follow.getStartIndex(); i <= follow
                            .getEndIndex(); i++) {
                        if (follow.getEndNode().getSuffixableElement().get(i)
                                .equals(
                                        this.getCurrentElement().get(
                                                endIndex + 1)) == false) {

                            // first non-matching character (implicit node on
                            // compressed path).
                            break;
                        }

                        endIndex++;
                    }

                    break;
                } else {

                    // advance pointer
                    lastPrefixNode = follow.getEndNode();
                    startIndex += follow.length();
                }
            }

            if (startIndex == getCurrentElement().size()) {

                // POSSIBILITY OF PERFORMANCE TUNING:
                // iterate through elements of the boundary path only.
                for (int i = 0; i < getCurrentElement().size(); i++) {
                    activePoint = new Suffix(this, rootNode, i,
                            getCurrentElement().size() - 1);

                    activePoint.canonize();
                    ((GSTNode) activePoint.originNode)
                            .addIndexedElement(getCurrentElementNumber());
                }

                return rootNode;
            } else {

                // insert remaining suffixes.
                activePoint = new Suffix(this, lastPrefixNode, startIndex,
                        endIndex);

                // loop through all prefixes.
                for (int i = endIndex + 1; i < getCurrentElement().size(); i++)
                    insertPrefix(activePoint, i);
            }
        }

        return rootNode;
    }

    /**
     * Returns the SuffixableElement which was added to the tree as the index-th
     * one.
     */
    protected SuffixableElement getElementByIndex(int index) {
        return (SuffixableElement) allElements.get(index);
    }

    /**
     * Returns the currently processed SuffixableElement.
     */
    protected SuffixableElement getCurrentElement() {
        return (SuffixableElement) this.allElements
                .get(getCurrentElementNumber());
    }

    /**
     * Returns the number of currently inserted SuffixableElement.
     */
    protected int getCurrentElementNumber() {
        return allElements.size() - 1;
    }
}

