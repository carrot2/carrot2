package com.paulodev.carrot.treeSnippetMiner.frequentTreeMiner;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Poznań University of Technology</p>
 * @author Paweł Kowalik
 * @version 1.0
 */

import java.util.*;

import org.jdom.*;

public class ResultNode
{
    public Vector subNodes = new Vector();
    public ResultNode parentNode;
    public String name;
    private TreeExpansion owner;
    private Hashtable documentOccurences = new Hashtable();

    public ResultNode(boolean forward, String name, ResultNode parent, ResultNode sibling,
                      TreeExpansion owner)
    {
        this.parentNode = parent;
        this.name = name;
        this.owner = owner;
        if (parentNode != null)
        {
			if (sibling == null) {
                if (forward)
                    parentNode.subNodes.add(this);
                else
                    parentNode.subNodes.insertElementAt(this, 0);
            }
			else {
                if (forward)
                    parentNode.subNodes.insertElementAt(this, parentNode.subNodes.indexOf(sibling) + 1);
                else
                    parentNode.subNodes.insertElementAt(this, parentNode.subNodes.indexOf(sibling));
            }
        }
    }

    private void internalToString(Vector s)
    {
        Vector subs = new Vector();
        if (subNodes.size() > 0)
        {
            for (int i = 0; i < subNodes.size(); i++)
            {
                ( (ResultNode)subNodes.get(i)).internalToString(subs);
            }
//            s.add("<" + name + "[" + owner.toSimpleString() + "]>");
            s.add("<" + owner.toString() + "[" + owner.getSupport() + "]>");
            for (int i = 0; i < subs.size(); i++)
            {
                s.add("  " + subs.get(i));
            }
            s.add("</" + name + ">");
        }
        else
        {
            s.add("<" + owner.toString() + "[" + owner.getSupport() + "]/>");
//            s.add("<" + name + "[" + owner.toSimpleString() + "] />");
        }
    }

    public ResultNode findNodeForExtension(TreeExpansion e) {
        return getRoot().innerFindNodeForExtension(e);
    }

    private ResultNode innerFindNodeForExtension(TreeExpansion e) {
		if (e == owner)
			return this;
		else
        {
            Enumeration en = subNodes.elements();
			while (en.hasMoreElements()) {
				ResultNode res = ((ResultNode) en.nextElement()).innerFindNodeForExtension(e);
				if (res != null)
					return res;
            }
			return null;
        }
    }

	public ResultNode getRoot() {
		ResultNode res = this;
        while (res.parentNode != null)
			res = res.parentNode;
		return res;
    }

    public String toString()
    {
        Vector tmp = new Vector();
        internalToString(tmp);
        String res = new String();
        for (int i = 0; i < tmp.size(); i++)
        {
            res += tmp.get(i) + "\n";
        }
        return res;
    }

    public Element toXML()
    {
        String tmpName = name;
        String tmpClass = null;
        tmpName = tmpName.replaceAll("_", "");
        if (tmpName.indexOf(" ") >= 0)
        {
            String[] sp = tmpName.split(" ");
            tmpName = sp[0];
            String[] cl = sp[1].split("=");
            tmpClass = cl[1];
        }
        Element res = new Element(tmpName);
        if (tmpClass != null)
        {
            res.setAttribute("class", tmpClass);
        }
		if (owner.getSupport() < 1)
			res.setAttribute("optional", "true");
        List childs = res.getChildren();
        for (int i = 0; i < subNodes.size(); i++)
        {
            childs.add( ( (ResultNode)subNodes.get(i)).toXML());
        }
        return res;
    }

    public TreeExpansion constructTreeExpansion(FreqSubtreeMiner miner,
                                                boolean forward)
    {
        if (parentNode != null)
        {
            return parentNode.constructTreeExpansion(miner, forward);
        }
        else
        {
            return DFS(miner, forward, null, null);
        }
    }

    private TreeExpansion DFS(FreqSubtreeMiner miner, boolean forward,
                              TreeExpansion parent, TreeExpansion prev)
    {
        int begin = forward ? 0 : subNodes.size() - 1;
        int end = forward ? subNodes.size(): -1;
        int step = forward ? 1 : -1;
        TreeExpansion thisNode = new TreeExpansion(forward, miner, prev, parent, name, true);
        for (int i = owner.getOccurences().size() - 1; i >= 0; i--)
        {
//            if ( ( (TreeExpansionElementOccurence)owner.getOccurences().get(i)).
//                getMarked())
//            {
                thisNode.getOccurences().add(
                    /*new TreeExpansionElementOccurence(*/ 
                        owner.getOccurences().get(i)
                    /*)*/);
//            }
        }
        TreeExpansion actPrev = thisNode;
        for (int i = begin; i != end; i += step)
        {
            actPrev = ( (ResultNode)subNodes.get(i)).DFS(miner, forward,
                thisNode, actPrev);
        }
        return actPrev;
    }
}