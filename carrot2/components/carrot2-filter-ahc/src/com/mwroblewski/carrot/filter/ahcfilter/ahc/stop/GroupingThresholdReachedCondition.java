
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
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
