

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
    HashMap leader;

    public DocumentSet(Set indices, SnippetParser parser)
    {
        this.indices = indices;
        this.parser = parser;
        sim = new HashMap();
        leader = new HashMap();
    }

    /*
     * Calculates the leader value of a document. This is used to determine the centre of a heap in
     * the fuzzy ant based clustering algorithm: the document with highest leader value is chosen as
     * the centre.
     */
    public double leadervalue(int i)
    {
        if (leader.containsKey(new Integer(i)))
        {
            return ((Double) leader.get(new Integer(i))).doubleValue();
        }

        Map doc = parser.getDocument(i);
        double sum = 0;

        for (Iterator it = doc.keySet().iterator(); it.hasNext();)
        {
            int termIndex = ((Integer) it.next()).intValue();
            double termValue = ((Double) doc.get(new Integer(termIndex))).doubleValue();
            double leaderValue = parser.leadervalue(termIndex);
            sum += (termValue * leaderValue);
        }

        leader.put(new Integer(i), new Double(sum));

        return sum;
    }


    /*
     * returns the similarity between the documents with indices i and j
     */
    public double similarity(int i, int j)
    {
        try
        {
            if (sim.containsKey(new Pair(i, j)))
            {
                Object o = sim.get(new Pair(i, j));
                Double d = (Double) o;
                double w = d.doubleValue();

                return w;
            }
            else
            {
                double numerator = parser.sumRoughDocMin(i, j);
                double w = 0;

                if (numerator < 0.00001)
                {
                    w = 0;
                }
                else
                {
                    double denominator = parser.sumRoughDoc(i);
                    w = numerator / denominator;
                }

                Double d = new Double(w);
                sim.put(new Pair(i, j), d);

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
    public Set getIndices()
    {
        return indices;
    }


    /*
     * returns the number of documents in this "Document Set"
     */
    public int getNumber()
    {
        return indices.size();
    }

    /*
     * Used for caching (objects of this class are stored in the HashMaps "sim" and "leader"
     */
    class Pair
    {
        int index1;
        int index2;

        public Pair(int index1, int index2)
        {
            this.index1 = index1;
            this.index2 = index2;
        }

        public boolean equals(Object o)
        {
            if (!(o instanceof Pair))
            {
                return false;
            }

            Pair p = (Pair) o;

            return ((index1 == p.index1) && (index2 == p.index2));
        }


        public int hashCode()
        {
            return (index1 * indices.size()) + index2;
        }
    }
}
