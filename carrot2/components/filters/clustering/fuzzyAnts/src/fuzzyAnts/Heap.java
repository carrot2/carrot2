

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
 * Represents a heap of objects of type "Document"
 *
 * @author Steven Schockaert
 */
abstract class Heap
{
    protected ArrayList documents;
    protected int count;
    protected boolean changed = true;
    private int worstIndex = -1;
    private double avgDissim = -1;
    private double maxDissim = -1;

    public abstract Document getCentrum();

    /*
     * add document "k" to the heap
     */
    public Heap add(Document k)
    {
        documents.add(k);
        count++;
        changed = true;

        return this;
    }


    /*
     * add all documents in "k" to the heap
     */
    public Heap addHeap(Heap k)
    {
        documents.addAll(k.documents);
        count += k.getNumber();
        changed = true;

        return this;
    }


    /*
     * Remove the document that is the most dissimilar from the centre of the heap
     */
    public Document removeWorstDocument()
    {
        Document k = (Document) documents.remove(getWorstIndex());
        count--;
        changed = true;

        return k;
    }


    /*
     * Returns the document that is the most dissimilar from the centre of the heap
     */
    public Document getWorstDocument()
    {
        return (Document) documents.get(getWorstIndex());
    }


    /*
     * Returns the index of the document that is the most dissimilar from the centre of the heap
     */
    protected int getWorstIndex()
    {
        if (changed)
        {
           getValues();
        }

        return worstIndex;
    }


    /*
     * Returns the similarity between the centre of this heap and the centre of "h"
     */
    public double similariteit(Heap h)
    {
        return 1 - getDissimilarityWithCentrum(h.getCentrum());
    }


    /*
     * Returns the similarity between the centre of this heap and "k"
     */
    public double similariteit(Document d)
    {
        return 1 - getDissimilarityWithCentrum(d);
    }


    /*
     * Calculate the centre, average similarity, maximal similarity ,...
     */
    private void getValues()
    {
        try
        {
            if (count == 0)
            {
                throw new Exception("0 elementen");
            }

            Document centrum = getCentrum();
            double maxDissimilarity = -1;
            double withDissimilarity = 0;
            int worst = -1;

            for (ListIterator i = documents.listIterator(); i.hasNext();)
            {
                Document huidig = (Document) i.next();
                withDissimilarity += huidig.dissimilarity(centrum);

                if (huidig.dissimilarity(centrum) > maxDissimilarity)
                {
                    maxDissimilarity = huidig.dissimilarity(centrum);
                    worst = i.previousIndex();
                }
            }

            maxDissim = maxDissimilarity;
            avgDissim = (withDissimilarity / count);
            worstIndex = worst;
            changed = false;
        }
        catch (Exception e)
        {
            System.err.println("fout in getValues: " + e.toString());
        }
    }


    /*
     * Returns the maximal dissimilarity between a document of the heap and the centre of the heap
     */
    public double getMaximumDissimilarity()
    {
        if (changed)
        {
            getValues();
        }

        return maxDissim;
    }


    /*
     * Returns the minimal similarity between a document of the heap and the centre of the heap
     */
    public double getMinimumSimilarity()
    {
        return 1 - getMaximumDissimilarity();
    }


    /*
     * Returns the average dissimilarity between a document of the heap and the centre of the heap
     */
    public double getAverageDissimilarity()
    {
        if (changed)
        {
            getValues();
        }

        return avgDissim;
    }


    /*
     * Returns the average similarity between a document of the heap and the centre of the heap
     */
    public double getAverageSimilarity()
    {
        return 1 - getAverageDissimilarity();
    }


    /*
     * Returns the dissimilarity between the centre of this heap and "d"
     */
    public double getDissimilarityWithCentrum(Document d)
    {
        Document c = getCentrum();

        return d.dissimilarity(c);
    }


    /*
     * returns is the heap is empty
     */
    public boolean isLeeg()
    {
        return count == 0;
    }


    /*
     * returns the number of documents in the heap
     */
    public int getNumber()
    {
        return count;
    }


    /*
     * returns all documents in the heap
     */
    public ArrayList getDocuments()
    {
        return documents;
    }
}
