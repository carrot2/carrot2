

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
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


import org.apache.log4j.*;
import java.util.*;


/**
 * This class represents a single node within an HTML tree. Each node has a name, zero or more
 * attributes and possibly some content. Nodes can appear within the content of other nodes.
 * 
 * <p>
 * End tags do not appear since they only indicate 'end-of-content'. To prevent the system
 * searching for the end of standalone tags, a dynamic list has been implemented. When the
 * HTMLNode class is resolved a setup method is called adding a set of default standalone tags to
 * the list. Standalone tags can then be added and removed dynamically using static method calls.
 * </p>
 * 
 * <p>
 * The list is the only way the internal code can tell whether a tag is standalone. If a problem
 * occurs the tree structure would still be sound, but it would not be accurate, so while the form
 * of the HTML would be conserved, searches would not operate correctly.
 * </p>
 *
 * @author <a href="http://www.strath.ac.uk/~ras97108/">David McNicol</a>
 *
 * @see HTMLTree
 */
public class HTMLNode
{
    protected final Logger log = Logger.getLogger(getClass());
    protected HTMLNode parent; // Refers to this node's parent.
    protected String name; // Stores the name of the HTML node.
    protected String classedName; // Stores the name of the HTML node along with it's class
    protected AttributeList attr; // List of element's attributes.
    protected Vector children; // Stores the HTML node's children.
    private boolean hidden; // True if the node is not to be printed.
    protected int level; // Distance from the root
    protected int num; // Ordinary number of the node
    protected int maxNum; // Maximum ordinary number of all children
    public int highlight = 0; // color it
    private boolean standalone = false; //if the node is standalone

    /**
     * Initializes a new HTMLNode.
     *
     * @param owner the owner HTMLTree
     * @param parent the parent HTMLNode
     * @param src enumeration of tag tokens.
     * @param level the depth of the node
     * @param num the ordinary number of the node
     */
    public void init(HTMLTree owner, HTMLNode parent, boolean tag, int level, int [] num)
    {
        // Store the reference to the node's parent.
        this.parent = parent;

        // Set the node to be unhidden by default.
        hidden = false;

        // Set the level and number
        this.level = level;
        this.num = num[0];
        num[0]++;

        // add the node to the owner tree collection
        if (tag)
        {
            owner.appendNode(this);
        }
    }

    /**
     * Constructs a new HTMLNode.
     *
     * @param owner the owner HTMLTree
     * @param parent the parent HTMLNode
     * @param src enumeration of tag tokens.
     * @param level the depth of the node
     * @param num the ordinary number of the node
     */
    public HTMLNode(HTMLTree owner, HTMLNode parent, boolean tag, int level, int [] num)
    {
        init(owner, parent, tag, level, num);
    }


    /**
     * Constructs a new HTMLNode.
     *
     * @param tag the TagToken representing the start of this node.
     * @param standalone true if the tag does not have any content.
     * @param src enumeration of tag tokens.
     * @param level the depth of the node
     * @param num the ordinary number of the node
     */
    public HTMLNode(
        HTMLTree owner, TagToken tag, HTMLNode parent, Enumeration src, int level, int [] num
    )
    {
        init(owner, parent, tag != null, level, num);

        // Check if the given tag is null.
        if (tag != null)
        {
            // Store the node's name.
            name = tag.getName();

            // set this to distingish same tags with different classes
            if (tag.getAttributes().exists("class"))
            {
                classedName = name + " class=" + tag.getAttributes().getQuoted("class");
            }
            else
            {
                classedName = name;
            }

            // Store the node's attribute list.
            attr = tag.getAttributes();

            // Get the node's children if needed.
            if (HTMLNode.isStandalone(name) || HTMLNode.canBeStandalone(name))
            {
                standalone = true;
                children = null;
            }
            else
            {
                children = parseChildren(owner, src, num);
            }

            maxNum = num[0] - 1;
        }
        else
        {
            // Otherwise, set the name and attributes to null.
            name = null;
            attr = null;

            // Get the node's children from the enumeration.
            children = parseChildren(owner, src, num);
        }
    }

    public boolean isChild(HTMLNode node)
    {
        return ((node.getPosition() > getPosition()) && (node.getPosition() <= getMaxPosition()));
    }


    public boolean isAfter(HTMLNode node)
    {
        return (node.getPosition() > getMaxPosition());
    }


    public void assignChildren(Vector nodes, int startIndex, int endIndex)
    {
        log.debug("Assigning children to: " + getName());

        if (endIndex < startIndex)
        {
            return;
        }

        children = new Vector(endIndex - startIndex + 1);
        standalone = false;

        for (int i = startIndex; i <= endIndex; i++)
        {
            log.debug("Assigning: " + ((HTMLNode) nodes.get(i)).getName());
            children.addElement(nodes.get(i));
            ((HTMLNode) nodes.get(i)).incLevel();
            ((HTMLNode) nodes.get(i)).parent = this;
        }

        maxNum = ((HTMLNode) nodes.get(endIndex)).getMaxPosition();

        // remove the nodes from the list
        for (int i = endIndex; i >= startIndex; i--)
        {
            nodes.remove(i);
        }

        log.debug("END assigning children to: " + getName());
    }


    public void incLevel()
    {
        if (children != null)
        {
            for (int i = 0; i < children.size(); i++)
            {
                ((HTMLNode) children.get(i)).incLevel();
            }
        }

        level++;
    }


    /**
     * Returns the name of this node.
     */
    public String getName()
    {
        return name;
    }


    /**
     * Returns the name of this node along with it's class.
     */
    public String getClassedName()
    {
        return classedName;
    }


    /**
     * Returns the node's parent node.
     */
    public HTMLNode getParent()
    {
        return parent;
    }


    /**
     * Returns the node's children.
     */
    public Enumeration getChildren()
    {
        // Return nothing if the node has any children.
        if (children == null)
        {
            return null;
        }

        return children.elements();
    }


    public Vector getChildrenVector()
    {
        return children;
    }


    /**
     * Returns true if the node is currently hidden.
     */
    public boolean isHidden()
    {
        return hidden;
    }


    /**
     * Returns the node's level
     */
    public int getLevel()
    {
        return level;
    }


    /**
     * Returns the node's position
     */
    public int getPosition()
    {
        return num;
    }


    /**
     * Returns the node's children maximum position
     */
    public int getMaxPosition()
    {
        return maxNum;
    }


    /**
     * Hides the node.
     */
    public void hide()
    {
        hidden = true;
    }


    /**
     * "Unhides" the node.
     */
    public void unhide()
    {
        hidden = false;
    }


    /**
     * Returns the value of the attribute with the given name.
     *
     * @param name the name of the attribute.
     */
    public String getAttribute(String name)
    {
        // Check that the attribute list is there.
        if (attr == null)
        {
            return null;
        }

        // Return the value associated with the attribute name.
        return (String) attr.get(name);
    }


    /**
     * Returns an enumeration of attributes defined in this node.
     */
    public Enumeration getAttributes()
    {
        // Check that the attribute list has been defined.
        if (attr == null)
        {
            return null;
        }

        // Return an enumeration of all of the attribute names.
        return attr.names();
    }


    /**
     * Returns an attribute with all double quote characters escaped with a backslash.
     *
     * @param name the name of the attribute.
     */
    public String getQuotedAttribute(String name)
    {
        // Check that the attribute list is there.
        if (attr == null)
        {
            return null;
        }

        // Return the quoted version.
        return attr.getQuoted(name);
    }


    /**
     * Returns a string version of the attribute and its value.
     *
     * @param name the name of the attribute.
     */
    public String getAttributeToString(String name)
    {
        // Check that the attribute list is there.
        if (attr == null)
        {
            return null;
        }

        // Return the string version.
        return attr.toString(name);
    }


    /**
     * Returns a string version of the HTMLNode. If the node is currently hidden then return an
     * empty string.
     */
    public String toString()
    {
        StringBuffer sb; // Stores the string to be returned.

        // Get a new StringBuffer.
        sb = new StringBuffer();

        openingTag(sb);

        // Return if the node is standalone.
        if (standalone && (children == null))
        {
            return sb.toString();
        }

        childrenTags(sb);

        closingTag(sb);

        // Return the string version.
        return sb.toString();
    }


    public void buildTabs(StringBuffer res)
    {
        for (int i = 0; i < level; i++)
        {
            res.append('\t');
        }
    }


    public void openingTag(StringBuffer sb)
    {
        if (!hidden && (name != null))
        {
            buildTabs(sb);

            // Write the opening of the tag.
            sb.append('<');

            // Write the tag's name.
            sb.append(name);

            // Check if there are any attributes.
            if ((attr != null) && (attr.size() > 0))
            {
                // Print string version of the attributes.
                sb.append(" " + attr);
            }

            // Finish off the tag.
            sb.append(">\n");
        }
    }


    public void childrenTags(StringBuffer sb)
    {
        // Otherwise, check if the node has any children.
        if (((name == null) || !name.equals("")) && (children != null) && (children.size() > 0))
        {
            // Get a list of all of the children.
            Enumeration list = children.elements();

            while (list.hasMoreElements())
            {
                // Get the next node from the list.
                Object o = list.nextElement();

                // Write it.
                sb.append(o.toString());
            }
        }
    }


    public void closingTag(StringBuffer sb)
    {
        if (!hidden && (name != null))
        {
            buildTabs(sb);

            // Write the end tag.
            sb.append("</").append(name).append(">\n");
        }
    }


    /**
     * Sets the node's parent to the specified HTMLNode.
     *
     * @param parent the new parent.
     */
    public void setParent(HTMLNode parent)
    {
        this.parent = parent;
    }


    /**
     * Returns true if an attribute with the given name exists.
     *
     * @param name the name of the attribute.
     */
    public boolean isAttribute(String name)
    {
        // Check that the attribute list is there.
        if (attr == null)
        {
            return false;
        }

        // Check the table for an attribute with that name.
        return attr.exists(name);
    }


    /**
     * Adds a new attribute to the node's attribute list with the specified value. If the attribute
     * already exists the old value is overwritten.
     *
     * @param name the name of the attribute.
     * @param value the value of the attribute.
     */
    public void addAttribute(String name, String value)
    {
        // Return if the attribute list is not there.
        if (attr == null)
        {
            return;
        }

        // Otherwise, add the name/value pair to the list.
        attr.set(name, value);
    }


    /**
     * Adds an object to the end of this node's content
     *
     * @param child the node to be added.
     */
    public void addChild(Object child)
    {
        // Return if the child is invalid.
        if (child == null)
        {
            return;
        }

        // Check that this node has no children.
        if (children == null)
        {
            return;
        }

        // Add the child if it is a string.
        if (child instanceof String)
        {
            children.addElement(child);

            return;
        }

        // Add the child and set its parent if it is an HTMLNode.
        if (child instanceof HTMLNode)
        {
            children.addElement(child);
            ((HTMLNode) child).setParent(this);

            return;
        }
    }


    /**
     * Removes the specified HTMLNode from the current node's list of children.
     *
     * @param child the node to be removed.
     */
    public void removeChild(HTMLNode child)
    {
        // Return if the child is not defined properly
        if (child == null)
        {
            return;
        }

        // Return if the list of children is not defined properly.
        if (children == null)
        {
            return;
        }

        // Otherwise, remove the child if it is on the list.
        children.removeElement(child);
    }


    /**
     * Adds an object to this node's content before the specified child node.
     *
     * @param child the object to be added.
     * @param before the node before which the child will be placed.
     */
    public void addChildBefore(Object child, HTMLNode before)
    {
        int total; // Total number of child nodes.
        int idx; // Index of the 'before' node.

        // Return if the child is invalid.
        if (child == null)
        {
            return;
        }

        // Return if this node has no children.
        if (children == null)
        {
            return;
        }

        // Add the child at the beginning if the before node is
        // invalid.
        if (before == null)
        {
            addChild(child);

            return;
        }

        total = children.size();
        idx = children.indexOf(before);

        // Add the child to the beginning if the 'before' node
        // was not found.
        if (idx < 0)
        {
            idx = 0;

            // Return if the child is not of the right type.
        }

        if (!((child instanceof String) || (child instanceof HTMLNode)))
        {
            return;
        }

        // Check if the 'before' node is the last node.
        if (idx == (total - 1))
        {
            // Add the child to the end of the list.
            children.addElement(child);
        }
        else
        {
            // Add the child before the 'before' node.
            children.insertElementAt(child, idx);
        }

        // If the child is an HTMLNode, set its parent.
        if (child instanceof HTMLNode)
        {
            ((HTMLNode) child).setParent(this);
        }
    }


    /**
     * Removes an attribute with the specified name from the attribute list.
     *
     * @param name the name of the attribute to remove.
     */
    public void removeAttribute(String name)
    {
        // Return if the attribute list is not there.
        if (attr == null)
        {
            return;
        }

        // Otherwise, remove the attribute from the list.
        attr.unset(name);
    }


    /**
     * Returns the node after this one in the parent's list of children.
     */
    public HTMLNode nextSibling()
    {
        // Return nothing if the node has no parent.
        if (parent == null)
        {
            return null;
        }

        // Ask the parent to return the node after this one.
        return parent.nextChild(this);
    }


    /**
     * Returns the node before this one in the parent's list of children.
     */
    public HTMLNode previousSibling()
    {
        // Return nothing if the node has no parent.
        if (parent == null)
        {
            return null;
        }

        // Ask the parent to return the node before this one.
        return parent.previousChild(this);
    }


    /**
     * Returns the first child of this node.
     */
    public HTMLNode firstChild()
    {
        Enumeration list; // Enumeration of this node's children.
        Object curr; // Current node from the list.

        // Return nothing if this node has no children.
        if (children == null)
        {
            return null;
        }

        // Return the first child node.
        list = children.elements();

        while (list.hasMoreElements())
        {
            curr = list.nextElement();

            // Return the first HTMLNode in the list.
            if (curr instanceof HTMLNode)
            {
                return (HTMLNode) curr;
            }
        }

        // Return nothing if there were no HTMLNodes in the list.
        return null;
    }


    /**
     * Returns the HTMLNode after the specified one in this nodes content.
     *
     * @param child the HTMLNode before the one we want.
     */
    public HTMLNode nextChild(HTMLNode child)
    {
        Enumeration list; // List of this node's children.
        Object curr; // Current object from the list.
        boolean getNext = false; // True when child has been found.

        // Return nothing if this node has no children.
        if (children == null)
        {
            return null;
        }

        // Get a list of this node's children
        list = children.elements();

        while (list.hasMoreElements())
        {
            curr = list.nextElement();

            // Check if we have found the specified child.
            if (getNext)
            {
                // Return the next HTMLNode we encounter.
                if (curr instanceof HTMLNode)
                {
                    return (HTMLNode) curr;
                }
            }
            else
            {
                // Check if we have found the specified child.
                if (curr == child)
                {
                    getNext = true;
                }
            }
        }

        return null;
    }


    /**
     * Returns the HTMLNode before the specified one in this nodes content.
     *
     * @param child the HTMLNode after the one we want.
     */
    public HTMLNode previousChild(HTMLNode child)
    {
        Enumeration list; // List of this node's children.
        Object curr; // Current object from the list.
        HTMLNode prev = null; // Stores last found HTMLNode.

        // Return nothing if this node has no children.
        if (children == null)
        {
            return null;
        }

        // Get a list of this node's children
        list = children.elements();

        while (list.hasMoreElements())
        {
            curr = list.nextElement();

            // Check if we have found the specified child.
            if (curr == child)
            {
                return prev;
            }

            // Check if curr is an HTMLNode.
            if (curr instanceof HTMLNode)
            {
                // Make curr the previously found HTMLNode.
                prev = (HTMLNode) curr;
            }
        }

        return null;
    }


    private void processPossibleStandAlone(Vector possibleStandAlone, Vector store, TagToken tag)
    {
        for (int i = possibleStandAlone.size() - 1; i >= 0; i--)
        {
            if (((HTMLNode) possibleStandAlone.get(i)).getName().equals(tag.getName()))
            {
                ((HTMLNode) possibleStandAlone.get(i)).assignChildren(
                    store, store.indexOf(possibleStandAlone.get(i)) + 1, store.size() - 1
                );
                possibleStandAlone.setSize(i);

                return;
            }
        }
    }


    /**
     * Parses the contents of this HTML node from the enumeration of tokens provided.
     *
     * @param src an enumeration of tokens.
     * @param num the ordinary number
     */
    private Vector parseChildren(HTMLTree owner, Enumeration src, int [] num)
    {
        // Create a new Vector to store the contents.
        Vector store = new Vector();
        Vector possibleStandAlone = new Vector();

        // Loop round the enumeration of tokens.
        while (src.hasMoreElements())
        {
            // Get the next token from the enumeration.
            Object token = src.nextElement();

            // Check if the token is simple text.
            if (token instanceof TextToken)
            {
                // Cast the token into type TextToken.
                TextToken text = (TextToken) token;

                // Add the text string to the vector.
                if (text.getText().toString().trim().length() > 0)
                {
                    store.addElement(new HTMLTextNode(owner, text.getText(), this, level + 1, num));
                }

                continue;
            }

            // Check if the token is a tag.
            if (token instanceof TagToken)
            {
                // Cast the token into type TagToken.
                TagToken tag = (TagToken) token;

                // Check if the token is an end tag.
                if (tag.isEndTag())
                {
                    // Break if the end tags name matches.
                    if ((name != null) && name.equals(tag.getName()))
                    {
                        break;
                    }

                    // Check if the closure tag can close any of possible standalone tags
                    if (canBeStandalone(tag.getName()))
                    {
                        processPossibleStandAlone(possibleStandAlone, store, tag);
                    }

                    // Otherwise ignore the end tag.
                    continue;
                }

                if (canBeStandaloneAndClosed(tag.getName()))
                {
/*            Enumeration e = possibleStandAlone.elements();
   while (e.hasMoreElements()) {
       HTMLNode n = (HTMLNode) e.nextElement();
       if (n.getName().equals(tag.getName()))

               }*/
                    processPossibleStandAlone(possibleStandAlone, store, tag);
                }

                // Otherwise make it into an HTMLNode.
                HTMLNode he = new HTMLNode(owner, tag, this, src, level + 1, num);

                if (canBeStandalone(he.getName()))
                {
                    possibleStandAlone.add(he);
                }

                // Add the node to the vector.
                store.addElement(he);
            }
        }

        // check if trere are any possible standlone to be closed
        boolean changed = true;

        while (changed)
        {
            changed = false;

            for (int i = possibleStandAlone.size() - 1; i >= 0; i--)
            {
                HTMLNode node = (HTMLNode) possibleStandAlone.get(i);

                if (canBeStandaloneAndClosed(node.getName()))
                {
                    changed = true;
                    node.assignChildren(store, store.indexOf(node) + 1, store.size() - 1);
                    possibleStandAlone.setSize(i);

                    break;
                }
            }
        }

        if (store.size() > 0)
        {
            return store;
        }
        else
        {
            return null;
        }
    }

    /** String of default node names which are standalone. */
    private static String [] defaultStandaloneList = 
    {
        "area", "base", "basefont", "bgsound", "br", "frame", "hr", "img", "input", "isindex",
        "link", "meta", "nextid", "option", "overlay", "param", "tab", "wbr", "!", "!--", "!DOCTYPE"
    };

    /** String of default node names which can be standalone. */
    private static String [] defaultCanBeStandAloneList = 
    {
        "p", "li", "font", "td", "col", "dd", "dt", "dl"
    };

    /**
     * String of default node names which can be standalone and should be closed before the next
     * occurence.
     */
    private static String [] defaultCanBeStandAloneAndClosedList = 
    {
        "p", "li", "font", "td", "col", "dd", "dt", "dl"
    };

    // Full list of standalone names.
    private static Vector standaloneList = null;

    // Full list of possible standalone names.
    private static Vector canBeStandaloneList = null;

    // Full list of possible standalone names which shoult be closed.
    private static Vector canBeStandaloneAndClosedList = null;

    // Load the default standalones into the list after class resolution.
    static
    {
        setupStandaloneList();
    }

    /**
     * Utility method which people can use to find out exactly which nodes are in the default
     * standalone list. The default list is printed to the standard output.
     */
    public static void printDefaultStandaloneList()
    {
        System.out.println(defaultStandaloneList);
    }


    /**
     * Adds the specified string to the standalone list.
     *
     * @param name the new standalone name.
     */
    public static void addStandalone(String name)
    {
        // Check if the list has been initialized first.
        if (standaloneList == null)
        {
            return;
        }

        // Convert the String to lower case.
        String lc = name.toLowerCase();

        // Check that the list does not have the String already.
        if (standaloneList.contains(lc))
        {
            return;
        }

        // Otherwise add the lowercase string to the list.
        standaloneList.addElement(lc);
    }


    /**
     * Removes the specified string from the standalone list.
     *
     * @param name the standalone name to remove.
     */
    public static void removeStandalone(String name)
    {
        // Check if the standaloneList has been initialized first.
        if (standaloneList == null)
        {
            return;
        }

        // Convert the String to lower case.
        String lc = name.toLowerCase();

        // Remove the lowercase string from the list.
        standaloneList.removeElement(lc);
    }


    /**
     * Checks the standalone list to see if it mentions the specified tag name and returns true if
     * so.
     *
     * @param name the tag name to check against the list.
     */
    public static boolean isStandalone(String name)
    {
        // Check if the standaloneList has been initialized first.
        if (standaloneList == null)
        {
            return true;
        }

        // Otherwise check the list to see if it contains the tag name.
        return standaloneList.contains(name);
    }


    public static boolean canBeStandalone(String name)
    {
        // Check if the standaloneList has been initialized first.
        if (canBeStandaloneList == null)
        {
            return true;
        }

        // Otherwise check the list to see if it contains the tag name.
        return canBeStandaloneList.contains(name);
    }


    public static boolean canBeStandaloneAndClosed(String name)
    {
        // Check if the standaloneList has been initialized first.
        if (canBeStandaloneAndClosedList == null)
        {
            return true;
        }

        // Otherwise check the list to see if it contains the tag name.
        return canBeStandaloneAndClosedList.contains(name);
    }


    /**
     * Sets up the standalone vector at runtime using the list of default standalone tags. New
     * standalone tags can then be added to the vector.
     * 
     * <p>
     * This method will only be executed once, since it is guarded by a private boolean variable.
     * </p>
     */
    private static void setupStandaloneList()
    {
        // Create a new vector to store the defaults.
        standaloneList = new Vector(defaultStandaloneList.length);
        canBeStandaloneList = new Vector(defaultCanBeStandAloneList.length);
        canBeStandaloneAndClosedList = new Vector(defaultCanBeStandAloneAndClosedList.length);

        // Add all of the strings in the default list.
        for (int i = 0; i < defaultStandaloneList.length; i++)
        {
            standaloneList.addElement(defaultStandaloneList[i]);
        }

        // Add all of the strings in the default list.
        for (int i = 0; i < defaultCanBeStandAloneList.length; i++)
        {
            canBeStandaloneList.addElement(defaultCanBeStandAloneList[i]);
        }

        // Add all of the strings in the default list.
        for (int i = 0; i < defaultCanBeStandAloneAndClosedList.length; i++)
        {
            canBeStandaloneAndClosedList.addElement(defaultCanBeStandAloneAndClosedList[i]);
        }
    }
}
