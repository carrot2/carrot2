
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
    private double [] membership; //membership values
    private Set supp; //set representing the support of the fuzzy set

    /*
     * Constructor of a fuzzy number with support ]m1,m3[ and modal value m2
     */
    public FuzzyNumber(int basis, int m1, int m2, int m3)
    {
        this.basis = basis;
        membership = new double[basis];
        supp = new HashSet();

        if (m1 != m2)
        {
            for (int i = m1; i <= m2; i++)
            {
                membership[i] = ((double) (i - m1)) / (m2 - m1);
                supp.add(new Integer(i));
            }
        }
        else
        {
            membership[m1] = 1;
            supp.add(new Integer(m1));
        }

        if (m2 != m3)
        {
            for (int i = m2; i <= m3; i++)
            {
                membership[i] = ((double) (m3 - i)) / (m3 - m2);
                supp.add(new Integer(i));
            }
        }
        else
        {
            membership[m2] = 1;
            supp.add(new Integer(m2));
        }
    }


    public FuzzyNumber(int basis)
    {
        this.basis = basis;
        supp = new HashSet();
        membership = new double[basis];
    }


    public FuzzyNumber(FuzzyNumber f)
    {
        basis = f.basis;
        membership = new double[basis];
        supp = f.supp;

        for (Iterator it = supp.iterator(); it.hasNext();)
        {
            int i = ((Integer) it.next()).intValue();
            membership[i] = f.membership[i];
        }
    }


    public FuzzyNumber(FuzzyNumber f, double m)
    {
        basis = f.basis;
        membership = new double[basis];
        supp = f.supp;

        for (Iterator it = supp.iterator(); it.hasNext();)
        {
            int i = ((Integer) it.next()).intValue();
            membership[i] = Math.min(f.membership[i], m);
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
            membership[i] = Math.min(k, membership[i]);
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
                membership[i] = Math.max(membership[i], f.membership(i));
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
            teller += (membership[i] * i);
            noemer += membership[i];
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
    public double membership(int i)
    {
        try
        {
            if ((i >= basis) || (i < 0))
            {
                throw new Exception();
            }

            return membership[i];
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return -1;
        }
    }
}
