

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


package com.mwroblewski.carrot.filter.termsfilter.weighing;


/**
 * @author Micha� Wr�blewski
 */
public class RootTfLinearIdfWeighing
    implements TermsWeighing
{
    public float [][] weighTerms(int [][] tfs, int [] dfs)
    {
        float [][] termsWeights = new float[tfs.length][tfs[0].length];

        // calculating inverse document frequencies
        float [] idfs = new float[dfs.length];

        for (int i = 0; i < idfs.length; i++)
        {
            idfs[i] = 1.0f - ((float) (dfs[i] - 1) / (tfs[0].length - 1));
        }

        for (int i = 0; i < tfs.length; i++)
        {
            for (int j = 0; j < tfs[0].length; j++)
            {
                termsWeights[i][j] = idfs[i] * (float) Math.sqrt(tfs[i][j]);
            }
        }

        return termsWeights;
    }
}
