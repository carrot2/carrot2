
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram;


import java.util.LinkedList;


/**
 * @author Micha� Wr�blewski
 */
public class DendrogramLeaf
    extends DendrogramItem
{
    protected int docNo;

    public DendrogramLeaf(int docNo)
    {
        this.docNo = docNo;
    }

    public int getDocNo()
    {
        return docNo;
    }


    public int getIndex()
    {
        return this.docNo;
    }


    protected DendrogramNode add(DendrogramLeaf doc, float similarity)
    {
        return new DendrogramNode(this, doc, similarity);
    }


    protected DendrogramNode add(DendrogramNode node, float similarity)
    {
        return new DendrogramNode(node, this, similarity);
    }


    public DendrogramNode add(DendrogramItem item, float similarity)
    {
        if (item instanceof DendrogramLeaf)
        {
            return add((DendrogramLeaf) item, similarity);
        }
        else
        {
            return add((DendrogramNode) item, similarity);
        }
    }


    public LinkedList getAllIndices()
    {
        LinkedList result = new LinkedList();
        result.add(new Integer(docNo));

        return result;
    }


    public int size()
    {
        return 1;
    }


    public String toString()
    {
        return ("" + docNo);
    }
}
