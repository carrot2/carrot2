

/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listen in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 * 
 * Refer to full text of the licence "carrot2.LICENCE" in the root folder
 * of CVS checkout or at: 
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENCE
 */


package com.mwroblewski.carrot.filter.ahcfilter.groups;


import java.util.Comparator;


/**
 * @author Micha� Wr�blewski
 */
class GroupDescription
{
    int descriptionID;
    float weight;
    float occurrence;

    GroupDescription(int descriptionID, float weight, float occurrence)
    {
        this.descriptionID = descriptionID;
        this.weight = weight;
        this.occurrence = occurrence;
    }
}



class DescriptionWeightComparator
    implements Comparator
{
    // inverse order !!!
    public int compare(Object o1, Object o2)
        throws ClassCastException
    {
        GroupDescription d1 = (GroupDescription) o1;
        GroupDescription d2 = (GroupDescription) o2;

        if (d1.weight < d2.weight)
        {
            return 1;
        }
        else if (d1.weight == d2.weight)
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }


    public boolean equals(Object o1, Object o2)
        throws ClassCastException
    {
        GroupDescription d1 = (GroupDescription) o1;
        GroupDescription d2 = (GroupDescription) o2;

        return (d1.weight == d2.weight);
    }
}



class DescriptionOccurrenceComparator
    implements Comparator
{
    // inverse order !!!
    public int compare(Object o1, Object o2)
        throws ClassCastException
    {
        GroupDescription d1 = (GroupDescription) o1;
        GroupDescription d2 = (GroupDescription) o2;

        if (d1.occurrence < d2.occurrence)
        {
            return 1;
        }
        else if (d1.occurrence == d2.occurrence)
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }


    public boolean equals(Object o1, Object o2)
        throws ClassCastException
    {
        GroupDescription d1 = (GroupDescription) o1;
        GroupDescription d2 = (GroupDescription) o2;

        return (d1.occurrence == d2.occurrence);
    }
}
