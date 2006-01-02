
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package fuzzyAnts;



/**
 * datastructure used for implementing a softer notion of completion in the sense of Zhang and Dong
 * this is in fact a variant of a Suffix Tree
 *
 * @author Steven Schockaert
 */
import java.util.*;


public class LabelTree
{
    int count;
    HashMap nodes;

    public LabelTree()
    {
        count = 0;
        nodes = new HashMap();
    }

    /*
     * Add a sequence of terms
     */
    public void add(List terms)
    {
        count++;

        if (terms.size() > 0)
        {
            String t = (String) terms.get(0);
            terms.remove(0);

            if (nodes.containsKey(t))
            {
                LabelTree sub = (LabelTree) nodes.get(t);
                sub.add(terms);
            }
            else
            {
                LabelTree sub = new LabelTree();
                sub.add(terms);
                nodes.put(t, sub);
            }
        }
    }


    public int getNumber()
    {
        return count;
    }


    /*
     * Returns the longest sequence of terms that occured at least 0.75* "totalNumber" times
     */
    public String getComplete(int totalNumber)
    {
        if (count > Math.max(0.75 * totalNumber, 3))
        {
            for (Iterator it = nodes.keySet().iterator(); it.hasNext();)
            {
                String t = (String) it.next();
                LabelTree sub = (LabelTree) nodes.get(t);

                if (sub.getNumber() > (0.60 * totalNumber))
                {
                    return (t + " " + sub.getComplete(totalNumber));
                }
            }
        }

        return "";
    }


    /*
     * Returns the longest sequence of terms that occured at least 0.75* "totalNumber" times in reverse order
     */
    public String getReverseComplete(int totalNumber)
    {
        if (count > Math.max(0.75 * totalNumber, 3))
        {
            for (Iterator it = nodes.keySet().iterator(); it.hasNext();)
            {
                String t = (String) it.next();
                LabelTree sub = (LabelTree) nodes.get(t);

                if (sub.getNumber() > (0.60 * totalNumber))
                {
                    return (sub.getReverseComplete(totalNumber) + " " + t);
                }
            }
        }

        return "";
    }
}
