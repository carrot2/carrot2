

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
 * central class of the fuzzy ant based algorithm
 *
 * @author Steven Schockaert
 */
public class LijstBordModel
    implements Constants
{
    protected ArrayList hopen;
    protected ArrayList objecten;
    protected ArrayList mieren;
    protected Random rand;
    protected Map parameters;
    protected SimInterface docset;

    public LijstBordModel(SimInterface docset, Map parameters)
    {
        this.parameters = parameters;
        this.docset = docset;
        hopen = new ArrayList();
        objecten = new ArrayList();
        mieren = new ArrayList();
        rand = new Random();

        Set indices = docset.geefIndices();

        for (Iterator it = indices.iterator(); it.hasNext();)
        {
            int i = ((Integer) it.next()).intValue();
            Document doc = new Document(i, this);
            hopen.add((new SimHoop(this)).add(doc));
            objecten.add(doc);
        }

        addMier();
    }

    /*
     * calculates a solution
     */
    public ArrayList berekenOplossing()
    {
        int aantalstappen = Integer.parseInt(
                (String) ((LinkedList) parameters.get("aantalstappen")).get(0)
            );
        skip(objecten.size() * aantalstappen);

        return geefHopen();
    }


    /*
     * Perform "n" iterations
     */
    private void skip(int n)
    {
        Mier m;

        for (int k = 0; k < n; k++)
        {
            for (ListIterator i = mieren.listIterator(); i.hasNext();)
            {
                m = (Mier) i.next();
                m.move();
            }
        }
    }


    /*
     * Returns the heap with index "i"
     */
    public Hoop geefHoop(int i)
    {
        return (Hoop) hopen.get(i);
    }


    /*
     * Take away 1 document from the heap with index "i". The document that is chosen is the one wose dissimilarity
     * with the centre of the heap is maximal
     */
    public Document takeDocument(int i)
    {
        Hoop h = (Hoop) hopen.get(i);
        Document doc = h.verwijderVersteDocument();

        if (h.geefAantal() == 0)
        {
            hopen.remove(i);
        }

        return doc;
    }


    /*
     * Take away the heap with index "i" from the list of heaps
     */
    public Hoop takeHoop(int i)
    {
        Hoop h = (Hoop) hopen.get(i);
        hopen.remove(i);

        return h;
    }


    /*
     * Add the document with index "i" to the heap "k"
     */
    public void drop(Hoop k, int i)
    {
        Hoop h = (Hoop) hopen.get(i);
        h.addHoop(k);
    }


    /*
     * Add the heap "h" to the list of heaps
     */
    public void nieuweHoop(Hoop h)
    {
        hopen.add(h);
    }


    /*
     * returns the number of heaps in the list of heaps
     */
    public int aantalLijstHopen()
    {
        return hopen.size();
    }


    /*
     * Add another (fuzzy) ant to the population of (fuzzy) ants
     */
    protected void addMier()
    {
        mieren.add(new LijstMier(this, parameters));
    }


    /*
     * Returns the similarity between the documents with indices "i1" and "i2"
     */
    public double similariteit(int i1, int i2)
    {
        return docset.similariteit(i1, i2);
    }


    /*
     * Returns the leader value of the document with index "i"
     */
    public double leiderwaarde(int i)
    {
        return docset.leiderwaarde(i);
    }


    /*
     * Returns the number of documents
     */
    public int geefTotaalAantal()
    {
        return docset.geefAantal();
    }


    /*
     * Returns a list with all heaps, including those carried by the ants
     */
    public ArrayList geefHopen()
    {
        ArrayList l = new ArrayList();
        l.addAll(hopen);
        l.addAll(geefMierHopen());

        return l;
    }


    /*
     * Returns a list with the heaps carried by the ants
     */
    private ArrayList geefMierHopen()
    {
        ArrayList res = new ArrayList();

        for (ListIterator it = mieren.listIterator(); it.hasNext();)
        {
            LijstMier m = (LijstMier) it.next();
            res.add(m.getObject());
        }

        return res;
    }
}
