

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
 * Extension of "Heap" which implements how to calculate the centre of a heap
 * @author Steven Schockaert
 */
class SimHeap
    extends Heap
{
    protected ListBordModel bm;
    protected Document centrum;

    public SimHeap(ListBordModel bm)
    {
        this.bm = bm;
        count = 0;
        documents = new ArrayList();
    }

    /*
     * The centre of the heap is the document whose leader value is greatest
     */
    public Document getCentrum()
    {
        if (centrum != null)
        {
            return centrum;
        }

        ListIterator it = documents.listIterator();
        centrum = (Document) it.next();

        double leadervalue = bm.leadervalue(centrum.getIndex());

        for (; it.hasNext();)
        {
            Document doc = (Document) it.next();
            double w = bm.leadervalue(doc.getIndex());

            if (leadervalue < w)
            {
                leadervalue = w;
                centrum = doc;
            }
        }

        return centrum;
    }


    public Heap add(Document k)
    {
        centrum = null;

        return super.add(k);
    }


    public Heap addHeap(Heap k)
    {
        centrum = null;

        return super.addHeap(k);
    }


    public Document removeWorstDocument()
    {
        centrum = null;

        return super.removeWorstDocument();
    }
}
