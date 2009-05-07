
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2009, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.clustering.stc;

import java.util.ArrayList;

import org.carrot2.text.suffixtrees.GeneralizedSuffixTree;
import org.carrot2.text.suffixtrees.Node;
import org.carrot2.text.suffixtrees.ISuffixableElement;

/**
 * Extends Generalized Suffix Tree in order to provide count of suffixed documents in each
 * node.
 */
public class STCTree extends GeneralizedSuffixTree
{
    /** Currently processed document index */
    private int currentDocumentIndex = 0;
    private ArrayList<Integer> mapping = new ArrayList<Integer>();

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

    public Node add(ISuffixableElement element)
    {
        mapping.add(currentDocumentIndex);

        return super.add(element);
    }

    /**
     * Creates a new PhraseNode (used by super classes).
     */
    protected Node createNode()
    {
        return new PhraseNode(this);
    }
}
