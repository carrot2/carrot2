

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


import java.util.*;


/**
 * Represents a set of documents. An instance of this class is used by the fuzzy ant based clustering algorithm. This
 * allows to flexible change the documents that should be clustered (without problems with different index numbers) and
 * to define the similarity measure that should be used.
 *
 * @author Steven Schockaert
 */
public class DocumentSet
    implements SimInterface
{
    Set indices;
    SnippetParser parser;

    //Used for "caching" similarity values and leader values 
    HashMap sim;
    HashMap leider;

    public DocumentSet(Set indices, SnippetParser parser)
    {
        this.indices = indices;
        this.parser = parser;
        sim = new HashMap();
        leider = new HashMap();
    }

    /*
     * Calculates the leader value of a document. This is used to determine the centre of a heap in
     * the fuzzy ant based clustering algorithm: the document with highest leader value is chosen as
     * the centre.
     */
    public double leiderwaarde(int i)
    {
        if (leider.containsKey(new Integer(i)))
        {
            return ((Double) leider.get(new Integer(i))).doubleValue();
        }

        Map doc = parser.geefDocument(i);
        double som = 0;

        for (Iterator it = doc.keySet().iterator(); it.hasNext();)
        {
            int termIndex = ((Integer) it.next()).intValue();
            double termWaarde = ((Double) doc.get(new Integer(termIndex))).doubleValue();
            double leiderWaarde = parser.leiderwaarde(termIndex);
            som += (termWaarde * leiderWaarde);
        }

        leider.put(new Integer(i), new Double(som));

        return som;
    }


    /*
     * returns the similarity between the documents with indices i and j
     */
    public double similariteit(int i, int j)
    {
        try
        {
            if (sim.containsKey(new Paar(i, j)))
            {
                Object o = sim.get(new Paar(i, j));
                Double d = (Double) o;
                double w = d.doubleValue();

                return w;
            }
            else
            {
                double teller = parser.somRuwDocMin(i, j);
                double w = 0;

                if (teller < 0.00001)
                {
                    w = 0;
                }
                else
                {
                    double noemer = parser.somRuwDoc(i);
                    w = teller / noemer;
                }

                Double d = new Double(w);
                sim.put(new Paar(i, j), d);

                return w;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return 0;
        }
    }


    /*
     * returns a set containing the indices of all the documents in this "Document Set".
     *
     */
    public Set geefIndices()
    {
        return indices;
    }


    /*
     * returns the number of documents in this "Document Set"
     */
    public int geefAantal()
    {
        return indices.size();
    }

    /*
     * Used for caching (objects of this class are stored in the HashMaps "sim" and "leider"
     */
    class Paar
    {
        int index1;
        int index2;

        public Paar(int index1, int index2)
        {
            this.index1 = index1;
            this.index2 = index2;
        }

        public boolean equals(Object o)
        {
            if (!(o instanceof Paar))
            {
                return false;
            }

            Paar p = (Paar) o;

            return ((index1 == p.index1) && (index2 == p.index2));
        }


        public int hashCode()
        {
            return (index1 * indices.size()) + index2;
        }
    }
}
