/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.stachoodev.carrot.filter.lingo.model;

/**
 * Calculates values of the IDF formula. Two different IDF variants are
 * implemented, see {@link #classic} and {@link #linear}.
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public abstract class IdfFormula
{
    /**
     * Classic idf formula: idf = log(N/df)
     */
    public static IdfFormula classic = new ClassicTfIdfFormula();

    /**
     * Linear idf formula: idf = (N - df) / (N - 1)
     */
    public static IdfFormula linear = new LinearTfIdfFormula();

    /**
     * Computes the IDF formula for given document frequency and the total
     * number of documents.
     * 
     * @param df document frequency
     * @param N total number of documents
     * @return IDF
     */
    public abstract double compute(double df, double N);

    /**
     * @author stachoo
     */
    public static class ClassicTfIdfFormula extends IdfFormula
    {
        /*
         * (non-Javadoc)
         * 
         * @see com.stachoodev.carrot.filter.lingo.model.IdfFormula#compute(double,
         *      double, double)
         */
        public double compute(double df, double N)
        {
            return Math.log(N / df);
        }
    }

    /**
     * @author stachoo
     */
    public static class LinearTfIdfFormula extends IdfFormula
    {
        /*
         * (non-Javadoc)
         * 
         * @see com.stachoodev.carrot.filter.lingo.model.IdfFormula#compute(double,
         *      double, double)
         */
        public double compute(double df, double N)
        {
            if (N == 1)
            {
                return (N - df);
            }
            else
            {
                return (N - df) / (N - 1);
            }
        }
    }
}