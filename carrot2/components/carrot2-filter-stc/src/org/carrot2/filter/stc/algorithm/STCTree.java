
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.stc.algorithm;


import java.util.ArrayList;

import org.carrot2.filter.stc.suffixtree.*;


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
