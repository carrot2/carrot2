

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
 * Extension of "Hoop" which implements how to calculate the centre of a heap
 * @author Steven Schockaert
 */
class SimHoop
    extends Hoop
{
    protected LijstBordModel bm;
    protected Document centrum;

    public SimHoop(LijstBordModel bm)
    {
        this.bm = bm;
        aantal = 0;
        documenten = new ArrayList();
    }

    /*
     * The centre of the heap is the document whose leader value is greatest
     */
    public Document geefCentrum()
    {
        if (centrum != null)
        {
            return centrum;
        }

        ListIterator it = documenten.listIterator();
        centrum = (Document) it.next();

        double leiderwaarde = bm.leiderwaarde(centrum.geefIndex());

        for (; it.hasNext();)
        {
            Document doc = (Document) it.next();
            double w = bm.leiderwaarde(doc.geefIndex());

            if (leiderwaarde < w)
            {
                leiderwaarde = w;
                centrum = doc;
            }
        }

        return centrum;
    }


    public Hoop add(Document k)
    {
        centrum = null;

        return super.add(k);
    }


    public Hoop addHoop(Hoop k)
    {
        centrum = null;

        return super.addHoop(k);
    }


    public Document verwijderVersteDocument()
    {
        centrum = null;

        return super.verwijderVersteDocument();
    }
}
