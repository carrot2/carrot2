

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the license "carrot2.LICENSE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */


package fuzzyAnts;


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
    Map parameters;

    public Ant(Map parameters)
    {
        this.parameters = parameters;
        rand = new Random();
        n1 = Integer.parseInt((String) ((LinkedList) parameters.get("n1")).get(0));
        m1 = Integer.parseInt((String) ((LinkedList) parameters.get("m1")).get(0));
        n2 = Integer.parseInt((String) ((LinkedList) parameters.get("n2")).get(0));
        m2 = Integer.parseInt((String) ((LinkedList) parameters.get("m2")).get(0));
    }


    public Ant(Ant m)
    {
        parameters = m.parameters;
        rand = new Random();
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
