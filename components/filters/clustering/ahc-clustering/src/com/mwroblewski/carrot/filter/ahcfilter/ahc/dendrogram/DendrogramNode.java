

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


package com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram;


import java.util.LinkedList;


/**
 * @author Micha� Wr�blewski
 */
public class DendrogramNode
    extends DendrogramItem
{
    protected DendrogramItem left;
    protected DendrogramItem right;
    protected float similarity;

    public DendrogramNode(DendrogramLeaf left, DendrogramLeaf right, float similarity)
        throws NullPointerException
    {
        if (left == null)
        {
            throw new NullPointerException("Trying to create a DendrogramNode with null left leaf");
        }

        if (right == null)
        {
            throw new NullPointerException(
                "Trying to create a DendrogramNode with null right leaf"
            );
        }

        this.left = left;
        this.right = right;
        this.similarity = similarity;
    }


    public DendrogramNode(DendrogramNode left, DendrogramNode right, float similarity)
        throws NullPointerException
    {
        if (left == null)
        {
            throw new NullPointerException("Trying to create a DendrogramNode with null left node");
        }

        if (right == null)
        {
            throw new NullPointerException(
                "Trying to create a DendrogramNode with null right node"
            );
        }

        this.left = left;
        this.right = right;
        this.similarity = similarity;
    }


    public DendrogramNode(DendrogramNode left, DendrogramLeaf right, float similarity)
    {
        if (left == null)
        {
            throw new NullPointerException("Trying to create a DendrogramNode with null left node");
        }

        if (right == null)
        {
            throw new NullPointerException(
                "Trying to create a DendrogramNode with null right leaf"
            );
        }

        this.left = left;
        this.right = right;
        this.similarity = similarity;
    }

    protected DendrogramLeaf GetLeftmostLeaf()
    {
        DendrogramNode result = this;

        while (result.left instanceof DendrogramNode)
        {
            result = (DendrogramNode) result.left;
        }

        return (DendrogramLeaf) result.left;
    }


    protected DendrogramNode add(DendrogramLeaf doc, float similarity)
    {
        return new DendrogramNode(this, doc, similarity);
    }


    protected DendrogramNode add(DendrogramNode node, float similarity)
    {
        return new DendrogramNode(this, node, similarity);
    }


    public DendrogramItem getLeft()
    {
        return left;
    }


    public DendrogramItem getRight()
    {
        return right;
    }


    public int getIndex()
    {
        return GetLeftmostLeaf().docNo;
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


    public int size()
    {
        return (left.size() + right.size());
    }


    public LinkedList getAllIndices()
    {
        LinkedList resultLeft = left.getAllIndices();

        resultLeft.addAll(right.getAllIndices());

        return resultLeft;
    }


    public float getSimilarity()
    {
        return similarity;
    }


    public String toString()
    {
        StringBuffer result = new StringBuffer("(");

        result.append(left.toString());
        result.append(" ");
        result.append(right.toString());
        result.append(")");

        return result.toString();
    }
}
