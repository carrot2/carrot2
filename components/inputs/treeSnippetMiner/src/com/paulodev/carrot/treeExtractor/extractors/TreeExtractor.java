package com.paulodev.carrot.treeExtractor.extractors;

/**
 * <p>Description: Extractor class</p>
 * <p>Copyright: Copyright (c) 2002 Dawid Weiss, Institute of Computing Science, Poznan University of Technology</p>
 * <p>Company: Institute of Computing Science, Poznan University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

import java.util.*;

import org.apache.log4j.*;
import org.jdom.*;
import org.put.util.xml.*;
import com.paulodev.carrot.util.html.parser.*;
import java.net.*;

public class TreeExtractor
{

    private static final Logger log = Logger.getLogger(TreeExtractor.class);

    private TreeNode artificialRootNode;

    private Vector searchItems = new Vector();

    public static String clearURL(String val)
        throws MalformedURLException {
        if (val.length() < 5)
            throw new MalformedURLException();
        String[] tmp = val.split(" ");
        if (tmp.length == 0)
            throw new MalformedURLException();
        val = tmp[0];
        if (val.indexOf(".") == -1)
            throw new MalformedURLException();
        if (!val.startsWith("http:") && !val.startsWith("ftp:"))
            val = "http://" + val;
        return val;
    }

    public class TreeNode
    {
        protected Vector SubNodes = new Vector();
        protected String tag;
        protected String cssClass = null;

        protected TreeNode next = null;
        protected TreeNode prev = null;
        protected TreeNode parent = null;

        protected boolean optional = false;

        private Vector SearchItems = new Vector();

        public Enumeration getSearchItems()
        {
            return SearchItems.elements();
        }

        public TreeNode(Element nodeDescription, TreeExtractor extractor)
        {
            optional = nodeDescription.getAttributeValue("optional") == null ?
                false :
                nodeDescription.getAttributeValue("optional").
                equalsIgnoreCase("true");
            tag = nodeDescription.getName().trim().toLowerCase();
            Enumeration en = extractor.getSearchItems();
            while (en.hasMoreElements())
            {
                SearchItem i = (SearchItem)en.nextElement();
                if (nodeDescription.getAttribute(i.getName()) != null)
                {
                    if (nodeDescription.getAttributeValue(i.getName()).
                        equalsIgnoreCase("inside"))
                    {
                        SearchItems.add(i.newOccurence(SearchItem.
                            SearchItemOccurence.KIND_INSIDE));
                    }
                    else if (nodeDescription.getAttributeValue(i.getName()).
                             equalsIgnoreCase("beginAfter"))
                    {
                        SearchItems.add(i.newOccurence(SearchItem.
                            SearchItemOccurence.KIND_BEGINAFTER));
                    }
                    else if (nodeDescription.getAttributeValue(i.getName()).
                             equalsIgnoreCase("beginOn"))
                    {
                        SearchItems.add(i.newOccurence(SearchItem.
                            SearchItemOccurence.KIND_BEGINON));
                    }
                    else if (nodeDescription.getAttributeValue(i.getName()).
                             equalsIgnoreCase("endBefore"))
                    {
                        SearchItems.add(i.newOccurence(SearchItem.
                            SearchItemOccurence.KIND_ENDBEFORE));
                    }
                    else if (nodeDescription.getAttributeValue(i.getName()).
                             toLowerCase().
                             startsWith("attribute:"))
                    {
                        String tmp = nodeDescription.getAttributeValue(i.
                            getName()).toLowerCase();
                        String attr = tmp.substring(tmp.indexOf(":") + 1);
                        SearchItems.add(i.newOccurence(SearchItem.
                            SearchItemOccurence.KIND_ATTRIBUTE, attr));
                    }
                }
            }
            if (nodeDescription.getAttribute("class") != null)
            {
                cssClass = nodeDescription.getAttributeValue("class");

            }
            TreeNode prevNode = null;
            for (Iterator i = nodeDescription.getChildren().iterator();
                 i.hasNext(); )
            {
                Element e = (Element)i.next();
                TreeNode n = new TreeNode(e, extractor);
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
        private boolean found = false;
        private int matchIndex = -1; // fount node index
        private HTMLNode matchNode = null;
        private NodeMatch parent;
        private int minLevel; // helper minimal level value for a node
        private int maxLevel; // helper maximum level value for a node
        private int minIndex; // helper minimal index for a node
        private int maxIndex; // helper maximum index for a node
        private Vector allNodes; // all nodes in a Vector
        public Vector SubNodes;

        public NodeMatch(Vector allNodes, TreeNode toFind, NodeMatch parent,
                         int minLevel, int maxLevel, int minIndex, int maxIndex)
        {
            this.allNodes = allNodes;
            this.parent = parent;
            this.toFind = toFind;
            this.minLevel = minLevel;
            this.maxIndex = maxIndex;
            this.minIndex = minIndex;
            this.maxLevel = maxLevel;
        }

        public NodeMatch(Vector allNodes, TreeNode toFind)
        {
            this.allNodes = allNodes;
            this.parent = null;
            this.toFind = toFind;
            this.minLevel = 0;
            this.maxLevel = Integer.MAX_VALUE;
            this.minIndex = 0;
            this.maxIndex = allNodes.size();
        }

        public void fillStack(Stack s) {
            s.add(this);
            if (SubNodes != null)
                for (int i = 0; i < SubNodes.size(); i++)
                    ((NodeMatch) SubNodes.get(i)).fillStack(s);
        }

        public boolean doFind(boolean skipOptional)
        {
            matchIndex = -1;
            matchNode = null;
            // No search after the end of the document
            if (minIndex >= allNodes.size())
            {
                return false;
            }
            if (toFind.optional && skipOptional)
            {
                matchNode = null;
                matchIndex = -1;
                return true;
            }
            HTMLNode act = (HTMLNode)allNodes.get(minIndex);
            int idx = minIndex;
            boolean found = false;
            while (!found && act.getPosition() <= maxIndex &&
                   act.getLevel() <= maxLevel && idx < allNodes.size())
            {
                if (act.getName() != null &&
                    act.getName().equalsIgnoreCase(toFind.tag)
                    &&
                    (toFind.cssClass == null ||
                     toFind.cssClass.equalsIgnoreCase(act.getAttribute("class")))
                    && (act.getLevel() >= minLevel))
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
                        act = (HTMLNode)allNodes.get(idx);
                    }
                }
            }
            this.found = found;
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

    public class SnippetBuilder
    {
        private class BuilderHelper
        {
            public BuilderHelper(SearchItem item)
            {
                this.item = item;
            }

            public SearchItem item;
            public boolean after;
            public HTMLNode started = null;
            public String value = "";
        }

        protected int OrdNum;
        protected Hashtable items = new Hashtable();
        protected Vector allNodes = new Vector();

        public SnippetBuilder(Vector v, Vector treeNodes, int index)
            throws MalformedSnippetException
        {
            this.OrdNum = index;
            Enumeration en_items = getSearchItems();
            while (en_items.hasMoreElements())
            {
                SearchItem it = (SearchItem)en_items.nextElement();
                items.put(it.getName(),
                          new TreeExtractor.SnippetBuilder.BuilderHelper(it));
            }
            NodeMatch akt;
            for (int i = 0; i < v.size(); i++)
            {
                akt = (NodeMatch)v.elementAt(i);
                allNodes.add(akt.matchNode);
                Enumeration en = akt.toFind.getSearchItems();
                while (en.hasMoreElements())
                {
                    SearchItem.SearchItemOccurence oc = (SearchItem.
                        SearchItemOccurence)en.nextElement();
                    BuilderHelper bh = (BuilderHelper)items.get(oc.
                        getSearchItem().getName());
                    if (oc.getKind() == oc.KIND_ATTRIBUTE)
                    {
                        bh.value = bh.value +
                            akt.matchNode.getAttribute(oc.getField().
                            toLowerCase());
                    }
                    else if (oc.getKind() == oc.KIND_BEGINAFTER)
                    {
                        bh.started = akt.matchNode;
                        bh.after = true;
                    }
                    else if (oc.getKind() == oc.KIND_BEGINON)
                    {
                        bh.started = akt.matchNode;
                        bh.after = false;
                    }
                    else if (oc.getKind() == oc.KIND_ENDBEFORE && bh.started != null)
                    {
                        StringBuffer sb = new StringBuffer();
                        traverseTree2(bh.started, !bh.after, akt.matchNode, false,
                                     treeNodes, sb);
                        bh.value = bh.value + sb.toString();
                        // ustawiamy rozpoczecie na zakonczenie (na wypadek gdyby jedna strefa miala opcjonalne drugie zakonczenie
                        bh.started = akt.matchNode;
                        bh.after = true;
                    }
                    else if (oc.getKind() == oc.KIND_INSIDE)
                    {
                        akt.matchNode.hide();
                        bh.value = bh.value + akt.matchNode.toString();
                        akt.matchNode.unhide();
                    }
                }
            }
        }

        public Enumeration getAllNodes()
        {
            return allNodes.elements();
        }

        public HTMLNode getFirstNode()
        {
            if (allNodes.size() > 0)
            {
                return (HTMLNode)allNodes.firstElement();
            }
            else
            {
                return null;
            }
        }

        public HTMLNode getLastNode()
        {
            if (allNodes.size() > 0)
            {
                return (HTMLNode)allNodes.lastElement();
            }
            else
            {
                return null;
            }
        }

        public int getOrdNum()
        {
            return OrdNum;
        }

        public void traverseTree2(HTMLNode start, boolean includeStart,
                                 HTMLNode end, boolean includeEnd,
                                 Vector allNodes, StringBuffer sb)
        {
            HTMLNode st, en, akt, prev;
            if (includeStart)
            {
                st = start;
            }
            else
            {
                st = (HTMLNode)allNodes.get(start.getMaxPosition() + 1);
            }
            if (includeEnd)
            {
                en = end;
            }
            else
            {
                en = (HTMLNode)allNodes.get(end.getPosition() - 1);
            }
            for (int i = st.getPosition(); i <= en.getPosition(); i++) {
                akt = (HTMLNode)allNodes.get(i);
                if (akt instanceof HTMLTextNode)
                    sb.append(akt.toString() + " ");
            }
        }
        public void traverseTree(HTMLNode start, boolean includeStart,
                                 HTMLNode end, boolean includeEnd,
                                 Vector allNodes, StringBuffer sb)
        {
            HTMLNode st, en, akt, prev;
            if (includeStart)
            {
                st = start;
            }
            else
            {
                st = (HTMLNode)allNodes.get(start.getMaxPosition() + 1);
            }
            if (includeEnd)
            {
                en = end;
            }
            else
            {
                en = (HTMLNode)allNodes.get(end.getPosition() - 1);
            }
            if (en.getPosition() < st.getPosition())
            {
                return;
            }
            Stack goIn = new Stack();
            int lastLevel = st.getLevel();
            for (int i = st.getPosition(); i <= en.getPosition(); i++)
            {
                if (allNodes.get(i)instanceof HTMLTextNode)
                {
                    sb.append(allNodes.get(i).toString());
                }
                else
                {
                    akt = (HTMLNode)allNodes.get(i);
                    prev = (HTMLNode) (goIn.empty() ? null : goIn.peek());
                    if (prev != null && prev.getMaxPosition() < i)
                    {
                        prev.closingTag(sb);
                        goIn.pop();
                    }
                    if (akt.getPosition() == akt.getMaxPosition())
                    {
                        sb.append(akt.toString());
                    }
                    else
                    {
                        goIn.push(akt);
                        akt.openingTag(sb);
                    }
                }
            }
        }

        public boolean dep_traverseTree(HTMLNode start, HTMLNode end,
                                        StringBuffer sb)
        {
            HTMLNode act;
            boolean finished = false;
            act = start.getParent();
            Vector chld = act.getChildrenVector();
            int idx;
            idx = chld.indexOf(start);
            log.debug("<< Starting >>");
            while (!finished)
            {
                for (int i = idx; i < chld.size(); i++)
                {
                    // exit if end node is finally found
                    if ( ( (HTMLNode)chld.get(i)).getPosition() ==
                        end.getPosition())
                    {
                        finished = true;
                        break;
                    }
                    // append the whole subtree if end tag isn't within it
                    if ( ( (HTMLNode)chld.get(i)).isAfter(end))
                    {
                        log.debug("Full append -> " +
                                  ( (HTMLNode)chld.get(i)).getName() +
                                  " from -> " + act.getName());
                        sb.append(chld.get(i));
                    }
                    else if ( ( (HTMLNode)chld.get(i)).isChild(end))
                    {
                        // end node is within actual - jump into it
                        log.debug("Running into -> " +
                                  ( (HTMLNode)chld.get(i)).getName() +
                                  " from -> " + act.getName());
                        act = (HTMLNode)chld.get(i);
                        act.openingTag(sb);
                        chld = act.getChildrenVector();
                        // begin loop from 0
                        i = -1;
                        if (chld == null)
                        {
                            log.fatal(
                                "Children table empty while expecting to end-tag to be a child at tag:" +
                                act.getName());
                            throw new java.lang.AssertionError(
                                "Children table empty while expecting to end-tag to be a child at tag:" +
                                act.getName());
                        }
                    }
                    else
                    {
                        log.fatal("Unexpected situation: Tag -> " + act.getName() +
                                  "(" + act.getPosition() + ", " +
                                  act.getMaxPosition() + "), Child -> " +
                                  ( (HTMLNode)chld.get(i)).getName() + "(" +
                                  ( (HTMLNode)chld.get(i)).getPosition() +
                                  "), End -> " +
                                  end.getName() + "(" + end.getPosition() + ")");
                    }
                }
                if (!finished)
                {
                    log.debug("Jumping up from -> " + act.getName());
                    if (act.getParent() == null)
                    {
                        log.fatal("No finish and no parent of tag: " +
                                  act.getName());
                        throw new java.lang.AssertionError(
                            "No finish and no parent of tag: " + act.getName());
                    }
                    chld = act.getParent().getChildrenVector();
                    idx = chld.indexOf(act) + 1;
                    act.closingTag(sb);
                    act = act.getParent();
                }
            }
            return finished;
        }

        public String getValueForItem(String itemName)
        {
            TreeExtractor.SnippetBuilder.BuilderHelper bh =
                (TreeExtractor.SnippetBuilder.BuilderHelper)items.get(itemName);
            return org.put.util.text.HtmlHelper.removeHtmlTags(clearHTMLString(
                bh.value));
        }

        public String clearHTMLString(String s)
        {
            String ret = s.replaceAll("&nbsp;", " ").replaceAll("&amp;", "&").
                replaceAll("&quot;", "'").
                replaceAll("&lt;", "<").replaceAll("&gt;",
                ">").replaceAll("&bull;", " ").replaceAll("\n", " ").
                replaceAll("\t", " ").trim();
            while (ret.indexOf("  ") >= 0)
            {
                ret = ret.replaceAll("  ", " ");
            }
            return ret;
        }

        private String xmlencode(String x)
        {
            return "<![CDATA[" + x + "]]>";
        }
    }

    public TreeExtractor(Element treeDescriptionXML, Enumeration tokens)
    {
        Element e = JDOMHelper.getElement("extractor/snippet",
                                          treeDescriptionXML);
        while (tokens.hasMoreElements())
        {
            addSearchItem(new SearchItem(tokens.nextElement().toString()));
        }
        artificialRootNode = new TreeNode(e, this);
    }

    public Enumeration getSearchItems()
    {
        return searchItems.elements();
    }

    public void addSearchItem(SearchItem item)
    {
        searchItems.add(item);
    }

    public Enumeration getResults(HTMLTree t, int hasSnippets)
    {
        Vector snippets = new Vector();
        int act = 0;
        NodeMatch lastMatch;
        boolean finished = false;
        while (!finished)
        {
            lastMatch = parseSubTree(t, act, t.getAllNodes().size() - 1,
                                     artificialRootNode, null);
            if (lastMatch == null || !lastMatch.isFound())
            {
                finished = true;
            }
            else
            {
                NodeMatch m = lastMatch;
                while (m.parent != null) {
                    m = m.parent;
                }
                Stack s = new Stack();
                m.fillStack(s);
                act = lastMatch.getMatchIndex() + 1;
                try
                {
                    snippets.add(new SnippetBuilder(s, t.getAllNodes(),
                        hasSnippets));
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

    /**
     * Extracts snippets from the tag tree
     * @param Search result page HTML tree
     */
    public Enumeration parseTree(HTMLTree t, int hasSnippets)
    {
        Vector res = new Vector();
        Enumeration builders = getResults(t, hasSnippets);
        while (builders.hasMoreElements())
        {
            SnippetBuilder b = (SnippetBuilder)builders.nextElement();
            res.add(new Snippet(b));
        }
        return res.elements();
    }

    private NodeMatch parseSubTree(HTMLTree t, int startAt, int endAt,
                                   TreeNode node, NodeMatch parent)
    {
        int minLevel = parent != null ? parent.matchNode.getLevel() + 1 : 0;
        int maxLevel = parent != null ? parent.matchNode.getLevel() + 4 :
            Integer.MAX_VALUE;
        boolean done = false;
        boolean optionals = false;
        Vector skippedOptionals = new Vector();
        Stack thisLevel = new Stack();
        int lastIndex = startAt;
        NodeMatch act = new NodeMatch(t.getAllNodes(),
                                      (TreeNode) node.SubNodes.firstElement(),
                                      parent, minLevel, maxLevel, startAt,
                                      t.getAllNodes().size());
        while (!done) {
            boolean good;
            if (good = act.doFind(true)) {
                if (!act.toFind.optional)
                    lastIndex = act.getMatchIndex();
                optionals = optionals || act.toFind.optional;
                thisLevel.push(act);
                if (!act.toFind.SubNodes.isEmpty() && !act.toFind.optional)
                {
                    good = (parseSubTree(t, act.getMatchIndex() + 1,
                                         act.matchNode.getMaxPosition(),
                                         act.toFind, act) != null);
                }
                // if we're done with subnodes, find next sibling
                if (good) {
                    if (act.toFind.next != null) {
                        act = new NodeMatch(t.getAllNodes(), act.toFind.next,
                                            parent, minLevel, maxLevel,
                                            lastIndex + 1, endAt);
                    }
                    else {
                        // all done
                        done = true;
                    }
                }
            }
            if (!good) {
                // failure
                if (thisLevel.isEmpty()) {
                    // no matching nodes found
                    return null;
                }
                else {
                    // restart last succes node
                    act = (NodeMatch)thisLevel.pop();
                    act.minIndex = act.matchIndex + 1;
                    act.SubNodes = null;
                    act.found = false;
                }
            }
        }
        if (optionals) {
            int lastIdx = endAt + 1;
            for (int i = thisLevel.size() - 1; i >= 0; i--) {
                if (((NodeMatch)thisLevel.get(i)).found)
                    lastIdx = ((NodeMatch)thisLevel.get(i)).matchIndex;
                else
                    ((NodeMatch)thisLevel.get(i)).maxIndex = lastIdx - 1;
            }
            lastIdx = startAt - 1;
            for (int i = 0; i < thisLevel.size(); i++)
            {
                act = (NodeMatch) thisLevel.get(i);
                if (((NodeMatch)thisLevel.get(i)).found)
                    lastIdx = ((NodeMatch)thisLevel.get(i)).matchIndex;
                else
                {
                    ( (NodeMatch)thisLevel.get(i)).minIndex = lastIdx + 1;
                    if (act.toFind.optional)
                    {
                        if (act.doFind(false))
                        {
                            if (!act.toFind.SubNodes.isEmpty())
                            {
                                NodeMatch last = parseSubTree(t,
                                    act.matchIndex + 1,
                                    act.maxIndex, act.toFind, act);
                            }
                        }
                    }
                }
            }
        }
        for (int i = thisLevel.size() - 1; i >= 0; i--)
            if (!((NodeMatch) thisLevel.get(i)).found)
                thisLevel.remove(i);
        if (parent != null)
            parent.SubNodes = thisLevel;
        return act;
    }
}