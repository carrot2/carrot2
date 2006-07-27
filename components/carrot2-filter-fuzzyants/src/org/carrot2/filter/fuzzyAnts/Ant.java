
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

package org.carrot2.filter.fuzzyAnts;


import java.util.*;


/**
 * Abstract representation of an ant
 * @author Steven Schockaert
 */
abstract class Ant
    implements Constants
{
    protected int n1;
    protected int m1;
    protected int n2;
    protected int m2;
    Random rand;

    public Ant(FuzzyAntsParameters parameters)
    {
        rand = new Random();
        n1 = parameters.getN1();
        m1 = parameters.getM1();
        n2 = parameters.getN2();
        m2 = parameters.getM2();
    }

    /*
     * Used for inference of fuzzy rules with 2 antecedents
     */
    public static void infer(
        FuzzyNumber f1, int w1, FuzzyNumber f2, int w2, FuzzyNumber f3, FuzzyNumber res
    )
    {
        if ((f1.membership(w1) > 0) && (f2.membership(w2) > 0))
        {
            FuzzyNumber temp = new FuzzyNumber(
                    f3, Math.min(f1.membership(w1), f2.membership(w2))
                );
            res.max(temp);
        }
    }


    /*
     * Used for inference of the fuzzy rules with 1 antecedent
     */
    public static void infer(FuzzyNumber f1, int w1, FuzzyNumber f2, FuzzyNumber res)
    {
        if (f1.membership(w1) > 0)
        {
            FuzzyNumber temp = new FuzzyNumber(f2, f1.membership(w1));
            res.max(temp);
        }
    }


    public abstract void move();
}
