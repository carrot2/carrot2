

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
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
