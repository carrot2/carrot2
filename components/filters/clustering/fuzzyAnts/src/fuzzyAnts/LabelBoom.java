

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
 * datastructure used for implementing a softer notion of completion in the sense of Zhang and Dong
 * this is in fact a variant of a Suffix Tree
 *
 * @author Steven Schockaert
 */
import java.util.*;


public class LabelBoom
{
    int aantal;
    HashMap nodes;

    public LabelBoom()
    {
        aantal = 0;
        nodes = new HashMap();
    }

    /*
     * Add a sequence of terms
     */
    public void add(List termen)
    {
        aantal++;

        if (termen.size() > 0)
        {
            String t = (String) termen.get(0);
            termen.remove(0);

            if (nodes.containsKey(t))
            {
                LabelBoom sub = (LabelBoom) nodes.get(t);
                sub.add(termen);
            }
            else
            {
                LabelBoom sub = new LabelBoom();
                sub.add(termen);
                nodes.put(t, sub);
            }
        }
    }


    public int geefAantal()
    {
        return aantal;
    }


    /*
     * Returns the longest sequence of terms that occured at least 0.75* "totaalAantal" times
     */
    public String geefCompleet(int totaalAantal)
    {
        if (aantal > Math.max(0.75 * totaalAantal, 3))
        {
            for (Iterator it = nodes.keySet().iterator(); it.hasNext();)
            {
                String t = (String) it.next();
                LabelBoom sub = (LabelBoom) nodes.get(t);

                if (sub.geefAantal() > (0.60 * totaalAantal))
                {
                    return (t + " " + sub.geefCompleet(totaalAantal));
                }
            }
        }

        return "";
    }


    /*
     * Returns the longest sequence of terms that occured at least 0.75* "totaalAantal" times in reverse order
     */
    public String geefReverseCompleet(int totaalAantal)
    {
        if (aantal > Math.max(0.75 * totaalAantal, 3))
        {
            for (Iterator it = nodes.keySet().iterator(); it.hasNext();)
            {
                String t = (String) it.next();
                LabelBoom sub = (LabelBoom) nodes.get(t);

                if (sub.geefAantal() > (0.60 * totaalAantal))
                {
                    return (sub.geefReverseCompleet(totaalAantal) + " " + t);
                }
            }
        }

        return "";
    }
}
