
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

package com.mwroblewski.carrot;


import com.mwroblewski.carrot.filter.ahcfilter.ahc.AHC;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram.*;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.dendrogram.visualisation.DendrogramFrame;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.linkage.SingleLinkage;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.similarity.EuclideanSimilarity;
import com.mwroblewski.carrot.filter.ahcfilter.ahc.similarity.SimilarityMeasure;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;


/**
 * @author Michał Wróblewski
 */
public class AHCTest
{
    protected static float roundSimilarity(float similarity, float granularity)
    {
        int tmp = Math.round(similarity / granularity);

        return tmp * granularity;
    }


    protected static String toString(DendrogramNode node, float granularity, boolean link)
    {
        float simNode = roundSimilarity(node.getSimilarity(), granularity);

        StringBuffer result = new StringBuffer("");

        if (!link)
        {
            result.append("(");
        }

        DendrogramItem left = node.getLeft();
        DendrogramItem right = node.getRight();

        if (left instanceof DendrogramLeaf)
        {
            result.append(left.toString());
        }
        else
        {
            float simLeft = roundSimilarity(((DendrogramNode) left).getSimilarity(), granularity);

            result.append(toString((DendrogramNode) left, granularity, (simLeft == simNode)));
        }

        result.append(" ");

        if (right instanceof DendrogramLeaf)
        {
            result.append(right.toString());
        }
        else
        {
            float simRight = roundSimilarity(((DendrogramNode) right).getSimilarity(), granularity);

            result.append(toString((DendrogramNode) right, granularity, (simRight == simNode)));
        }

        if (!link)
        {
            result.append(")");
        }

        return result.toString();
    }


    public static void main(String [] args)
        throws Exception
    {
        Vector points = new Vector();

        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        String line;

        while ((line = br.readLine()) != null)
        {
            StringTokenizer s = new StringTokenizer(line);

            while (s.hasMoreTokens())
            {
                String ss = s.nextToken();

                if (ss.startsWith("#"))
                {
                    // if this line is a comment
                    break;
                }
                else
                {
                    points.add(ss);
                }
            }
        }

        float [][] data = new float[points.size() / 2][2];

        for (int i = 0; i < (points.size() / 2); i++)
        {
            data[i][0] = Float.parseFloat((String) points.elementAt(2 * i));
            data[i][1] = Float.parseFloat((String) points.elementAt((2 * i) + 1));
        }

        SimilarityMeasure sim = new EuclideanSimilarity();
        float [][] similarities = sim.calculateSimilarity(data);

        AHC ahc = new AHC(similarities, 0.0f, new SingleLinkage());
        LinkedList trees = ahc.group();

        new DendrogramFrame(
            trees, similarities, sim.minSimilarity(), sim.maxSimilarity(), 200, 200
        );

        System.out.println(toString((DendrogramNode) trees.get(0), 0.2f, false));
    }
}
