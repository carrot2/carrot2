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

import com.paulodev.carrot.util.html.parser.*;

public class TreeExpansionElementOccurence
{
    private TreeExpansionElementOccurence parent;
    private TreeExpansionElementOccurence previous;
    private HTMLNode node;
    private HTMLNode root;
    private boolean mark;
    private int bound = 0;
	private TreeExpansionElementOccurence last;

    public TreeExpansionElementOccurence(TreeExpansionElementOccurence parent,
                                         TreeExpansionElementOccurence previous,
                                         HTMLNode node)
    {
        this.parent = parent;
        this.previous = previous;
        if (previous != null)
        {
            this.bound = previous.bound;
        }
        this.node = node;
        if (parent != null)
        {
            root = parent.getRoot();
        }
        else
        {
            root = node;
        }
    }

    public TreeExpansionElementOccurence(TreeExpansionElementOccurence parent,
                                         TreeExpansionElementOccurence previous,
                                         HTMLNode node, HTMLNode root)
    {
        this.previous = previous;
        if (previous != null)
        {
            this.bound = previous.bound;
        }
        this.parent = parent;
        this.node = node;
        this.root = root;
    }

    public TreeExpansionElementOccurence(TreeExpansionElementOccurence src)
    {
        this.previous = src.previous;
        // do not copy bound
        this.bound = 0;
        this.parent = src.parent;
        this.node = src.node;
        this.root = src.root;
    }

    public HTMLNode getNode()
    {
        return node;
    }

    public HTMLNode getRoot()
    {
        return root;
    }

	public TreeExpansionElementOccurence getLast() {
        return last;
    }

	public void setLast(TreeExpansionElementOccurence newLast) {
        last = newLast;
    }

    public void setRoot(HTMLNode root)
    {
        this.root = root;
    }

    public TreeExpansionElementOccurence getParent()
    {
        return parent;
    }

    public TreeExpansionElementOccurence getPrevios()
    {
        return previous;
    }

    public boolean getMarked()
    {
        return mark;
    }

    public void setMarked(boolean value)
    {
        mark = value;
    }

    public void markWholeTree(Vector results, boolean flag)
    {
        mark = flag;
		if (results != null)
        	results.add(this);
        if (previous != null)
        {
            previous.markWholeTree(results, flag);
        }
    }

    public int getBound()
    {
        return bound;
    }

    public void setBound(int pos)
    {
        bound = pos;
        if (parent != null)
        {
            parent.setBound(pos);
        }
    }

	public void boundPreviousOccurence() {
        if (previous != null)
			previous.setBound(node.getPosition());
    }

    public boolean calcBound(boolean forward, Enumeration roots)
    {
        HTMLNode maxNode = null;
        while (roots.hasMoreElements())
        {
            HTMLNode r = (HTMLNode)roots.nextElement();
            if (forward ?
                (r.getPosition() > root.getPosition() &&
                 (maxNode == null || r.getPosition() < maxNode.getPosition()))
                :
                (r.getMaxPosition() < root.getPosition() &&
                 (maxNode == null ||
                  r.getMaxPosition() > maxNode.getMaxPosition())))
            {
                maxNode = r;
            }
        }
        if (maxNode != null)
        {
            bound = maxNode.getPosition();
            if (parent != null)
            {
                parent.setBound(bound);
            }
            return true;
        }
        return false;
    }

	public String toString() {
        return node.getPosition()
			+ " p" + (previous != null ? ("" + previous.node.getPosition()) : "-")
			+ " r" + (root != null ? ("" + root.getPosition()) : "-");
    }
}