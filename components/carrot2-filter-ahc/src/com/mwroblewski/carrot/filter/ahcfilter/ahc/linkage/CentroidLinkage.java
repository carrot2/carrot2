
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

package com.mwroblewski.carrot.filter.ahcfilter.ahc.linkage;


import com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram.DendrogramItem;


/**
 * @author Micha� Wr�blewski
 */
public class CentroidLinkage
    implements LinkageMethod
{
    public float newSimilarity(
        DendrogramItem group1, DendrogramItem group2, DendrogramItem targetGroup,
        float [][] similarities
    )
    {
        float group1ToTarget = group1.similarityFromMatrix(targetGroup, similarities);
        float group2ToTarget = group2.similarityFromMatrix(targetGroup, similarities);
        float group1ToGroup2 = group1.similarityFromMatrix(group2, similarities);
        int size1 = group1.size();
        int size2 = group2.size();

        // calculating the similarities of clustered groups 1 and 2 to the
        // target group
        float a1 = size1 / (size1 + size2);
        float a2 = size2 / (size1 + size2);
        float result = (a1 * group1ToTarget) + (a2 * group2ToTarget);

        return (result - (a1 * a2 * group1ToGroup2));

        //return 0.5f * (group1ToTarget + group2ToTarget) - 0.25f * (group1ToGroup2);
    }
}
