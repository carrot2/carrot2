

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
abstract class Hoop
{
    protected ArrayList documenten;
    protected int aantal;
    protected boolean changed = true;
    private int versteIndex = -1;
    private double gemAfst = -1;
    private double maxAfst = -1;

    public abstract Document geefCentrum();


    /*
     * add document "k" to the heap
     */
    public Hoop add(Document k)
    {
        documenten.add(k);
        aantal++;
        changed = true;

        return this;
    }


    /*
     * add all documents in "k" to the heap
     */
    public Hoop addHoop(Hoop k)
    {
        documenten.addAll(k.documenten);
        aantal += k.geefAantal();
        changed = true;

        return this;
    }


    /*
     * Remove the document that is the most dissimilar from the centre of the heap
     */
    public Document verwijderVersteDocument()
    {
        Document k = (Document) documenten.remove(geefVersteIndex());
        aantal--;
        changed = true;

        return k;
    }


    /*
     * Returns the document that is the most dissimilar from the centre of the heap
     */
    public Document geefVersteDocument()
    {
        return (Document) documenten.get(geefVersteIndex());
    }


    /*
     * Returns the index of the document that is the most dissimilar from the centre of the heap
     */
    protected int geefVersteIndex()
    {
        if (changed)
        {
            bepaalWaarden();
        }

        return versteIndex;
    }


    /*
     * Returns the similarity between the centre of this heap and the centre of "h"
     */
    public double similariteit(Hoop h)
    {
        return 1 - geefAfstandTotCentrum(h.geefCentrum());
    }


    /*
     * Returns the similarity between the centre of this heap and "k"
     */
    public double similariteit(Document d)
    {
        return 1 - geefAfstandTotCentrum(d);
    }


    /*
     * Calculate the centre, average similarity, maximal similarity ,...
     */
    private void bepaalWaarden()
    {
        try
        {
            if (aantal == 0)
            {
                throw new Exception("0 elementen");
            }

            Document centrum = geefCentrum();
            double maxAfstand = -1;
            double totAfstand = 0;
            int verste = -1;

            for (ListIterator i = documenten.listIterator(); i.hasNext();)
            {
                Document huidig = (Document) i.next();
                totAfstand += huidig.afstand(centrum);

                if (huidig.afstand(centrum) > maxAfstand)
                {
                    maxAfstand = huidig.afstand(centrum);
                    verste = i.previousIndex();
                }
            }

            maxAfst = maxAfstand;
            gemAfst = (totAfstand / aantal);
            versteIndex = verste;
            changed = false;
        }
        catch (Exception e)
        {
            System.err.println("fout in bepaalWaarden: " + e.toString());
        }
    }


    /*
     * Returns the maximal dissimilarity between a document of the heap and the centre of the heap
     */
    public double geefMaximumAfstand()
    {
        if (changed)
        {
            bepaalWaarden();
        }

        return maxAfst;
    }


    /*
     * Returns the minimal similarity between a document of the heap and the centre of the heap
     */
    public double geefMinimumSimilariteit()
    {
        return 1 - geefMaximumAfstand();
    }


    /*
     * Returns the average dissimilarity between a document of the heap and the centre of the heap
     */
    public double geefGemiddeldeAfstand()
    {
        if (changed)
        {
            bepaalWaarden();
        }

        return gemAfst;
    }


    /*
     * Returns the average similarity between a document of the heap and the centre of the heap
     */
    public double geefGemiddeldeSimilariteit()
    {
        return 1 - geefGemiddeldeAfstand();
    }


    /*
     * Returns the dissimilarity between the centre of this heap and "d"
     */
    public double geefAfstandTotCentrum(Document d)
    {
        Document c = geefCentrum();

        return d.afstand(c);
    }


    /*
     * returns is the heap is empty
     */
    public boolean isLeeg()
    {
        return aantal == 0;
    }


    /*
     * returns the number of documents in the heap
     */
    public int geefAantal()
    {
        return aantal;
    }


    /*
     * returns all documents in the heap
     */
    public ArrayList geefDocumenten()
    {
        return documenten;
    }
}
