

/*
 * Carrot2 Project
 * Copyright (C) 2002-2004, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.mwroblewski.carrot.filter.ahcfilter.ahc.stop;


import java.util.LinkedList;


/**
 * @author Micha� Wr�blewski
 */
public class GroupingThresholdReachedCondition
    implements StopCondition
{
    protected float treeCreatingThreshold;

    public GroupingThresholdReachedCondition(float treeCreatingThreshold)
    {
        this.treeCreatingThreshold = treeCreatingThreshold;
    }

    public boolean finish(LinkedList groups, float simMax)
    {
        if (simMax < treeCreatingThreshold)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
