

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.mwroblewski.carrot.filter.termsfilter.weighing;


/**
 * @author Micha� Wr�blewski
 */
public class TfWeighing
    implements TermsWeighing
{
    public float [][] weighTerms(int [][] tfs, int [] dfs)
    {
        float [][] termsWeights = new float[tfs.length][tfs[0].length];

        for (int i = 0; i < tfs.length; i++)
        {
            for (int j = 0; j < tfs[0].length; j++)
            {
                termsWeights[i][j] = tfs[i][j];
            }
        }

        return termsWeights;
    }
}
