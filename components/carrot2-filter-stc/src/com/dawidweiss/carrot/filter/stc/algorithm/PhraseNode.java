
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package com.dawidweiss.carrot.filter.stc.algorithm;


import com.dawidweiss.carrot.filter.stc.suffixtree.*;


/**
 * Extends Generalized Suffix Tree in order to provide count of suffixed documents in each node.
 */
public class PhraseNode
    extends GSTNode
{
    /** Suffixed documents */
    ExtendedBitSet docs;

    public ExtendedBitSet getInternalDocumentsRepresentation()
    {
        return docs;
    }


    public int getSuffixedDocumentsCount()
    {
        return docs.numberOfSetBits();
    }


    public int getSuffixedElementsCount()
    {
        throw new RuntimeException("CANNOT CALL THIS IN PHRASENODE");
    }


    public ExtendedBitSet getInternalSuffixedElementsRepresentation()
    {
        throw new RuntimeException("CANNOT CALL THIS IN PHRASENODE");
    }

    /**
     * Public constructor
     */
    public PhraseNode(STCTree t)
    {
        super(t);

        docs = new ExtendedBitSet(t.getCurrentDocumentIndex());

        docs.set(t.getCurrentDocumentIndex());
    }

    protected STCTree getSTCContainer()
    {
        return (STCTree) super.getContainer();
    }


    /**
     * adds a new indexed element to this node
     */
    public void addIndexedElement(int elementIndex)
    {
        docs.set(getSTCContainer().map(elementIndex));
        super.addIndexedElement(elementIndex);
    }


    /**
     * Propagates the element of some index up the nodes' hierarchy.
     */
    protected void propagateIndexedElementUp(int elementIndex)
    {
        Edge edge;
        PhraseNode parent = this;

        while ((edge = parent.getEdgeToParent()) != null)
        {
            parent = (PhraseNode) edge.getStartNode();

            if (parent.elementsInNode.get(elementIndex) == true)
            {
                break;
            }
            else
            {
                parent.elementsInNode.or(elementsInNode);
                parent.docs.or(docs);
            }
        }
    }


    /**
     * Detects 'eos-marker-only' nodes.
     */
    public boolean isEOSOnly()
    {
        return getSuffixableElement().get(getEdgeToParent().getStartIndex()) == SuffixableElement.END_OF_SUFFIX;
    }
}
