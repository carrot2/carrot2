

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


package com.mwroblewski.carrot.filter.ahcfilter.ahc.linkage;


import com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram.DendrogramItem;


/**
 * @author Micha� Wr�blewski
 */
public class CompleteLinkage
    implements LinkageMethod
{
    public float newSimilarity(
        DendrogramItem group1, DendrogramItem group2, DendrogramItem targetGroup,
        float [][] similarities
    )
    {
        float group1ToTarget = group1.similarityFromMatrix(targetGroup, similarities);
        float group2ToTarget = group2.similarityFromMatrix(targetGroup, similarities);

        // calculating the similarities of clustered groups 1 and 2 to the
        // target group
        if (group1ToTarget < group2ToTarget)
        {
            return group1ToTarget;
        }
        else
        {
            return group2ToTarget;
        }
    }
}
