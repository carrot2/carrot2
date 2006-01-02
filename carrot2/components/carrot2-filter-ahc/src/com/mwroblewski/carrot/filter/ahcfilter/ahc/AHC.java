
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
package com.mwroblewski.carrot.filter.ahcfilter.ahc;


import com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram.DendrogramItem;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram.DendrogramLeaf;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.linkage.LinkageMethod;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.stop.StopCondition;
import org.apache.log4j.Logger;
import java.util.LinkedList;


/**
 * @author Micha� Wr�blewski
 */
public class AHC
{
    private final Logger log = Logger.getLogger(this.getClass());
    protected float [][] similarities;
    protected float minSimilarity;
    protected LinkageMethod linkageMethod;
    protected StopCondition stopCondition;
    protected LinkedList groups;

    protected class Pair
    {
        int leftItem;
        int rightItem;
        float similarity;

        public Pair(int leftItem, int rightItem, float similarity)
        {
            this.leftItem = leftItem;
            this.rightItem = rightItem;
            this.similarity = similarity;
        }
    }

    public AHC(float [][] similarities, float minSimilarity, LinkageMethod linkageMethod)
    {
        this.similarities = similarities;
        this.minSimilarity = minSimilarity;
        this.linkageMethod = linkageMethod;
    }


    public AHC(
        float [][] similarities, float minSimilarity, LinkageMethod linkageMethod,
        StopCondition stopCondition
    )
    {
        this.similarities = similarities;
        this.minSimilarity = minSimilarity;
        this.linkageMethod = linkageMethod;
        this.stopCondition = stopCondition;
    }

    protected boolean finish(Pair mostSimilarPair)
    {
        if (groups.size() < 2)
        {
            return true;
        }
        else
        {
            if (stopCondition == null)
            {
                return false;
            }
            else
            {
                return stopCondition.finish(groups, mostSimilarPair.similarity);
            }
        }
    }


    protected Pair findMostSimilarPair()
    {
        int iMax = 0;
        int jMax = 0;
        DendrogramItem newGroup;
        DendrogramItem oldGroup;
        float simMax = minSimilarity;

        for (int i = 0; i < groups.size(); i++)
        {
            newGroup = (DendrogramItem) groups.get(i);

            for (int j = (i + 1); j < groups.size(); j++)
            {
                oldGroup = (DendrogramItem) groups.get(j);

                float similarity = newGroup.similarityFromMatrix(oldGroup, similarities);

                // we need >= in order to find anything !!! (in case if
                // similarity of some pair = minSimilarity)
                if (similarity >= simMax)
                {
                    simMax = similarity;
                    iMax = i;
                    jMax = j;
                }
            }
        }

        return new Pair(iMax, jMax, simMax);
    }


    public LinkedList group()
    {
        // placing each document in a separate group
        groups = new LinkedList();

        for (int i = 0; i < similarities.length; i++)
        {
            groups.add(new DendrogramLeaf(i));
        }

        Pair mostSimilarPair;

        // main loop of the algorithm
        while (!finish(mostSimilarPair = findMostSimilarPair()))
        {
            // linking 2 most similar groups
            int iMax = mostSimilarPair.leftItem;
            int jMax = mostSimilarPair.rightItem;
            float simMax = mostSimilarPair.similarity;

            DendrogramItem newGroup = (DendrogramItem) groups.get(iMax);
            DendrogramItem oldGroup = (DendrogramItem) groups.get(jMax);
            int newIndex = newGroup.getIndex();

            groups.set(iMax, newGroup.add(oldGroup, simMax));
            groups.remove(jMax);

            log.debug("Linked " + newGroup + " with: " + oldGroup + " with sim: " + simMax);
            log.debug("groups after linking: ");
            log.debug(groups);

            // recalculating of similarities of all clusters to newGroup
            // (but only the part of the array above the diagonal)
            for (int i = 0; i < groups.size(); i++)
            {
                if (i != iMax)
                {
                    DendrogramItem currentGroup = (DendrogramItem) groups.get(i);
                    int currentIndex = currentGroup.getIndex();
                    float newSim;

                    newSim = linkageMethod.newSimilarity(
                            newGroup, oldGroup, currentGroup, similarities
                        );

                    if (newIndex > currentIndex)
                    {
                        similarities[currentIndex][newIndex] = newSim;
                    }
                    else
                    {
                        similarities[newIndex][currentIndex] = newSim;
                    }
                }
            }

            // cancelled logging, because it took too much time even when
            // log status was higher than DEBUG (evaluating the expression
            // took about 80-90 % of total time of AHC algorithm)
            //log.debug("similarities after linking : ");
            //log.debug(DendrogramItem.similaritiesToString(groups, similarities));
        }

        return groups;
    }
}
