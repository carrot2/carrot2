

/*
 * Carrot2 Project
 * Copyright (C) 2002-2003, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package fuzzyAnts;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Represents a Fuzzy Number, i.e. a fuzzy set in the set of real numbers. In this implementation the domain of the
 * fuzzy number is the set of natural numbers instead .
 *
 * @author Steven Schockaert
 */
public class FuzzyNumber
{
    private int basis; // support is a subset of [0,basis-1]
    private double [] lidmaatschap; //membership values
    private Set supp; //set representing the support of the fuzzy set

    /*
     * Constructor of a fuzzy number with support ]m1,m3[ and modal value m2
     */
    public FuzzyNumber(int basis, int m1, int m2, int m3)
    {
        this.basis = basis;
        lidmaatschap = new double[basis];
        supp = new HashSet();

        if (m1 != m2)
        {
            for (int i = m1; i <= m2; i++)
            {
                lidmaatschap[i] = ((double) (i - m1)) / (m2 - m1);
                supp.add(new Integer(i));
            }
        }
        else
        {
            lidmaatschap[m1] = 1;
            supp.add(new Integer(m1));
        }

        if (m2 != m3)
        {
            for (int i = m2; i <= m3; i++)
            {
                lidmaatschap[i] = ((double) (m3 - i)) / (m3 - m2);
                supp.add(new Integer(i));
            }
        }
        else
        {
            lidmaatschap[m2] = 1;
            supp.add(new Integer(m2));
        }
    }


    public FuzzyNumber(int basis)
    {
        this.basis = basis;
        supp = new HashSet();
        lidmaatschap = new double[basis];
    }


    public FuzzyNumber(FuzzyNumber f)
    {
        basis = f.basis;
        lidmaatschap = new double[basis];
        supp = f.supp;

        for (Iterator it = supp.iterator(); it.hasNext();)
        {
            int i = ((Integer) it.next()).intValue();
            lidmaatschap[i] = f.lidmaatschap[i];
        }
    }


    public FuzzyNumber(FuzzyNumber f, double m)
    {
        basis = f.basis;
        lidmaatschap = new double[basis];
        supp = f.supp;

        for (Iterator it = supp.iterator(); it.hasNext();)
        {
            int i = ((Integer) it.next()).intValue();
            lidmaatschap[i] = Math.min(f.lidmaatschap[i], m);
        }
    }

    /*
     * "cut off" the membership values
     */
    public void min(double k)
    {
        for (Iterator it = supp.iterator(); it.hasNext();)
        {
            int i = ((Integer) it.next()).intValue();
            lidmaatschap[i] = Math.min(k, lidmaatschap[i]);
        }
    }


    /*
     * Change the membership values as the maximum of the current membership value en the memebership value in "f"
     */
    public void max(FuzzyNumber f)
    {
        try
        {
            if (f.getBasis() != basis)
            {
                throw new Exception();
            }

            supp.addAll(f.supp);

            for (Iterator it = supp.iterator(); it.hasNext();)
            {
                int i = ((Integer) it.next()).intValue();
                lidmaatschap[i] = Math.max(lidmaatschap[i], f.lidmaatschap(i));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /*
     * Defuzzification using COG
     */
    public int defuzzification()
    {
        double teller = 0;
        double noemer = 0;

        for (Iterator it = supp.iterator(); it.hasNext();)
        {
            int i = ((Integer) (it.next())).intValue();
            teller += (lidmaatschap[i] * i);
            noemer += lidmaatschap[i];
        }

        return (int) (teller / noemer);
    }


    public int getBasis()
    {
        return basis;
    }


    /*
     * Membership value for "i"
     */
    public double lidmaatschap(int i)
    {
        try
        {
            if ((i >= basis) || (i < 0))
            {
                throw new Exception();
            }

            return lidmaatschap[i];
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return -1;
        }
    }
}
