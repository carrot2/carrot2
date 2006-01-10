
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

package com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram;


import java.util.LinkedList;
import java.util.List;


/**
 * @author Micha� Wr�blewski
 */
public abstract class DendrogramItem
{
    public abstract int getIndex();


    public abstract DendrogramNode add(DendrogramItem item, float simialrity);


    public float similarityFromMatrix(DendrogramItem targetGroup, float [][] similarities)
    {
        int targetIndex = targetGroup.getIndex();
        int groupIndex = getIndex();

        if (groupIndex > targetIndex)
        {
            return similarities[targetIndex][groupIndex];
        }
        else
        {
            return similarities[groupIndex][targetIndex];
        }
    }


    public abstract LinkedList getAllIndices();


    public abstract int size();


    public abstract String toString();


    public static String similaritiesToString(List groups, float [][] similarities)
    {
        StringBuffer result = new StringBuffer("");

        for (int i = 0; i < groups.size(); i++)
        {
            int group1 = ((DendrogramItem) groups.get(i)).getIndex();

            result.append("\n");

            for (int j = (i + 1); j < groups.size(); j++)
            {
                int group2 = ((DendrogramItem) groups.get(j)).getIndex();
                result.append(similarities[group1][group2]);
                result.append(" ");
            }
        }

        return result.toString();
    }
}
