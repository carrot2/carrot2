

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


package com.dawidweiss.carrot.filter.stc.algorithm;


import org.put.util.algorithm.suffixtree.generic.*;
import java.util.*;


/**
 * Extends Generalized Suffix Tree in order to provide count of suffixed documents in each node.
 */
public class STCTree
    extends GeneralizedSuffixTree
{
    /** Currently processed document index */
    private int currentDocumentIndex = 0;
    private ArrayList mapping = new ArrayList();

    public void nextDocument()
    {
        currentDocumentIndex++;
    }


    public int map(int suffixedElementIndex)
    {
        return ((Integer) mapping.get(suffixedElementIndex)).intValue();
    }


    public int getCurrentDocumentIndex()
    {
        return currentDocumentIndex;
    }


    public Node add(SuffixableElement element)
    {
        mapping.add(new Integer(currentDocumentIndex));

        return super.add(element);
    }


    /**
     * Creates a new PhraseNode (used by superclasses).
     */
    protected Node createNode()
    {
        return new PhraseNode(this);
    }
}
