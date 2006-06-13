
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

package fuzzyAnts;


import java.util.*;


/**
 * central class of the fuzzy ant based algorithm
 *
 * @author Steven Schockaert
 */
public class ListBordModel
    implements Constants
{
    protected ArrayList heaps;
    protected ArrayList objects;
    protected ArrayList ants;
    protected Random rand;
    protected FuzzyAntsParameters parameters;
    protected SimInterface docset;

    public ListBordModel(SimInterface docset, FuzzyAntsParameters parameters)
    {
        this.parameters = parameters;
        this.docset = docset;
        heaps = new ArrayList();
        objects = new ArrayList();
        ants = new ArrayList();
        rand = new Random();

        Set indices = docset.getIndices();

        for (Iterator it = indices.iterator(); it.hasNext();)
        {
            int i = ((Integer) it.next()).intValue();
            Document doc = new Document(i, this);
            heaps.add((new SimHeap(this)).add(doc));
            objects.add(doc);
        }

        addAnt();
    }

    /*
     * calculates a solution
     */
    public ArrayList getSolution()
    {
        int numberOfIterations  = parameters.getNumberOfIterations();
        skip(objects.size() * numberOfIterations);
        return getHeaps();
    }


    /*
     * Perform "n" iterations
     */
    private void skip(int n)
    {
        Ant m;

        for (int k = 0; k < n; k++)
        {
            for (ListIterator i = ants.listIterator(); i.hasNext();)
            {
                m = (Ant) i.next();
                m.move();
            }
        }
    }


    /*
     * Returns the heap with index "i"
     */
    public Heap getHeap(int i)
    {
        return (Heap) heaps.get(i);
    }


    /*
     * Take away 1 document from the heap with index "i". The document that is chosen is the one wose dissimilarity
     * with the centre of the heap is maximal
     */
    public Document takeDocument(int i)
    {
        Heap h = (Heap) heaps.get(i);
        Document doc = h.removeWorstDocument();

        if (h.getNumber() == 0)
        {
            heaps.remove(i);
        }

        return doc;
    }


    /*
     * Take away the heap with index "i" from the list of heaps
     */
    public Heap takeHeap(int i)
    {
        Heap h = (Heap) heaps.get(i);
        heaps.remove(i);

        return h;
    }


    /*
     * Add the document with index "i" to the heap "k"
     */
    public void drop(Heap k, int i)
    {
        Heap h = (Heap) heaps.get(i);
        h.addHeap(k);
    }


    /*
     * Add the heap "h" to the list of heaps
     */
    public void newHeap(Heap h)
    {
        heaps.add(h);
    }


    /*
     * returns the number of heaps in the list of heaps
     */
    public int numberOfListHeaps()
    {
        return heaps.size();
    }


    /*
     * Add another (fuzzy) ant to the population of (fuzzy) ants
     */
    protected void addAnt()
    {
        ants.add(new ListAnt(this, parameters));
    }


    /*
     * Returns the similarity between the documents with indices "i1" and "i2"
     */
    public double similarity(int i1, int i2)
    {
        return docset.similarity(i1, i2);
    }


    /*
     * Returns the leader value of the document with index "i"
     */
    public double leadervalue(int i)
    {
        return docset.leadervalue(i);
    }


    /*
     * Returns the number of documents
     */
    public int getTotalNumber()
    {
        return docset.getNumber();
    }


    /*
     * Returns a list with all heaps, including those carried by the ants
     */
    public ArrayList getHeaps()
    {
        ArrayList l = new ArrayList();
        l.addAll(heaps);
        l.addAll(getAntHeaps());

        return l;
    }


    /*
     * Returns a list with the heaps carried by the ants
     */
    private ArrayList getAntHeaps()
    {
        ArrayList res = new ArrayList();

        for (ListIterator it = ants.listIterator(); it.hasNext();)
        {
            ListAnt m = (ListAnt) it.next();
            res.add(m.getObject());
        }

        return res;
    }
}
