

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



/**
 * Representation of a document.
 *
 * @author Steven Schockaert
 */
class Document
{
    protected int index = -1;
    protected LijstBordModel bm;

    public Document(int index, LijstBordModel bm)
    {
        this.index = index;
        this.bm = bm;
    }

    public int geefIndex()
    {
        return index;
    }


    /*
     * Returns the similarity between this document and the document "d"
     */
    public double similariteit(Document d)
    {
        try
        {
            Document md = (Document) d;

            return bm.similariteit(index, md.index);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return -1;
        }
    }


    /*
     * Returns the dissimilarity between this document and the document "d"
     */
    public double afstand(Document d)
    {
        return 1 - similariteit(d);
    }


    public boolean equals(Object o)
    {
        if (!(o instanceof Document))
        {
            return false;
        }

        return ((Document) o).index == index;
    }
}
