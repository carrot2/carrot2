

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



/**
 * Representation of a document.
 *
 * @author Steven Schockaert
 */
class Document
{
    protected int index = -1;
    protected ListBordModel bm;

    public Document(int index, ListBordModel bm)
    {
        this.index = index;
        this.bm = bm;
    }

    public int getIndex()
    {
        return index;
    }


    /*
     * Returns the similarity between this document and the document "d"
     */
    public double similarity(Document d)
    {
        try
        {
            Document md = (Document) d;

            return bm.similarity(index, md.index);
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
    public double dissimilarity(Document d)
    {
        return 1 - similarity(d);
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
