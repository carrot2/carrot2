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

import com.paulodev.carrot.treeSnippetMiner.*;
import com.paulodev.carrot.util.html.parser.*;

public class DictNodeOccurence
    extends Int
{
    private String name;
    private Vector occurences = new Vector();
    private Hashtable possibleChildren = new Hashtable();
    private boolean isRoot;
    private int maxOccurences = 0;

    public DictNodeOccurence(String name, boolean isRoot)
    {
        super(0);
        this.name = name;
        this.isRoot = isRoot;
    }

    public Enumeration getOccurences()
    {
        return occurences.elements();
    }

    public int getOccurencesSize()
    {
        return occurences.size();
    }

    public void addOccurence(HTMLNode node)
    {
        occurences.addElement(node);
        incValue();
    }

    public void addPossibleChild(String name)
    {
        if (possibleChildren.get(name) == null)
        {
            possibleChildren.put(name, name);
        }
    }

    public boolean isPossibleChild(String name)
    {
        return (possibleChildren.get(name) != null);
    }

    public void clearPossibleChildren()
    {
        possibleChildren.clear();
    }

    public void setMaxOccurences(int max)
    {
        maxOccurences = max;
    }

    public int getMaxOccurences()
    {
        return maxOccurences;
    }

    public String getName()
    {
        return name;
    }

    public boolean getIsRoot()
    {
        return isRoot;
    }

    public String toString()
    {
        return "<" + name + "[" + getValue() + "] -> " +
            possibleChildren.keySet() + ">";
    }

    public int getChildrenCount(DictNodeOccurence toCompare, int maxDistance)
    {
        int cnt = 0;
        for (int i = 0; i < occurences.size(); i++)
        {
            for (int j = 0; j < toCompare.occurences.size(); j++)
            {
                HTMLNode here = (HTMLNode)occurences.get(i);
                HTMLNode toCheck = (HTMLNode)toCompare.occurences.get(j);
                if (here.isChild(toCheck) &&
                    (toCheck.getLevel() - here.getLevel() <= maxDistance))
                {
                    cnt++;
                }
            }
        }
        return cnt;
    }
}