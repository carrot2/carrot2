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

public class TreeExpansion
{
    private DictNodeOccurence roots;
    private FreqSubtreeMiner miner;
    private TreeExpansion prevExpansion;
    private int treeSize;
    private boolean zeroLevelExpand = false;

    private String name;
    private TreeExpansion parent;
    private Vector occurences = new Vector();
    private Vector relevantOccurences = null;
    private boolean isRoot = false;
	private boolean forward;

    public TreeExpansion(boolean forward, FreqSubtreeMiner miner, TreeExpansion prevExpansion,
                         TreeExpansion attachToElement,
                         String elementName, boolean zeroLevelExpand)
    {
        this.miner = miner;
        this.prevExpansion = prevExpansion;
        if (prevExpansion != null)
        {
            isRoot = false;
            treeSize = prevExpansion.treeSize + 1;
        }
        else
        {
            treeSize = 1;
            isRoot = true;
        }
        this.parent = attachToElement;
        this.name = elementName;
        this.zeroLevelExpand = zeroLevelExpand;
        this.forward = forward;
    }

    private TreeExpansion(boolean forward, FreqSubtreeMiner miner, DictNodeOccurence rootNodes)
    {
        this.miner = miner;
        this.prevExpansion = null;
        treeSize = 1;
        this.isRoot = true;
        this.parent = null;
        this.name = rootNodes.getName();
        this.forward = forward;
    }

    public TreeExpansion(boolean forward, FreqSubtreeMiner miner, DictNodeOccurence rootNodes,
                         DictNodeOccurence artificialRoot)
    {
        this.miner = miner;
        this.prevExpansion = new TreeExpansion(forward, miner, artificialRoot);
        this.parent = prevExpansion;
        this.name = rootNodes.getName();
        this.forward = forward;
        // get the only occurence of artificial root
        Enumeration oc = artificialRoot.getOccurences();
        if (!oc.hasMoreElements())
        {
            throw new ExceptionInInitializerError(
                "The size of artificial root's occurences table shoul be 1");
        }
        HTMLNode rootNode = (HTMLNode)oc.nextElement();
        if (oc.hasMoreElements())
        {
            throw new ExceptionInInitializerError(
                "The size of artificial root's occurences table shoul be 1");
        }

        Enumeration e = rootNodes.getOccurences();
        while (e.hasMoreElements())
        {
            TreeExpansionElementOccurence rootOc = new
                TreeExpansionElementOccurence(null, null, rootNode);
            HTMLNode n = (HTMLNode)e.nextElement();
            rootOc.setRoot(n);
            prevExpansion.addOccurence(rootOc);
            addOccurence(new TreeExpansionElementOccurence(
                rootOc, rootOc, n, n));
        }
    }

	public TreeExpansion getTreeInDirection(FreqSubtreeMiner miner, boolean forward) {
        if (this.forward != forward) {
			return getResult().constructTreeExpansion(miner, forward);
        }
		else
			return this;
    }

    public void setZeroLevelExpands(boolean value)
    {
        zeroLevelExpand = value;
    }

    private class ProcessItem
    {
        public TreeExpansion elementToProcess;
        public Vector relevantOccurences;

        public ProcessItem(TreeExpansion element)
        {
            elementToProcess = element;
            relevantOccurences = element.getRelevantOccurences();
        }
    }

    public void expandTrees()
    {
        Vector outerExpannsionProcess = new Vector();
        calcRelevantOccurences();
        TreeExpansion curr = this;
        while (curr.parent != null)
        {
            outerExpannsionProcess.add(new ProcessItem(curr));
            curr = curr.parent;
        }
        clearRelevantOccurences(true);
        Enumeration e = miner.getDictionary();
        while (e.hasMoreElements())
        {
            DictNodeOccurence n = (DictNodeOccurence)e.nextElement();
            // Check if the node wouldn't be overused
            if (getTagUsageCount(n.getName()) + 1 > n.getMaxOccurences())
            {
                continue;
            }
            expandTree(n, outerExpannsionProcess.elements());
        }
    }

    public int getTagUsageCount(String tag)
    {
        int ret = tag.equals(name) ? 1 : 0;
        if (prevExpansion != null)
        {
            return prevExpansion.getTagUsageCount(tag) + ret;
        }
        else
        {
            return ret;
        }
    }

    public double getSupport()
    {
		Hashtable t = new Hashtable();
		for (int i = 0; i < occurences.size(); i++)
			t.put(((TreeExpansionElementOccurence) occurences.get(i)).getRoot(), occurences.get(i));
        return (double) t.size() / (double)miner.getNumSnippets();
    }

    private void expandTree(DictNodeOccurence nodes,
                            Enumeration outerProcess)
    {
        Vector result = new Vector();
        if (miner.getDictionaryForTag(name).isPossibleChild(nodes.
            getName()))
        {
            innerExpand(nodes, this, result);
        }
        while (outerProcess.hasMoreElements())
        {
            ProcessItem item = (ProcessItem)outerProcess.nextElement();
            if (miner.getDictionaryForTag(item.elementToProcess.parent.name).
                isPossibleChild(nodes.getName()))
            {
                outerExpand(nodes, item.elementToProcess,
                            item.relevantOccurences.elements(),
                            result);
            }
        }

        // dig deeper into results
        Enumeration e = result.elements();
        while (e.hasMoreElements())
        {
            ( (TreeExpansion)e.nextElement()).expandTrees();
        }
    }

    private void innerExpand(DictNodeOccurence nodes,
                             TreeExpansion el,
                             Vector result)
    {
        Vector rootFlags = new Vector();
        TreeExpansion res = new TreeExpansion(forward, miner, this, el, nodes.getName(),
                                              zeroLevelExpand);
        Enumeration exampl = el.occurences.elements();
        while (exampl.hasMoreElements())
        {
            TreeExpansionElementOccurence occur = (
                TreeExpansionElementOccurence)exampl.nextElement();
            Enumeration dict = nodes.getOccurences();
            while (dict.hasMoreElements())
            {
                HTMLNode curr = (HTMLNode)dict.nextElement();
                // both lists must be position-sorted to work this merge out (sort-merge)
                if (occur.getNode().isChild(curr))
                {
                    if (curr.getLevel() - occur.getNode().getLevel() <=
                        miner.getMaxLevelDistance()
						&& (occur.getBound() == 0
		                    || (forward && curr.getPosition() < occur.getBound())
							|| (!forward && curr.getPosition() > occur.getBound()))
                        )
                    {
                        res.addOccurence(new TreeExpansionElementOccurence(occur,
                            occur, curr));
                        if (!rootFlags.contains(occur.getRoot()))
                        {
                            rootFlags.add(occur.getRoot());
                        }
                    }
                }
                // if we are past the current node skip to the next one
                else if (curr.getPosition() > occur.getNode().getMaxPosition())
                {
                    break;
                }
            }
        }
        // check if min support is fullfilled
        if (rootFlags.size() >= miner.getMinSnippetsToSupprt())
        {
            if (res.treeSize > miner.getMaxSize())
            {
                miner.setMaxExpansion(res);
            }
            result.add(res);
        }
    }

    private void outerExpand(DictNodeOccurence nodes,
                             TreeExpansion el,
                             Enumeration exampl, Vector result)
    {
        Vector rootFlags = new Vector();
        TreeExpansion res = new TreeExpansion(forward, miner, this, el.parent,
                                              nodes.getName(), zeroLevelExpand);
        while (exampl.hasMoreElements())
        {
            TreeExpansionElementOccurence occur = (TreeExpansionElementOccurence)exampl.nextElement();
            Enumeration dict = nodes.getOccurences();
            while (dict.hasMoreElements() && occur.getParent() != null)
            {
                HTMLNode curr = (HTMLNode)dict.nextElement();
                if (occur.getParent().getNode().isChild(curr))
                {
                    boolean cond = forward ?
                        forwardOuterExpansionCondition(miner, el, occur, curr) :
                        backwardOuterExpansionCondition(miner, el, occur, curr);
                    if (cond)
                    {
                        res.addOccurence(new TreeExpansionElementOccurence(occur.getParent(), occur.getLast(),
                            curr));
                        if (!rootFlags.contains(occur.getRoot()))
                        {
                            rootFlags.add(occur.getRoot());
                        }
                    }
                }
                // if we are past the current node skip to the next one (only true if expanding forwards)
                else if (forward && curr.getPosition() >
                         occur.getParent().getNode().getMaxPosition())
                {
                    break;
                }
            }
        }
        // check if min support is fullfilled
        if (rootFlags.size() >= miner.getMinSnippetsToSupprt())
        {
            if (res.treeSize > miner.getMaxSize())
            {
                miner.setMaxExpansion(res);
            }
            result.add(res);
        }
    }

    private boolean forwardOuterExpansionCondition(FreqSubtreeMiner miner,
        TreeExpansion el,
        TreeExpansionElementOccurence occur, HTMLNode curr)
    {
        return (curr.getLevel() -
                occur.getParent().getNode().getLevel() <=
                miner.getMaxLevelDistance() && (!el.parent.isRoot)
                ||
                (zeroLevelExpand && (el.parent.isRoot) &&
                 Math.
                 abs( (curr.getLevel() - occur.getNode().getLevel())) <=
                 miner.getMaxLevelDistance())
                )
            &&
            curr.getPosition() - occur.getRoot().getPosition() <=
            miner.getMaxSnippetSize()
            && curr.getPosition() > occur.getNode().getMaxPosition()
            && (occur.getBound() == 0 || curr.getPosition() < occur.getBound());
    }

    private boolean backwardOuterExpansionCondition(FreqSubtreeMiner miner,
        TreeExpansion el,
        TreeExpansionElementOccurence occur, HTMLNode curr)
    {
        return (curr.getLevel() -
                occur.getParent().getNode().getLevel() <=
                miner.getMaxLevelDistance() && (!el.parent.isRoot)
                ||
                (zeroLevelExpand && (el.parent.isRoot) &&
                 Math.
                 abs( (curr.getLevel() - occur.getNode().getLevel())) <=
                 miner.getMaxLevelDistance())
                )
            &&
            occur.getRoot().getPosition() - curr.getMaxPosition() <=
            miner.getMaxSnippetSize()
            && curr.getMaxPosition() < occur.getNode().getPosition()
            /*&& (occur.getBound() == 0 || curr.getPosition() > occur.getBound())*/;
    }

    public void calcBound()
    {
        Vector roots = new Vector();
        Enumeration e = occurences.elements();
        while (e.hasMoreElements())
        {
            HTMLNode r = ( (TreeExpansionElementOccurence)e.nextElement()).
                getRoot();
            if (!roots.contains(r))
            {
                roots.add(r);
            }
        }
        TreeExpansionElementOccurence last = null;
        e = occurences.elements();
        while (e.hasMoreElements())
        {
            TreeExpansionElementOccurence cu = (TreeExpansionElementOccurence)e.
                nextElement();
            if (!cu.calcBound(forward, roots.elements()))
            {
                last = cu;
            }
        }

        int maxDist = 0;
        e = occurences.elements();
        while (e.hasMoreElements())
        {
            TreeExpansionElementOccurence cu = (TreeExpansionElementOccurence)e.
                nextElement();
            if (cu.getBound() - cu.getRoot().getPosition() > maxDist)
            {
                maxDist = cu.getBound() - cu.getRoot().getPosition();
            }
        }
        if (last != null)
        {
            last.setBound(forward ? last.getRoot().getPosition() + maxDist :
                          last.getRoot().getPosition() - maxDist);
        }
    }

    public ResultNode getResult()
    {
        ResultNode res = innerGetResult(null, null);
        while (res.parentNode != null)
        {
            res = res.parentNode;
        }
        return res;
    }

	public ResultNode MergeResult(ResultNode lastResult, TreeExpansion lastExpansion) {
		ResultNode last = lastResult.findNodeForExtension(lastExpansion);
		return innerGetResult(lastExpansion, last).getRoot();
    }

    private ResultNode innerGetResult(TreeExpansion attachExp, ResultNode attachNode)
    {
        if (attachExp != null && attachNode != null && this == attachExp) {
            return attachNode;
        } else
        if (prevExpansion == null)
        {
            return new ResultNode(forward, name, null, null, this);
        }
        else
        {
            ResultNode prevNode = prevExpansion.innerGetResult(attachExp, attachNode);
			ResultNode sibNode = null;
            TreeExpansion curr = prevExpansion;
            while (curr != null)
            {
                if (parent == curr)
                {
                    return new ResultNode(forward, name, prevNode, sibNode, this);
                }
                else
                {
                    curr = curr.parent;
					sibNode = prevNode;
                    prevNode = prevNode.parentNode;
                }
            }
            return null;
        }
    }

    public Vector markResults()
    {
        Vector res = new Vector();
        markResults(res, null);
        return res;
    }

    public void removeIrrelevantOccurences() {
		markAllOccurences(false);
		markAllRelevant(true);
		removeOccurencesWithFlag(false);
    }

	private void markAllOccurences(boolean flag) {
		Enumeration e = occurences.elements();
		while (e.hasMoreElements()) {
            ((TreeExpansionElementOccurence) e.nextElement()).setMarked(flag);
        }
		if (prevExpansion != null)
			prevExpansion.markAllOccurences(flag);
    }

	private void markAllRelevant(boolean flag) {
        Enumeration e = occurences.elements();
        while (e.hasMoreElements()) {
            ((TreeExpansionElementOccurence) e.nextElement()).markWholeTree(null, flag);
        }
    }

    private void removeOccurencesWithFlag(boolean flag) {
        Enumeration e = occurences.elements();
        while (e.hasMoreElements()) {
            TreeExpansionElementOccurence o = (TreeExpansionElementOccurence) e.nextElement();
			if (o.getMarked() == flag)
				occurences.remove(o);
        }
        if (prevExpansion != null)
            prevExpansion.removeOccurencesWithFlag(flag);
    }

    public int getTreeSize()
    {
        return treeSize;
    }

	public boolean isRoot() {
        return isRoot;
    }

	public TreeExpansion getPrevExpansion() {
		return prevExpansion;
    }

	public void boundPreviousExpansion() {
        Enumeration e = occurences.elements();
		while (e.hasMoreElements()) {
            TreeExpansionElementOccurence o = (TreeExpansionElementOccurence) e.nextElement();
			o.boundPreviousOccurence();
        }
    }

    private void unmarkTree()
    {
        Enumeration e = occurences.elements();
        while (e.hasMoreElements())
        {
            ( (TreeExpansionElementOccurence)e.nextElement()).setMarked(false);
        }
        if (prevExpansion != null)
        {
            prevExpansion.unmarkTree();
        }
    }

    private void calcRelevantOccurences() {
		relevantOccurences = occurences;
		Enumeration e = occurences.elements();
		while (e.hasMoreElements())
		{
            TreeExpansionElementOccurence o = (TreeExpansionElementOccurence) e.nextElement();
			o.setLast(o);
        }
		if (parent != null)
	    	parent.innerCalcRelevantOccurences(occurences);
    }

    private void innerCalcRelevantOccurences(Vector childOccurences)
    {
        relevantOccurences = new Vector();
        Enumeration e = childOccurences.elements();
        while (e.hasMoreElements())
        {
            // get the child's occurence
            TreeExpansionElementOccurence o = (
                TreeExpansionElementOccurence)e.nextElement();
			TreeExpansionElementOccurence org = o;
            // jump to the parent
            o = o.getParent();
            if (!relevantOccurences.contains(o))
            {
				o.setLast(org.getLast());
                relevantOccurences.add(o);
            }
        }
        if (parent != null)
        {
            parent.innerCalcRelevantOccurences(relevantOccurences);
        }
    }


    private Vector getRelevantOccurences()
    {
        if (relevantOccurences == null)
        {
            throw new ExceptionInInitializerError(
                "Niewypełniona lista relewantnych wystąpień");
        }
        return relevantOccurences;
    }

    private void clearRelevantOccurences(boolean recursive)
    {
        relevantOccurences = null;
        if (recursive && parent != null)
        {
            parent.clearRelevantOccurences(recursive);
        }
    }

    private void addOccurence(TreeExpansionElementOccurence occ)
    {
        if (forward)
        {
            occurences.add(occ);
        }
        else
        {
            occurences.insertElementAt(occ, 0);
        }
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
		String res = name + " ";
		Enumeration e = occurences.elements();
/*		while (e.hasMoreElements()) {
            TreeExpansionElementOccurence o = (TreeExpansionElementOccurence) e.nextElement();
			res += "[" + o.toString() + "]";
        }*/
		return res;
    }

    private void markResults(Vector result, Hashtable allowedRoots)
    {
        Hashtable treeInstances = new Hashtable();
        Enumeration e = occurences.elements();
        // build unique tree endings
        while (e.hasMoreElements())
        {
            TreeExpansionElementOccurence o = (TreeExpansionElementOccurence)e.
                nextElement();
            if (allowedRoots == null || allowedRoots.containsKey(o.getRoot()))
            {
                if (treeInstances.containsKey(o.getRoot()))
                {
                    TreeExpansionElementOccurence toComp = (
                        TreeExpansionElementOccurence)treeInstances.get(o.
                        getRoot());
                    if (toComp.getNode().getPosition() >
                        o.getNode().getPosition())
                    {
                        treeInstances.put(o.getRoot(), o);
                    }
                }
                else
                {
                    treeInstances.put(o.getRoot(), o);
                }
            }
        }
        if (allowedRoots == null)
        {
            allowedRoots = new Hashtable();
            e = treeInstances.keys();
            while (e.hasMoreElements())
            {
                Object o = e.nextElement();
                allowedRoots.put(o, o);
            }
        }
        // mark instances
        e = treeInstances.elements();
        while (e.hasMoreElements())
        {
            TreeExpansionElementOccurence o = (TreeExpansionElementOccurence)e.
                nextElement();
            o.markWholeTree(result, true);
        }
        if (prevExpansion != null)
        {
            prevExpansion.markResults(result, allowedRoots);
        }
    }

	public void truncateOccurences() {
		Hashtable minRootOccurences = new Hashtable();
        Enumeration e = occurences.elements();
		while (e.hasMoreElements()) {
        	TreeExpansionElementOccurence o = (TreeExpansionElementOccurence) e.nextElement();
			TreeExpansionElementOccurence min = (TreeExpansionElementOccurence) minRootOccurences.get(o.getRoot());
			if (min == null)
				minRootOccurences.put(o.getRoot(), o);
			else if (min.getNode().getPosition() > o.getNode().getPosition())
				minRootOccurences.put(o.getRoot(), o);
        }
		occurences.clear();
		Iterator i = minRootOccurences.values().iterator();
		while (i.hasNext()) {
            occurences.add(i.next());
        }
		prevExpansion.truncateOccurences(occurences.elements());
    }

    private void truncateOccurences(Enumeration nextLevelOccurences) {
		occurences.clear();
		while (nextLevelOccurences.hasMoreElements()) {
			occurences.add(((TreeExpansionElementOccurence) nextLevelOccurences.nextElement()).getPrevios());
        }
		if (prevExpansion != null)
	        prevExpansion.truncateOccurences(occurences.elements());
    }

    public Vector getOccurences()
    {
        return occurences;
    }
}