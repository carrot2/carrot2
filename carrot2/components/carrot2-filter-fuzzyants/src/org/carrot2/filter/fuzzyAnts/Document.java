
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.filter.fuzzyAnts;



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
            return bm.similarity(index, d.index);
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
