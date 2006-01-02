
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
package com.mwroblewski.carrot.filter.ahcfilter.ahc.linkage;


import com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram.DendrogramItem;


/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 *
 * @author Micha� Wr�blewski
 */
public class SingleLinkage
    implements LinkageMethod
{
    public float newSimilarity(
        DendrogramItem group1, DendrogramItem group2, DendrogramItem targetGroup,
        float [][] similarities
    )
    {
        float group1ToTarget = group1.similarityFromMatrix(targetGroup, similarities);
        float group2ToTarget = group2.similarityFromMatrix(targetGroup, similarities);

        // obliczenie podobienstwa od polaczonych grup 1 i 2 do docelowej
        if (group1ToTarget > group2ToTarget)
        {
            return group1ToTarget;
        }
        else
        {
            return group2ToTarget;
        }
    }
}
