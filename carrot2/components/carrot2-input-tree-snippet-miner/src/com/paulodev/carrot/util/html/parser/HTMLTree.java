

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


/*
 * HTML Parser
 * Copyright (C) 1997 David McNicol
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * file COPYING for more details.
 */
package com.paulodev.carrot.util.html.parser;


import java.util.*;


/**
 * This class stores an HTML file in tree format. It can be constructed from an HTMLTokenizer or a
 * file name, in which case it will create its own tokenizer.
 * 
 * <p>
 * Once the HTML file has been parsed a number of search operations can be performed. The nature of
 * the searches are described below, but some of their uses are highlighted here:
 * </p>
 * 
 * <p>
 * 
 * <ul>
 * <li>
 * Subtree - Finding all of the FORM elements within a BODY element.
 * </li>
 * <li>
 * Sibling - Finding all the LI elements within the same UL element.
 * </li>
 * <li>
 * All - Finding every occurence of the A element.
 * </li>
 * </ul>
 * 
 * There is also a context search, which performs a subtree search on the specified element's
 * parent. This can be thought of as a combination between as sibling search and a subtree search.
 * </p>
 *
 * @author <a href="http://www.strath.ac.uk/~ras97108/">David McNicol</a>
 *
 * @see HTMLTokenizer
 */
public class HTMLTree
{
    private HTMLNode root; // The root of the HTML tree.
    private Vector allNodes = new Vector();

    /**
     * Gets the root node
     */
    public HTMLNode getRootNode()
    {
        return root;
    }


    /**
     * Gets all nodes vector
     */
    public Vector getAllNodes()
    {
        return allNodes;
    }

    /**
     * Constructs a new HTMLTree using the tokens from the specified Enumeration.
     */
    public HTMLTree(Enumeration e)
    {
        // Create the root element from the enumeration of tokens.
        int [] num = { 0 };
        root = new HTMLNode(this, null, null, e, 0, num);
    }


    /**
     * Constructs a new HTMLTree using the tokens from the specified HTMLTokenizer.
     *
     * @param ht the source of the HTML tokens.
     */
    public HTMLTree(HTMLTokenizer ht)
    {
        int [] num = { 0 };

        // Create the root element from the tokens.
        root = new HTMLNode(this, new TagToken(""), null, ht.getTokens(), 0, num);
    }

    public void appendNode(HTMLNode node)
    {
        allNodes.add(node);
    }


    /**
     * Finds the first element with the specified name in the specified subtree.
     *
     * @param name the name of the element to search for.
     * @param tree the subtree to search.
     */
    public HTMLNode findInSubtree(String name, HTMLNode tree)
    {
        return find(name, tree, null, true, false);
    }


    /**
     * Finds the next element after the specified one in the subtree. If the previous element is
     * not in the subtree then nothing will be found.
     *
     * @param tree the subtree to search.
     * @param prev a previously found element.
     */
    public HTMLNode findNextInSubtree(HTMLNode tree, HTMLNode prev)
    {
        // Return nothing if there is no previous element.
        if (prev == null)
        {
            return null;
        }

        // Search the subtree for the next element with the same name.
        return find(prev.getName(), tree, prev, true, false);
    }


    /**
     * Finds the first element with the specified name in the entire tree.
     *
     * @param name the name of the element to search for.
     */
    public HTMLNode findInAll(String name)
    {
        return find(name, root, null, true, false);
    }


    /**
     * Finds the next element with the same name as the one specified in the entire tree.
     *
     * @param prev the previously found element.
     */
    public HTMLNode findNextInAll(HTMLNode prev)
    {
        // Return nothing if there is no previous element.
        if (prev == null)
        {
            return null;
        }

        // Search for the next element in the entire tree.
        return find(prev.getName(), prev.getParent(), prev, true, true);
    }


    /**
     * Find the first element with the specified name in the specified element's context (that is,
     * the elements parent's subtree).
     *
     * @param name the name of the element to search for.
     * @param el the element whose context is to be searched.
     */
    public HTMLNode findInContext(String name, HTMLNode el)
    {
        // Return nothing if the arguments are invalid.
        if (el == null)
        {
            return null;
        }

        // Search the elements parent's subtree.
        return find(name, el.getParent(), null, true, false);
    }


    /**
     * Find the next element with the same name as the specified one in the first element's context
     * (that is, the first elements parent's subtree). If the previous element is not in the
     * subtree then nothing will be found.
     *
     * @param el the element whose context is to be searched.
     * @param the previously found element.
     */
    public HTMLNode findNextInContext(HTMLNode el, HTMLNode prev)
    {
        // Return nothing if the arguments are invalid.
        if (el == null)
        {
            return null;
        }

        // Search the elements parent's subtree.
        return find(el.getName(), el.getParent(), prev, true, false);
    }


    /**
     * Finds the next element with the same name as the specified one amongst that elements
     * siblings (that is, the elements parent's children).
     *
     * @param el the element whose siblings are to be searched.
     */
    public HTMLNode findSibling(HTMLNode el)
    {
        // Return nothing if the element is invalid.
        if (el == null)
        {
            return null;
        }

        // Search for a sibling in the elements parent's subtree.
        return find(el.getName(), el.getParent(), el, false, false);
    }


    /**
     * Prints a string representation of the HTMLTree.
     */
    public String toString()
    {
        root.hide();

        return root.toString();
    }


    /**
     * Generic find method which searches for a string in the given tree's children. However, the
     * search will not start until the start element has been passed. The tree's grandchildren
     * will be searched recursively if the <code>recursive</code> argument is true. The whole tree
     * after the element will be searched if the <code>searchParent</code> argument is true. In
     * this case the method will recurse back towards the root element.
     */
    private HTMLNode find(
        String name, HTMLNode tree, HTMLNode start, boolean recursive, boolean searchParent
    )
    {
        Enumeration children; // The immediate children of the subtree.
        Object next; // The next object from the enumeration.
        boolean searching; // True if we are actively searching.
        HTMLNode child; // One of the subtree's children.
        HTMLNode found; // Result of a subtree search.

        // Return nothing if the arguments are invalid.
        if ((name == null) || (tree == null))
        {
            return null;
        }

        // Check if we should delay the search until we find the
        // specified start element.
        searching = (start == null);

        // Get the subtree's children.
        children = tree.getChildren();

        // Return nothing if the subtree has no children.
        if (children == null)
        {
            return null;
        }

        // Loop through the subtree's children.
        while (children.hasMoreElements())
        {
            // Get the next child from the enumeration.
            next = children.nextElement();

            // Check if this child is an HTMLNode.
            if (!(next instanceof HTMLNode))
            {
                continue;
            }

            // Cast the child into type HTMLNode.
            child = (HTMLNode) next;

            if (searching)
            {
                // Check if we have found the element.
                if (name.equalsIgnoreCase(child.getName()))
                {
                    return child;
                }

                // Check if we should search grandchildren.
                if (recursive)
                {
                    // Search the child's subtree.
                    found = find(name, child, null, true, false);

                    // Return the element if we found one.
                    if (found != null)
                    {
                        return found;
                    }
                }
            }
            else
            {
                // Check if this element is the start element.
                if (child == start)
                {
                    searching = true;
                }
            }
        }

        // Check if we should search the subtree's parent tree.
        if (searchParent)
        {
            HTMLNode parent = tree.getParent();

            // Check if the subtree has a parent.
            if (parent == null)
            {
                return null;
            }

            // Otherwise search it, starting after the subtree.
            return find(name, parent, tree, true, true);
        }

        return null;
    }
}
