

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


package com.paulodev.carrot.input.treeExtractor.extractors;


import com.paulodev.carrot.input.treeExtractor.extractors.htmlParser.HTMLNode;
import com.paulodev.carrot.input.treeExtractor.extractors.htmlParser.HTMLTree;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.put.util.text.HtmlHelper;
import org.put.util.xml.JDOMHelper;
import java.util.*;


public class TreeExtractor
{
    private static final Logger log = Logger.getLogger(TreeExtractor.class);
    private TreeNode artificialRootNode;

    private class TreeNode
    {
        protected boolean titleStartAfter = false;
        protected boolean titleInside = false;
        protected boolean titleEndBefore = false;
        protected String titleAtAttribute = null;
        protected boolean descriptionStartAfter = false;
        protected boolean descriptionInside = false;
        protected boolean descriptionEndBefore = false;
        protected String descriptionAtAttribute = null;
        protected boolean urlStartAfter = false;
        protected boolean urlInside = false;
        protected boolean urlEndBefore = false;
        protected String urlAtAttribute = null;
        protected Vector SubNodes = new Vector();
        protected String tag;
        protected TreeNode next = null;
        protected TreeNode prev = null;
        protected TreeNode parent = null;

        public TreeNode(Element nodeDescription)
        {
            tag = nodeDescription.getName().trim().toLowerCase();

            if (nodeDescription.getAttribute("address") != null)
            {
                if (nodeDescription.getAttributeValue("address").equalsIgnoreCase("inside"))
                {
                    urlInside = true;
                }

                if (nodeDescription.getAttributeValue("address").equalsIgnoreCase("beginAfter"))
                {
                    urlStartAfter = true;
                }

                if (nodeDescription.getAttributeValue("address").equalsIgnoreCase("endBefore"))
                {
                    urlEndBefore = true;
                }

                if (
                    nodeDescription.getAttributeValue("address").toLowerCase().startsWith(
                            "attribute:"
                        )
                )
                {
                    String attr = nodeDescription.getAttributeValue("address").toLowerCase();
                    urlAtAttribute = attr.substring(attr.indexOf(":") + 1);
                }
            }

            if (nodeDescription.getAttribute("title") != null)
            {
                if (nodeDescription.getAttributeValue("title").equalsIgnoreCase("inside"))
                {
                    titleInside = true;
                }

                if (nodeDescription.getAttributeValue("title").equalsIgnoreCase("beginAfter"))
                {
                    titleStartAfter = true;
                }

                if (nodeDescription.getAttributeValue("title").equalsIgnoreCase("endBefore"))
                {
                    titleEndBefore = true;
                }

                if (
                    nodeDescription.getAttributeValue("title").toLowerCase().startsWith(
                            "attribute:"
                        )
                )
                {
                    String attr = nodeDescription.getAttributeValue("title").toLowerCase();
                    titleAtAttribute = attr.substring(attr.indexOf(":"));
                }
            }

            if (nodeDescription.getAttribute("description") != null)
            {
                if (nodeDescription.getAttributeValue("description").equalsIgnoreCase("inside"))
                {
                    descriptionInside = true;
                }

                if (nodeDescription.getAttributeValue("description").equalsIgnoreCase("beginAfter"))
                {
                    descriptionStartAfter = true;
                }

                if (nodeDescription.getAttributeValue("description").equalsIgnoreCase("endBefore"))
                {
                    descriptionEndBefore = true;
                }

                if (
                    nodeDescription.getAttributeValue("description").toLowerCase().startsWith(
                            "attribute:"
                        )
                )
                {
                    String attr = nodeDescription.getAttributeValue("description").toLowerCase();
                    descriptionAtAttribute = attr.substring(attr.indexOf(":"));
                }
            }

            TreeNode prevNode = null;

            for (Iterator i = nodeDescription.getChildren().iterator(); i.hasNext();)
            {
                Element e = (Element) i.next();
                TreeNode n = new TreeNode(e);
                n.parent = this;

                // if there is previos node - link actual node to it as a sibling
                if (prevNode != null)
                {
                    prevNode.next = n;
                    n.prev = prevNode;
                }

                prevNode = n;
                SubNodes.add(n);
            }
        }
    }


    private class NodeMatch
    {
        private TreeNode toFind; // node from definition to look for
        private int matchIndex = -1; // fount node index
        private HTMLNode matchNode = null;
        private NodeMatch prev; // previous match node
        private boolean isPrevChild; // indicates if a node is a child of previous node or it's 'sibling'
        private int minLevel; // helper minimal level value for a node
        private int maxLevel; // helper maximum level value for a node
        private int minIndex; // helper minimal index for a node
        private int maxIndex; // helper maximum index for a node
        private Vector allNodes; // all nodes in a Vector

        public NodeMatch(
            Vector allNodes, TreeNode toFind, NodeMatch prev, int minLevel, int maxLevel,
            int minIndex, int maxIndex
        )
        {
            this.allNodes = allNodes;
            this.prev = prev;
            this.toFind = toFind;
            this.minLevel = minLevel;
            this.maxIndex = maxIndex;
            this.minIndex = minIndex;
            this.maxLevel = maxLevel;
        }


        public NodeMatch(Vector allNodes, TreeNode toFind)
        {
            this.allNodes = allNodes;
            this.prev = null;
            this.toFind = toFind;
            this.minLevel = 0;
            this.maxLevel = Integer.MAX_VALUE;
            this.minIndex = 0;
            this.maxIndex = allNodes.size();
        }

        public boolean doFind()
        {
            matchIndex = -1;
            matchNode = null;

            // No search after the end of the document
            if (minIndex >= allNodes.size())
            {
                return false;
            }

            HTMLNode act = (HTMLNode) allNodes.get(minIndex);
            int idx = minIndex;
            boolean found = false;

            while (
                !found && (act.getPosition() <= maxIndex) && (act.getLevel() <= maxLevel)
                    && (idx < allNodes.size())
            )
            {
                if (act.getName().equalsIgnoreCase(toFind.tag) && (act.getLevel() >= minLevel))
                {
                    matchIndex = idx;
                    matchNode = act;
                    found = true;
                }
                else
                {
                    idx++;

                    if (idx < allNodes.size())
                    {
                        act = (HTMLNode) allNodes.get(idx);
                    }
                }
            }

            return found;
        }


        public boolean isFound()
        {
            return matchIndex != -1;
        }


        public int getMatchIndex()
        {
            return matchIndex;
        }
    }


    public class MalformedSnippetException
        extends Exception
    {
        private String msg;

        public MalformedSnippetException(String s)
        {
            msg = s;
        }

        public String toString()
        {
            return msg;
        }
    }


    public class Snippet
    {
        protected int OrdNum;
        protected String title = new String();
        protected String description = new String();
        protected String url = new String();

        public Snippet(Vector v, int index)
            throws MalformedSnippetException
        {
            HTMLNode titleStarted = null;
            HTMLNode descriptionStarted = null;
            HTMLNode urlStarted = null;
            this.OrdNum = index;

            NodeMatch akt;

            for (int i = 0; i < v.size(); i++)
            {
                akt = (NodeMatch) v.elementAt(i);

//	akt.matchNode.match = true;
                if (akt.toFind.descriptionAtAttribute != null)
                {
                    description = description
                        + akt.matchNode.getAttribute(
                            akt.toFind.descriptionAtAttribute.toLowerCase()
                        );
                }

                if (akt.toFind.urlAtAttribute != null)
                {
                    url = akt.matchNode.getAttribute(akt.toFind.urlAtAttribute.toLowerCase());
                }

                if (akt.toFind.titleAtAttribute != null)
                {
                    title = title
                        + akt.matchNode.getAttribute(akt.toFind.titleAtAttribute.toLowerCase());
                }

                if (akt.toFind.descriptionStartAfter)
                {
                    descriptionStarted = akt.matchNode;
                }

                if (akt.toFind.urlStartAfter)
                {
                    urlStarted = akt.matchNode;
                }

                if (akt.toFind.titleStartAfter)
                {
                    titleStarted = akt.matchNode;
                }

                if (
                    akt.toFind.descriptionEndBefore
                        && ((descriptionStarted != null)
                        && !(descriptionStarted.isChild(akt.matchNode)))
                )
                {
                    StringBuffer sb = new StringBuffer();
                    traverseTree(descriptionStarted, akt.matchNode, sb);
                    description = sb.toString();
                    descriptionStarted = null;
                }

                if (
                    akt.toFind.urlEndBefore
                        && ((urlStarted != null) && !(urlStarted.isChild(akt.matchNode)))
                )
                {
                    StringBuffer sb = new StringBuffer();
                    traverseTree(urlStarted, akt.matchNode, sb);
                    url = sb.toString();
                    urlStarted = null;
                }

                if (
                    akt.toFind.titleEndBefore
                        && ((titleStarted != null) && !(titleStarted.isChild(akt.matchNode)))
                )
                {
                    StringBuffer sb = new StringBuffer();
                    traverseTree(titleStarted, akt.matchNode, sb);
                    title = sb.toString();
                    titleStarted = null;
                }

                if (akt.toFind.titleInside)
                {
                    akt.matchNode.hide();
                    title = title + akt.matchNode.toString();
                    akt.matchNode.unhide();
                }

                if (akt.toFind.urlInside)
                {
                    akt.matchNode.hide();
                    url = akt.matchNode.toString();
                    akt.matchNode.unhide();
                }

                if (akt.toFind.descriptionInside)
                {
                    akt.matchNode.hide();
                    description = description + akt.matchNode.toString();
                    akt.matchNode.unhide();
                }
            }

            if ((title != null) && (title.length() != 0))
            {
                title = clearHTMLString(title);
            }
            else
            {
                throw new MalformedSnippetException("No title");
            }

            if ((description != null) && (description.length() != 0))
            {
                description = clearHTMLString(description);
            }
            else
            {
                throw new MalformedSnippetException("No description");
            }

            if ((url != null) && (url.length() != 0))
            {
                url = clearHTMLString(url);
            }
            else
            {
                throw new MalformedSnippetException("No url");
            }
        }

        public boolean traverseTree(HTMLNode start, HTMLNode end, StringBuffer sb)
        {
            HTMLNode act;
            boolean finished = false;

//      if (start.getChildrenVector() != null)
//	act = start;
//      else
            act = start.getParent();

            Vector chld = act.getChildrenVector();
            int idx;

//      if (act == start)
//	idx = 0;
//      else
            idx = chld.indexOf(start) + 1;
            log.debug("<< Starting >>");

            while (!finished)
            {
                for (int i = idx; i < chld.size(); i++)
                {
                    // exit if end node is finally found
                    if (((HTMLNode) chld.get(i)).getPosition() == end.getPosition())
                    {
                        finished = true;

                        break;
                    }

                    // append the whole subtree if end tag isn't within it
                    if (((HTMLNode) chld.get(i)).isAfter(end))
                    {
                        log.debug(
                            "Full append -> " + ((HTMLNode) chld.get(i)).getName() + " from -> "
                            + act.getName()
                        );
                        sb.append(chld.get(i));
                    }
                    else if (((HTMLNode) chld.get(i)).isChild(end))
                    {
                        // end node is within actual - jump into it
                        log.debug(
                            "Running into -> " + ((HTMLNode) chld.get(i)).getName() + " from -> "
                            + act.getName()
                        );
                        act = (HTMLNode) chld.get(i);
                        act.openingTag(sb);
                        chld = act.getChildrenVector();

                        // begin loop from 0
                        i = -1;

                        if (chld == null)
                        {
                            log.fatal(
                                "Children table empty while expecting to end-tag to be a child at tag:"
                                + act.getName()
                            );
                            throw new AssertionException(
                                "Children table empty while expecting to end-tag to be a child at tag:"
                                + act.getName()
                            );
                        }
                    }
                    else
                    {
                        log.fatal(
                            "Unexpected situation: Tag -> " + act.getName() + "("
                            + act.getPosition() + ", " + act.getMaxPosition() + "), Child -> "
                            + ((HTMLNode) chld.get(i)).getName() + "("
                            + ((HTMLNode) chld.get(i)).getPosition() + "), End -> " + end.getName()
                            + "(" + end.getPosition() + ")"
                        );
                    }
                }

                if (!finished)
                {
                    log.debug("Jumping up from -> " + act.getName());

                    if (act.getParent() == null)
                    {
                        log.fatal("No finish and no parent of tag: " + act.getName());
                        throw new AssertionException(
                            "No finish and no parent of tag: " + act.getName()
                        );
                    }

                    chld = act.getParent().getChildrenVector();
                    idx = chld.indexOf(act) + 1;
                    act.closingTag(sb);
                    act = act.getParent();
                }
            }

            return finished;
        }


        public String clearHTMLString(String s)
        {
            return com.dawidweiss.carrot.util.StringUtils.entitiesToCharacters(s, false).trim();
        }


        public String toString()
        {
            String res = new String();
            res += ("<document id=\"" + OrdNum + "\">\n\t<title>");

//      res += xmlencode(title);
            res += xmlencode(HtmlHelper.removeHtmlTags(title));
            res += "</title>\n";

            res += "\t<url>";
            res += xmlencode(url);
            res += "</url>\n";

            res += "\t<snippet>";
            res += xmlencode(HtmlHelper.removeHtmlTags(description));

//      res += xmlencode(description);
            res += "</snippet>\n";
            res += "</document>\n";

            return res;
        }


        private String xmlencode(String x)
        {
            return "<![CDATA[" + x + "]]>";
        }
    }

    public TreeExtractor(Element treeDescriptionXML)
    {
        Element e = JDOMHelper.getElement("extractor/snippet", treeDescriptionXML);
        artificialRootNode = new TreeNode(e);
    }

    /**
     * Extracts snippets from the tag tree
     *
     * @param Search result page HTML tree
     */
    public Enumeration parseTree(HTMLTree t, int hasSnippets)
    {
        Vector snippets = new Vector();
        int act = 0;
        NodeMatch lastMatch;
        boolean finished = false;

        while (!finished)
        {
            Stack s = new Stack();
            lastMatch = parseSubTree(
                    t, act, t.getAllNodes().size() - 1, artificialRootNode, s, null
                );

            if ((lastMatch == null) || !lastMatch.isFound())
            {
                finished = true;
            }
            else
            {
                act = lastMatch.getMatchIndex() + 1;

                try
                {
                    snippets.add(new Snippet(s, hasSnippets));
                    hasSnippets++;
                }
                catch (MalformedSnippetException e)
                {
                    log.debug("Malformed snippet: " + e);
                }
            }
        }

        ;

        return snippets.elements();
    }


    private NodeMatch parseSubTree(
        HTMLTree t, int startAt, int endAt, TreeNode node, Stack s, NodeMatch prev
    )
    {
//    log.debug("Parsing subtree of node: " + node.tag  + ", startAt: " + startAt);
        int minLevel = (prev != null) ? (prev.matchNode.getLevel() + 1)
                                      : 0;
        int maxLevel = (prev != null) ? (prev.matchNode.getLevel() + 4)
                                      : Integer.MAX_VALUE;
        boolean done = false;
        NodeMatch act = new NodeMatch(
                t.getAllNodes(), (TreeNode) node.SubNodes.firstElement(), prev, minLevel, maxLevel,
                startAt, t.getAllNodes().size()
            );

        while (!done)
        {
            boolean good;

            if (good = act.doFind())
            {
                s.push(act);

                // first check if there are any subnodes to find
                if (!act.toFind.SubNodes.isEmpty())
                {
                    good = (parseSubTree(
                            t, act.getMatchIndex() + 1, act.matchNode.getMaxPosition(), act.toFind,
                            s, act
                        ) != null);
                }

                // if we're done with subnodes, find next sibling
                if (good)
                {
                    if (act.toFind.next != null)
                    {
                        act = new NodeMatch(
                                t.getAllNodes(), act.toFind.next, act, minLevel, maxLevel,
                                act.getMatchIndex() + 1, endAt
                            );
                    }

                    // otherwise jump upwards to parent
                    else
                    {
                        done = true;
                    }
                }
            }

            // check if there is any failure
            if (!good)
            {
                // check if there is anything on stack
                if (!s.isEmpty())
                {
                    // restart the search of previous node after failure
                    act = (NodeMatch) s.peek();

                    // if the top object on the stack is the caller's object - return failure (null)
                    if (act == prev)
                    {
                        log.debug(
                            "Going back to parent:" + act.toFind.tag + " from position: "
                            + act.minIndex
                        );
                        act = null;

                        break;
                    }

                    // otherwise pop it and restart the search just after last match
                    s.pop();
                    act.minIndex = act.getMatchIndex() + 1;
                    log.debug(
                        "Restarting to search:" + act.toFind.tag + " from position: "
                        + act.minIndex
                    );
                }
                else
                {
                    log.debug("The stack is empty !. Returning null");
                    act = null;
                    done = true;
                }
            }
        }

        return act;
    }
}
